var map;
var bounds;
var geocoder;
var personId = 0;
var people = {};
var poiMarkers = [];
var infowindows = [];
var haveNewValue = false;

//Mapping between error messages in the java enumeration and what should be shown to the user.
var errorMessages = {
  UNKNOWN: "Sorry, an unknown error occurred.",
  NOT_ENOUGH_PEOPLE: "Ooops!  You need at least two people to MeetUpInTheMiddle!",
  MISSING_NAME: "Ooops!  Looks like you missed out a name!",
  MISSING_FROM: "Ooops!  We need to know where everyone is travelling from!",
  MISSING_OR_INVALID_POI_TYPE: "Ooops!  Please tell us what kind of place you are looking to meet at!",
  MISSING_OR_INVALID_TRANSPORT_MODE: "Ooops!  Please tell us how everyone is travelling in!",
  UNKNOWN_LOCATION: "Ooops!  We can't find one or more of your locations."
};

//Load Templates to be used by mustache
/*TODO I'm not sure that this is the best way of doing this???
 I need to fetch the templates from the server, my first attempt
 did the get request every time we needed the template but given
 they will certainly be needed and more than once it seemed a waste.
 What is the usual approach?  I could include them in the DOM????
 */

var infoWindowTemplate;
$.get("mustache/infowindow.html", function (template) {
  infoWindowTemplate = template;
});

var tableRowTemplate;
$.get("mustache/personTableRow.html", function (template) {
  tableRowTemplate = template;
});

var errorTemplate;
$.get("mustache/error.html", function (template) {
  errorTemplate = template;
});

$(document).ready(function () {
  //We need these selectors more than once so lets get them now
  var newPersonForm = $("#newPersonForm");
  var newName = $("#newName");
  var newFrom = $("#newFrom");
  var newMode = $("#newMode");
  var addPersonBtn = $("#add-person");
  var peopleTable = $("#peopleTable");
  var submitButton = $("#submitButton");

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
    this.prettyTransportMode = prettyTransportMode;
    this.id = personId;
    people[personId] = this;
    this.marker = addPinToMap(latLong, name);
    centreMap();

    personId += 1;
    $("#peopleTable").append(Mustache.to_html(tableRowTemplate, this));

    this.remove = function () {
      delete people[this.id];
      this.marker.setMap(null);
      bounds = new google.maps.LatLngBounds();
      for (person in people) {
        bounds.extend(people[person].marker.getPosition());
      }
      centreMap();
    }
  }

  //If both the name and location field have text in them, enable the add person button
  newName.add(newFrom).on("change keyup", function () {
    haveNewValue = $.trim(newName.val()).length > 0
      && $.trim(newFrom.val()).length > 0;

    if (haveNewValue) {
      addPersonBtn.removeClass("disabled");
    }
  });

  /**
   * When the add person button is clicked, if we have new entries in the form,
   * we geocode the location add a map pin and then add a person to the table.
   *
   * If we now have enough people in the table, we enable the submit button.
   */
  addPersonBtn.click(function () {
    if (!haveNewValue) {
      return;
    }

    var name = newName.val();
    var location = newFrom.val();
    var mode = newMode.val();
    //How to display it to the user
    var prettyMode = newMode.find("option[value=" + mode + "]").text();
    geocoder.geocode({"address": location}, function (results, status) {
      if (status === google.maps.GeocoderStatus.OK) {
        var latLng = results[0].geometry.location;
        new Person(name, location, latLng, mode, prettyMode);

        newPersonForm[0].reset();
        haveNewValue = false;
        addPersonBtn.addClass("disabled");

        if (personId >= 2) {
          submitButton.prop("disabled", false);
        }
      } else {
        //TODO if the geo-coded location is not in the UK - give a sensible error.  This needs doing on the backend too.
        addError("UNKNOWN_LOCATION");
      }
    });
  });

  /**
   * When we click the x next to a row in the table,
   * we need to remove the row from the table,
   * remove the pin from the map and from the JSON object that we
   * are building up to send to the backend.
   */
  $("body").on("click", ".removePerson", function (e) {
    var idToRemove = e.target.id.replace(/remove\[(\d)\]/, "$1");
    people[idToRemove].remove();

    //Get rid of the row in the table
    $(this).closest("tr").remove();

    //Disable the submit button if required
    if (peopleTable.find("tr").length < 2) {
      submitButton.prop("disabled", true);
    }
  });

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

  submitButton.click(function (e) {
    e.preventDefault();
    ajaxSearch();
  });

  /**
   * Does the main search function using AJAX.
   */
  function ajaxSearch() {
    //Hide any errors so far
    var errorSection = $("#error-section");
    errorSection.fadeOut();
    errorSection.empty();

    //Give user feedback we are doing something
    submitButton.button("loading");

    //Convert the object to JSON
    var data = JSON.stringify({
      //TODO - this is probably a code smell...
      //Something here was causing a circular reference - just send what's required
      "people": Object.keys(people).map(function (key) {
        delete people[key].remove;
        delete people[key].prettyTransportMode;
        delete people[key].marker;
        delete people[key].id;
        return people[key];
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
      $("#overlayContent").html(data.html);
      for (var i = 0; i < data.pois.length; i++) {
        var latLng = data.pois[i].latLong;
        marker = new google.maps.Marker({
          position: new google.maps.LatLng(latLng.lat, latLng.lng),
          label: "" + (i + 1),
          clickable: true,
          map: map
        });

        infowindow = new google.maps.InfoWindow({
          content: Mustache.to_html(infoWindowTemplate, data.pois[i])
        });

        infowindow.addListener("closeclick", function () {
          centreMap();
        });

        infowindows.push(infowindow);

        addPoiMarkerEventHandler(marker, infowindow);

        poiMarkers.push(marker);
        bounds.extend(marker.getPosition());
        centreMap();
      }

      //If we click on a POI table row, simulate a click on the coresponding marker
      $("#poiTable").find("tr").click(function () {
        google.maps.event.trigger(poiMarkers[$(this).index()], 'click');
        poiMarkers[$(this).index()].click();
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
});

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
  var errorSection = $("#error-section");
  errorSection.append(Mustache.to_html(errorTemplate, {message: errorMessages[errorMessage]}));
  errorSection.fadeIn();
}

function initMap() {
  bounds = new google.maps.LatLngBounds();
  geocoder = new google.maps.Geocoder();

  $(document).ready(function () {
    //Load the map
    map = new google.maps.Map(document.getElementById("map"), {
      center: {lat: 54.152141, lng: -3.032227}, //Centre of UK
      zoom: 7,
      mapTypeControl: false
    });

    new google.maps.places.Autocomplete(document.getElementById("newFrom"),
      {
        componentRestrictions: {
          country: "gb"
        }
      });
    return false;
  });
}