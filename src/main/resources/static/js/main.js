//We have to pollute the global scope here or else the call back from google maps
//doesn't work
var map,
  bounds,
  geocoder,
  settings = {
    ukCentre: {lat: 54.152141, lng: -3.032227},
    isMobile: $(window).width() < 760
  };

function initMap() {
  bounds = new google.maps.LatLngBounds();
  geocoder = new google.maps.Geocoder();

  $(function () {
    //Load the map
    map = new google.maps.Map(document.getElementById("map"), {
      center: settings.ukCentre,
      zoom: 7,
      mapTypeControl: false
    });
    initAutocomplete();

    return false;
  });
}

function initAutocomplete() {
  new google.maps.places.Autocomplete(document.getElementById("newFrom"),
    {
      componentRestrictions: {
        country: "gb"
      }
    });
}

(function () {
  "use strict";

  //Make sure we have our dependencies
  if (!window.jQuery || !window.Mustache || !typeof($.fn.popover)) {
    window.alert("There was an error loading the page");
    return;
  }

  $(function () {
    var personId = 0,
      people = {},
      poiMarkers = [],
      infowindows = [],
      haveNewValue = false,
      errorMessages = {
        UNKNOWN: "Sorry, an unknown error occurred.",
        NOT_ENOUGH_PEOPLE: "Ooops!  You need at least two people to MeetUpInTheMiddle!",
        MISSING_NAME: "Ooops!  Looks like you missed out a name!",
        MISSING_FROM: "Ooops!  We need to know where everyone is travelling from!",
        MISSING_OR_INVALID_POI_TYPE: "Ooops!  Please tell us what kind of place you are looking to meet at!",
        MISSING_OR_INVALID_TRANSPORT_MODE: "Ooops!  Please tell us how everyone is travelling in!",
        UNKNOWN_LOCATION: "Ooops!  We can't find one or more of your locations."
      },
      templates = {};

    var $infoWindowTemplateNode = $("#js-infowindow-template");
    templates["infowindow"] = $infoWindowTemplateNode ? $infoWindowTemplateNode.html() : false;

    var $errorTemplateNode = $("#js-error-template");
    templates["error"] = $errorTemplateNode ? $errorTemplateNode.html() : false;

    var $tableRowTemplateNode = $("#js-tablerow-template");
    templates["tableRow"] = $tableRowTemplateNode ? $tableRowTemplateNode.html() : false;

    if (!templates.error || !templates.infowindow || !templates.tableRow) {
      window.alert("An error occurred loading the page");
      return;
    }

    var $searchOverlayContent = $("#searchoverlayContent"),
      $newPersonForm = $("#newPersonForm"),
      $newName = $("#newName", $newPersonForm),
      $newFrom = $("#newFrom", $newPersonForm),
      $newMode = $("#newMode", $newPersonForm),
      $addPersonBtn = $("#add-person", $newPersonForm),
      $peopleTable = $("#peopleTable", $newPersonForm),
      $submitButton = $("#submitButton"),
      $overlay = $("#overlay"),
      $grip = $("#grip", $overlay),
      $errorSection = $("#error-section");

    $newName.focus();

    //If both the name and location field have text in them, enable the add person button
    $newName.add($newFrom).on("change keyup", function () {
      haveNewValue = $.trim($newName.val()).length > 0
        && $.trim($newFrom.val()).length > 0;

      if (haveNewValue) {
        $addPersonBtn.removeClass("disabled");
      }
    });

    /**
     * When the add person button is clicked, if we have new entries in the form,
     * we geocode the location add a map pin and then add a person to the table.
     *
     * If we now have enough people in the table, we enable the submit button.
     */
    function addPerson() {
      if (!haveNewValue) {
        return;
      }

      var name = $newName.val();
      var location = $newFrom.val();
      var mode = $newMode.val();
      //How to display it to the user
      var prettyMode = $newMode.find("option[value=" + mode + "]").text();

      geocoder.geocode({"address": location}, function (results, status) {
        if (status === google.maps.GeocoderStatus.OK) {
          var latLng = results[0].geometry.location;
          new Person(name, location, latLng, mode, prettyMode);

          $newPersonForm[0].reset();
          haveNewValue = false;
          $addPersonBtn.addClass("disabled");

          if (personId >= 2) {
            $submitButton.prop("disabled", false);
          }
        } else {
          //TODO if the geo-coded location is not in the UK - give a sensible error.  This needs doing on the backend too.
          addError("UNKNOWN_LOCATION");
        }
      });
    }

    $addPersonBtn.click(addPerson);

    $(".person-form-control", $newPersonForm).keyup(function (e) {
      if (e.which == 13) { //enter
        addPerson();
        $newName.focus();
      }
    });

    /**
     * When we click the x next to a row in the table,
     * we need to remove the row from the table,
     * remove the pin from the map and from the JSON object that we
     * are building up to send to the backend.
     */
    $(document.body).on("click", ".removePerson", function (e) {
      var idToRemove = e.target.id.replace(/remove\[(\d)\]/, "$1");
      people[idToRemove].person.remove();

      //Get rid of the row in the table
      $(this).closest("tr").remove();

      //Disable the submit button if required
      if (Object.keys(people).length < 2) {
        $submitButton.prop("disabled", true);
      }
    });

    $submitButton.click(function (e) {
      e.preventDefault();
      ajaxSearch();
    });


    $("[data-toggle='tooltip']").tooltip();

    function addPinToMap(location, name) {
      var marker = new google.maps.Marker({
        position: location,
        label: name,
        clickable: false,
        map: map
      });
      bounds.extend(marker.getPosition());
      return marker;
    }

    function Person(name, from, latLong, transportMode, prettyTransportMode) {
      this.name = name;
      this.from = from;
      this.latLong = latLong;
      this.transportMode = transportMode;
      this.id = personId;

      //The properties of "this" are the ones we want to actually send to the backend
      //The ones in the object are only required by the JS.
      people[this.id] = {
        person: this,
        marker: addPinToMap(latLong, name),
        prettyTransportMode: prettyTransportMode
      };

      centreMap();

      personId += 1;
      $peopleTable.append(Mustache.to_html(templates.tableRow, this));

      this.remove = function () {
        people[this.id].marker.setMap(null);
        delete people[this.id];
        bounds = new google.maps.LatLngBounds();
        for (var person in people) {
          bounds.extend(people[person].marker.getPosition());
        }
        centreMap();
      }
    }

    /**
     * If there are no more markers, centre on the whole UK,
     * if there is one marker focus on it, otherwise centre on the bounding box
     * of all the markers.
     */
    function centreMap() {
      if (Object.keys(people).length == 0) { // Can't possibly be POIs if < 2 people
        map.setCenter(new google.maps.LatLng(54.152141, -3.032227));
        map.setZoom(7);
      } else if (Object.keys(people).length == 1) {
        map.setCenter(people[Object.keys(people)[0]].marker.getPosition());
        map.setZoom(10);
      } else {
        map.fitBounds(bounds);
      }
    }

    /**
     * Does the main search function using AJAX.
     */
    function ajaxSearch() {
      //Hide any errors so far
      $errorSection.fadeOut();
      $errorSection.empty();

      //Give user feedback we are doing something
      $submitButton.button("loading");

      //Convert the object to JSON
      var data = JSON.stringify({
        "people": Object.keys(people).map(function (id) {
          return people[id].person;
        }),
        "poiType": $("#poi").val()
      });

      //Send it off!
      var req = $.ajax({
        url: "/search",
        contentType: "application/json",
        method: "post",
        data: data
      });

      /**
       * Get it back!
       *
       * Replace the contents of the overlay with what we get from the backend.
       * Add a marker for each POI and attach an info window.
       * Add event handlers to each marker & table row to open the info window.
       * If something went wrong, tell the user I'm sorry!
       */
      req.done(function (data) {
        //TODO - fix this
        //$("#submitButton").button("reset");
        //$searchOverlayContent.hide();
        //$overlay.prepend(data.html);
        $searchOverlayContent.html(data.html);
        for (var i = 0; i < data.pois.length; i++) {
          var latLng = data.pois[i].latLong;
          var marker = new google.maps.Marker({
            position: new google.maps.LatLng(latLng.lat, latLng.lng),
            label: "" + (i + 1),
            clickable: true,
            map: map
          });

          var infowindow = new google.maps.InfoWindow({
            content: Mustache.to_html(templates.infowindow, data.pois[i]),
            maxWidth: settings.isMobile ? 200 : 500
          });

          infowindow.addListener("closeclick", function () {
            resetOverlay();
            centreMap();
          });

          infowindows.push(infowindow);

          addPoiMarkerEventHandler(marker, infowindow);

          poiMarkers.push(marker);
          bounds.extend(marker.getPosition());
        }

        centreMap();
        bounceOverlay();

        //If we click on a POI table row, simulate a click on the coresponding marker
        $("#poiTable").find("tr").click(function () {
          peelBackOverlay();
          google.maps.event.trigger(poiMarkers[$(this).index()], 'click');
        });

        //If the user clicks to meetupagain, grab the original contents of
        // of the overlay and put it back.  This is a hack to get round
        // having to reload the whole map which is slow if we just made the button an anchor
        // of "/"
        //$("#searchAgain").click(function (e) {
        //  //Ditch any errors we had the first time
        //  $errorSection.hide();
        //  $errorSection.empty();
        //
        //  //Empty the current state of the app
        //  for (var id in people) {
        //    people[id].person.remove();
        //  }
        //
        //  $peopleTable.empty();
        //
        //  infowindows.length = 0;
        //  for (var marker in poiMarkers) {
        //    poiMarkers[marker].setMap(null);
        //  }
        //  poiMarkers.length = 0;
        //  centreMap();
        //
        //  resetOverlay();
        //
        //  //Switch the HTML
        //  var $resultsOverlayContent = $("#resultsOverlayContent");
        //  $resultsOverlayContent.fadeOut();
        //  $resultsOverlayContent.remove();
        //  $searchOverlayContent.fadeIn();
        //
        //  e.preventDefault();
        //});

        //Info windows, these have to use event delegation, since they are not on the dom
        //Until the window is displayed
        $(document.body).on("click", ".opening-hours-glyph", function () {
          $(this).parents().find(".opening-times").toggle()
        });
      });

      //If there was an error, the overlay won't have changed,
      // make the submit button clickable again so that when the user
      // has fixed the issue, they can click on it!!
      req.error(function (data) {
        $("#submitButton").button("reset");
        if (data.responseJSON.errorReasons) {
          data.responseJSON.errorReasons.forEach(function (error) {
            addError(error);
          });
        } else {
          addError("UNKNOWN");
        }
        $("#error-section").fadeIn();
      });
    }

    //Need to do this outside of the loop or else we always open the same one!
    function addPoiMarkerEventHandler(marker, infowindow) {
      marker.addListener("click", function () {
        infowindows.forEach(function (eachInfoWindow) {
          eachInfoWindow.close();
        });
        map.setCenter(marker.position);
        map.setZoom(15);
        infowindow.open(map, marker);
      });
    }

    /**
     * Use mustache to add an error - simples!
     */
    function addError(errorMessage) {
      $errorSection.append(Mustache.to_html(templates.error, {message: errorMessages[errorMessage]}));
      $errorSection.fadeIn();
    }

    //Calc each time encase they have rotated the device.
    function maxleft() {
      return 0 - $(window).width() * 0.85;
    }

    //Ability to slide overlay to see map if we are on a mobile.
    if (settings.isMobile) {
      $grip.on("touchmove", function (e) {
        var x = e.originalEvent.changedTouches[0].clientX;
        $overlay.css('left', x - $(window).width() + 'px');
        e.preventDefault();
      });

      $grip.on("touchend", function (e) {
        var x = e.originalEvent.changedTouches[0].clientX;

        if (x - $(window).width() < maxleft() / 2) {
          peelBackOverlay()
        } else {
          resetOverlay();
        }

        e.preventDefault();
      });
    }

    function peelBackOverlay() {
      if (settings.isMobile) {
        $overlay.animate({left: maxleft()}, "fast");
      }
    }

    function resetOverlay() {
      if (settings.isMobile) {
        $overlay.animate({left: 0}, "fast");
      }
    }

    //Visual clue to users that you can peel back
    function bounceOverlay() {
      if (settings.isMobile) {
        $overlay.animate({left: -10}, "fast");
        $overlay.animate({left: 0}, "fast");
        $overlay.animate({left: -10}, "fast");
        $overlay.animate({left: 0}, "fast");
      }
    }
  });
}());