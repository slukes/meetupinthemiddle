var map;
var bounds;
var geocoder;
var personId = 0;
var people = {};
var personMarkers = [];
var poiMarkers = [];
var infowindows = [];
var haveNewValue = false;
var errorMessages = {
  UNKNOWN: "Sorry, an unknown error occurred.",
  NOT_ENOUGH_PEOPLE: "Ooops!  You need at least two people to MeetUpInTheMiddle!",
  MISSING_NAME: "Ooops!  Looks like you missed out a name!",
  MISSING_FROM: "Ooops!  We need to know where everyone is travelling from!",
  MISSING_LAT_LONG: this.UNKNOWN,
  MISSING_OR_INVALID_POI_TYPE: "Ooops!  Please tell us what kind of place you are looking to meet at!",
  MISSING_OR_INVALID_TRANSPORT_MODE: "Ooops!  Please tell us how everyone is travelling in!",
  UNKNOWN_LOCATION: "Ooops!  We can't find one or more of your locations."
};

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
  var newPersonForm = $("#newPersonForm");
  var newName = $('#newName');
  var newFrom = $('#newFrom');
  var newMode = $("#newMode");
  var addPersonBtn = $("#add-person");
  var peopleTable = $("#peopleTable");
  var submitButton = $("#submitButton");

  $("[data-toggle='tooltip']").tooltip();

  newName.add(newFrom).on("change keyup", function (e) {
    haveNewValue = $.trim(newName.val()).length > 0
      && $.trim(newFrom.val()).length > 0;

    if (haveNewValue) {
      addPersonBtn.removeClass("disabled");
    }
  });

  addPersonBtn.click(function (e) {
    if (!haveNewValue) {
      return;
    }

    var name = newName.val();
    var location = newFrom.val();
    var mode = newMode.val();
    var prettyMode = newMode.find("option[value=" + mode + "]").text();

    geocoder.geocode({"address": location}, function (results, status) {
      if (status === google.maps.GeocoderStatus.OK) {
        latLng = results[0].geometry.location;
        addPinToMap(latLng, name);
        addPerson(name, location, latLng, mode, prettyMode);

        newPersonForm[0].reset();
        haveNewValue = false;
        addPersonBtn.prop("disabled", true);
        personId++;

        if (personId >= 2) {
          submitButton.prop("disabled", false);
        }
      } else {
        addError("UNKNOWN_LOCATION");
      }
    });
  });

  function addPinToMap(location, name) {
    var marker = new google.maps.Marker({
      position: location,
      label: name,
      clickable: false,
      map: map
    });
    personMarkers.push(marker);
    bounds.extend(marker.getPosition());
    centreMap();
  }

  function addPerson(name, from, latLng, mode, prettyTransportMode) {
    var person = new Person(name, from, latLng, mode, prettyTransportMode, personId);
    people[personId] = person;
    $("#peopleTable").append(Mustache.to_html(tableRowTemplate, person));
  }

  //Removing a person
  $("body").on("click", ".removePerson", function (e) {
    var idToRemove = e.target.id.replace(/remove\[(\d)\]/, "$1");
    //Removes pin from map and re-centres
    personMarkers[idToRemove].setMap(null);
    bounds = new google.maps.LatLngBounds();
    personMarkers.splice(idToRemove, 1);
    personMarkers.forEach(function (marker) {
      if (marker.getMap() != null) {
        bounds.extend(marker.getPosition());
      }
    });
    centreMap();

    //Get rid of the row in the table
    $(this).closest("tr").remove();

    //And from the JSON we will send
    delete people[idToRemove];
    //Disable the submit button if required
    if (peopleTable.find("tr").length < 2) {
      submitButton.prop("disabled", true);
    }
  });

  function centreMap() {
    if (personMarkers.length == 0) {
      map.setCenter(new google.maps.LatLng(54.152141, -3.032227));
      map.setZoom(7);
    } else if (personMarkers.length == 1) {
      map.setCenter(personMarkers[0].getPosition());
      map.setZoom(10);
    } else {
      map.fitBounds(bounds);
    }
  }

  submitButton.click(function (e) {
    e.preventDefault();
    ajaxSearch();
  });

  function ajaxSearch() {
    $("#error-section").fadeOut();
    submitButton.button('loading');

    var data = JSON.stringify({
      "people": Object.keys(people).map(function (key) {
        return people[key];
      }),
      "poiType": $("#poi").val()
    });

    var req = $.ajax({
      url: "/search",
      contentType: "application/json",
      method: "post",
      data: data
    });

    req.done(function (data) {
      $("#overlayContent").html(data.html);
      for (i = 0; i < data.pois.length; i++) {
        var latLng = data.pois[i].latLong;
        const marker = new google.maps.Marker({
          position: new google.maps.LatLng(latLng.lat, latLng.lng),
          label: "" + (i + 1),
          clickable: true,
          map: map
        });

        var infowindow = new google.maps.InfoWindow({
          content: Mustache.to_html(infoWindowTemplate, data.pois[i])
        });

        infowindow.addListener("closeclick", function () {
          centreMap();
        });

        infowindows.push(infowindow);

        marker.addListener("click", function () {
          infowindows.forEach(function (eachInfoWindow) {
            eachInfoWindow.close();
          });
          map.setCenter(marker.position);
          map.setZoom(15);
          infowindow.open(map, marker);
        });

        poiMarkers.push(marker);
        bounds.extend(marker.getPosition());
        centreMap();
      }

      $("#poiTable").find("tr").click(function () {
        var idx = $(this).index();
        var marker = poiMarkers[idx];
        var infowindow = infowindows[idx];
        map.setCenter(marker.position);
        map.setZoom(15);
        infowindow.open(map, marker);
      });
    });

    req.error(function (data) {
      $('#submitButton').button('reset');
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

function addError(errorMessage) {
  var errorSection = $("#error-section");
  errorSection.append(Mustache.to_html(errorTemplate, {message: errorMessages[errorMessage]}));
  errorSection.fadeIn();
}

function Person(name, from, latLong, transportMode, prettyTransportMode, personId) {
  this.name = name;
  this.from = from;
  this.latLong = latLong;
  this.transportMode = transportMode;
  this.prettyTransportMode = prettyTransportMode;
  this.personId = personId;
}

function initMap() {
  bounds = new google.maps.LatLngBounds();
  geocoder = new google.maps.Geocoder();

  $(document).ready(function () {
    //Load the map
    map = new google.maps.Map(document.getElementById("map"), {
      center: {lat: 54.152141, lng: -3.032227},
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