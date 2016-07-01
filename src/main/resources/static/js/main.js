var map;
var bounds;
var geocoder;
var personId = 0;
var people = {};
var markers = [];
var infowindows = [];
var haveNewValue = false;
var errorMessages = {
    UNKNOWN: "Sorry, an unknown error occurred.",
    NOT_ENOUGH_PEOPLE: "Ooops!  You need at least two people to MeetUpInTheMiddle!",
    MISSING_NAME: "Ooops!  Looks like you missed out a name!",
    MISSING_FROM: "Ooops!  We need to know where everyone is travelling from!",
    MISSING_LAT_LONG: this.UNKNOWN,
    MISSING_OR_INVALID_POI_TYPE: "Ooops!  Please tell us what kind of place you are looking to meet at!",
    MISSING_OR_INVALID_TRANSPORT_MODE: "Ooops!  Please tell us how everyone is travelling in!"
};

$(document).ready(function () {
    $("[data-toggle='tooltip']").tooltip();
    //If something is entered in both the name and from field
    //the add button should be ungreyed out
    var newName = $('#newName');
    var newFrom = $('#newFrom');

    newName.add(newFrom).on("change keyup", function (e) {
        haveNewValue = $.trim(newName.val()).length > 0
            && $.trim(newFrom.val()).length > 0;

        if (haveNewValue) {
            $("#add-person").removeClass("disabled");
        }
    });

    //When the add person button is clicked
    //Assuming that data is in the name anf from fields then:
    // Add a row to the table
    // Add a pin to the map
    // Reset the form fields
    // If we now have 2 or more peopl enable the submit button
    $("#add-person").click(function (e) {
        if (!haveNewValue) {
            return;
        }

        var name = $("#newName").val();
        var location = $("#newFrom").val();
        var mode = $("#newMode").val();
        var prettyMode = $("#newMode").find("option[value=" + mode + "]").text();

        geocoder.geocode({"address": location}, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                latLng = results[0].geometry.location;
                addPinToMap(latLng, name);
                addPerson(name, location, latLng, mode, prettyMode);

                $("#newPersonForm")[0].reset();
                haveNewValue = false;
                $("#add-person").addClass("disabled");
                personId++;

                if (personId >= 2) {
                    $("#submitButton").prop("disabled", false);
                }
            } else {
                //TODO
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
        markers.push(marker);
        bounds.extend(marker.getPosition());
        centreMap();
    }

    function addPerson(name, from, latLng, mode, prettyTransportMode) {
        var person = new Person(name, from, latLng, mode, prettyTransportMode, personId);
        people[personId] = person;

        //TODO it would be more efficient to not make this AJAX request everytime
        //Is it possible to load it into the DOM as a hidden element at the start?
        $.get("mustache/personTableRow.html", function (template) {
            $("#peopleTable").append(Mustache.to_html(template, person));
        });
    }

    //Removing a person
    $("body").on("click", ".removePerson", function (e) {
        var idToRemove = e.target.id.replace(/remove\[(\d)\]/, "$1");
        //Removes pin from map and re-centres
        markers[idToRemove].setMap(null);
        bounds = new google.maps.LatLngBounds();
        markers.splice(idToRemove, 1);
        markers.forEach(function (marker) {
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
        if ($("#peopleTable tr").length < 2) {
            $("#submitButton").prop("disabled", true);
        }
    });

    /**
     * Centres the pins on the points currently displayed
     */
    function centreMap() {
        if (markers.length == 0) {
            map.setCenter(new google.maps.LatLng(54.152141, -3.032227));
            map.setZoom(7);
        } else if (markers.length == 1) {
            map.setCenter(markers[0].getPosition());
            map.setZoom(10);
        } else {
            map.fitBounds(bounds);
        }
    }

    $("#submitButton").click(function (e) {
        e.preventDefault();
        ajaxSearch();
    });

    //Loads the results when the submit button is clicked.
    //Changes the content of the overlaid section
    //Adds a point to the map and an info window for each of the businesses
    function ajaxSearch() {
        data = JSON.stringify({
            "people": Object.keys(people).map(function (key) {
                return people[key];
            }),
            "poiType": $("#poi").val()
        });

        req = $.ajax({
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
                //TODO how can I get rid of this const?  I was trying not to use ECMA 6 features.
                //This is required since initially I was finding that the call back below was reading the value of i
                //For the iteration the loop was on at the time of the callback rather than the correct value
                const index = i;
                //TODO - more efficient way than ajax here????
                $.get("mustache/infowindow.html", function (template) {
                    var infowindow = new google.maps.InfoWindow({
                        content: Mustache.to_html(template, data.pois[index])
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
                });

                markers.push(marker);
                bounds.extend(marker.getPosition());
                centreMap();
            }
        });

        req.error(function (data) {
            var errorSection = $("#error-section");
            if (data.responseJSON.errorReasons) {
                data.responseJSON.errorReasons.forEach(function (error) {
                    addError(error);
                });
            } else {
                addError("UNKNOWN");
            }
            errorSection.fadeIn();
        });
    }

});


function addError(errorMessage) {
    $.get("mustache/error.html", function (template) {
            $("#error-section").append(Mustache.to_html(template, {message: errorMessages[errorMessage]}));
        }
    );
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

        //Configure automcomplete for locations
        //TODO configure this further
        new google.maps.places.Autocomplete(document.getElementById("newFrom"),
            {
                componentRestrictions: {country: "gb"}
            });
        return false;
    });
}