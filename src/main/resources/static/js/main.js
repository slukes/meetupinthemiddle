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
  overrideBoundsMethod();
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
      isTest = $("#map").data("test"),
      editingPerson,
      errorMessages = {
        UNKNOWN: "Sorry, an unknown error occurred.",
        NOT_ENOUGH_PEOPLE: "Ooops!  You need at least two people to MeetUpInTheMiddle!",
        MISSING_NAME: "Ooops!  Looks like you missed out a name!",
        MISSING_FROM: "Ooops!  We need to know where everyone is travelling from!",
        MISSING_OR_INVALID_POI_TYPE: "Ooops!  Please tell us what kind of place you are looking to meet at!",
        MISSING_OR_INVALID_TRANSPORT_MODE: "Ooops!  Please tell us how everyone is travelling in!",
        UNKNOWN_LOCATION: "Ooops!  We can't find one or more of your locations.  Only locations in the UK are supported.",
        OVER_QUOTA: "Ooops!  MeetUpInTheMiddle.com makes use of data from Google Maps, which they charge for.  In order to keep the site free we have to limit the number of requests per day.  Please come back after 8am"
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
      $addPersonBtn = $("#add-person", $newPersonForm),
      $peopleTable = $("#peopleTable", $newPersonForm),
      $submitButton = $("#submitButton"),
      $overlay = $("#overlay"),
      $grip = $("#grip", $overlay),
      $hideArrows = $("#hideArrows", $overlay),
      $showArrows = $("#showArrows", $overlay),
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
      var mode = $("input[name='newMode']:checked", $newPersonForm).val();

      geocoder.geocode({
        "address": location, "region": "GB", componentRestrictions: {country: 'UK'}
      }, function (results, status) {
        if (status === google.maps.GeocoderStatus.OK && results[0].address_components.length > 1) { //Length of one is address of "UK" means it wasn't found.
          var latLng = results[0].geometry.location;
          if (editingPerson === undefined || editingPerson === null) { //Don't rely on falsy since person ids start from 0
            new Person(name, location, latLng, mode);
          } else {
            people[editingPerson].person.update(name, location, latLng, mode);
            editingPerson = null;
          }

          $newPersonForm[0].reset();
          haveNewValue = false;
          $addPersonBtn.addClass("disabled");

          if (personId >= 2) {
            $submitButton.prop("disabled", false);
          }

          $newName.focus()
        } else {
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
    $(document.body).on("click", ".removePerson:not(.disabled)", function (e) {
      var $row = $(this).closest("tr");
      var idToRemove = $row.data("personid");
      people[idToRemove].person.remove();

      //Get rid of the row in the table
      $row.remove();

      //Disable the submit button if required
      if (Object.keys(people).length < 2) {
        $submitButton.prop("disabled", true);
      }
    });

    /**
     * When we click the pencil next to a row in the table,
     * we need to set the form fields to be the values relating to that person
     * so they can be edited.
     *
     * I am considering changing this to use some kind of popover functionality /edit in place
     * however want to get it running this way to get feedback first.
     */
    $(document.body).on("click", ".editPerson:not(.disabled)", function (e) {
      var $row = $(this).closest("tr");
      var idToEdit = $row.data("personid");
      var person = people[idToEdit].person;

      //If we are already editing someone else, say no, otherwise set flag
      if (editingPerson) {
        //TODO - better way of doing this.
        alert("Please finish editing " + people[editingPerson].person.name + " before editing another person.")
        return;
      } else {
        editingPerson = idToEdit;
      }

      //Set the form fields to the values of the person
      $newName.val(person.name);
      $newFrom.val(person.from);
      //Simulating a click takes care of bootstrap classes etc. too.
      $("input[name='newMode'][value=" + person.transportMode + "]").click()
      haveNewValue = true; //We must have something since we just put it there
      $addPersonBtn.removeClass("disabled");

      //Grey out the row in the table
      $row.addClass("disabled");
      var controls = $row.find(".tableControl");
      controls.addClass("disabled");
    });

    $submitButton.click(function (e) {
      e.preventDefault();
      ajaxSearch();
    });

    $("[data-toggle='tooltip']").tooltip({container: ".overlayContent"});

    function addPinToMap(location, name) {
      var marker = new google.maps.Marker({
        position: location,
        label: name,
        clickable: false,
        map: map,
        optimized: !isTest
      }); //if optimized is false then we can see map pins in the dom
      bounds.extend(marker.getPosition());
      return marker;
    }

    function Person(name, from, latLng, transportMode) {
      this.name = name;
      this.from = from;
      this.latLng = latLng;
      this.transportMode = transportMode;
      this.id = personId;

      //The properties of "this" are the ones we want to actually send to the backend
      //The ones in the object are only required by the JS.
      people[this.id] = {
        person: this,
        marker: addPinToMap(latLng, name),
        transportIcon: this.transportMode === "DRIVING" ? "car" : "bus"
      };

      centreMap();

      personId += 1;
      $peopleTable.find("tbody").append(Mustache.to_html(templates.tableRow, people[this.id]));

      this.update = function (name, from, latLng, transportMode) {
        this.name = name;
        this.from = from;
        this.transportMode = transportMode;

        //If we were editing a person we need to replace their table row.
        //We could try and update the existing one - but it would be more fragile to changes in the markup
        $peopleTable.find('*[data-personid="' + this.id + '"]')
          .replaceWith(Mustache.to_html(templates.tableRow, people[this.id]));

        people[this.id].marker.setPosition(latLng);
        people[this.id].marker.setLabel(name);
        rebuildMapBounds();
        centreMap();
      };

      this.remove = function () {
        people[this.id].marker.setMap(null);
        delete people[this.id];
        rebuildMapBounds(people);
        centreMap();
      };
    }

    /**
     * If we have updated the location of a person or removed one,
     * we need to call this before centering hte map.  If we have just added
     * a person - no need.
     */
    function rebuildMapBounds() {
      bounds = new google.maps.LatLngBounds();
      for (var person in people) {
        bounds.extend(people[person].marker.getPosition());
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
        map.fitBounds(bounds, {left: $overlay.width()});
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

    //Ability to just tap to peel back / reset after feedback that resetting was hard
    $hideArrows.click(peelBackOverlay);
    $showArrows.click(resetOverlay);

    function peelBackOverlay() {
      if (settings.isMobile) {
        $overlay.animate({left: maxleft()}, "fast");
        $hideArrows.hide();
        $showArrows.show();
      }
    }

    function resetOverlay() {
      if (settings.isMobile) {
        $overlay.animate({left: 0}, "fast");
        $showArrows.hide();
        $hideArrows.show();
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