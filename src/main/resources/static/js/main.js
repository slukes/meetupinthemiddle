$(document).ready(function () {
    //Uses jQuery tooltip library to load tooltip
    $('[data-toggle="tooltip"]').tooltip();
});

var map;

function Person(name, from, latLong, transportMode, prettyTransportMode, personId){
    this.name = name;
    this.from = from;
    this.latLong = latLong;
    this.transportMode = transportMode;
    this.prettyTransportMode = prettyTransportMode
    this.personId = personId;
}

/**
 * This function is a call back from the GoogleMaps JS loading
 * The majority of the apps own JS is within this call back
 * as it makes reference to the map.
 */
function initMap() {
    //Keep track of an id so we can easily delete people!
    var personId = 0;
    var people = {};
    var markers = [];
    var newName = $("#newName"),
        newFrom = $("#newFrom");

    var bounds = new google.maps.LatLngBounds();
    var geocoder = new google.maps.Geocoder();
    //Keep track of the markers and info windows so can iterate over them
    var infowindows = [];

    //Used to control whether the add person button should be enabled
    var haveNewValue = false;

    //Load the map
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 54.152141, lng: -3.032227},
        zoom: 7,
        mapTypeControl: false
    });

    //Configure automcomplete for locations
    //TODO configure this further
    var autocomplete = new google.maps.places.Autocomplete(document.getElementById('newFrom'),
        {
            componentRestrictions: {country: 'gb'}
        });

    //If something is entered in both the name and from field
    //the add button should be ungreyed out
    $(newName).add(newFrom).on('change keyup', function (e) {
        haveNewValue = $.trim(newName.val()).length > 0
            && $.trim(newFrom.val()).length > 0;

        if (haveNewValue) {
            $('#add-person').removeClass('disabled');
        }
    });

    //When the add person button is clicked
    //Assuming that data is in the name anf from fields then:
    // Add a row to the table
    // Add a pin to the map
    // Reset the form fields
    // If we now have 2 or more peopl enable the submit button
    $('#add-person').click(function (e) {
        if (!haveNewValue) {
            return;
        }

        var name = $('#newName').val();
        var location = $('#newFrom').val();
        var mode = $('#newMode').val();
        var prettyMode = $('#newMode option[value=' + mode + ']').text()

        geocoder.geocode({'address': location}, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                latLng = results[0].geometry.location;
                addPinToMap(latLng, name);
                addPerson(name, location, latLng, mode, prettyMode);

                $('#newPersonForm')[0].reset();
                haveNewValue = false;
                $('#add-person').addClass('disabled');
                personId++;

                if (personId >= 2) {
                    $('#submitButton').prop('disabled', false);
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
        $.get('mustache/personTableRow.html', function (template) {
            $('#peopleTable').append(Mustache.to_html(template, person));
        });
    }

    //Removing a person
    $('body').on('click', '.removePerson', function (e) {
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
        $(this).closest('tr').remove();

        //And from the JSON we will send
        delete people[idToRemove];

        //Disable the submit button if required
        if ($('#peopleTable tr').length < 2) {
            $('#submitButton').prop('disabled', true);
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

    $('#submitButton').click(function (e) {
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

        $.ajax({
            url: '/search',
            contentType: 'application/json',
            method: 'post',
            data: data,
            success: function (data) {
                $('#overlayContent').html(data.html);
                for (var i = 0; i < data.pois.length; i++) {
                    var latLng = data.pois[i].latLong;
                    const marker = new google.maps.Marker({
                        position: new google.maps.LatLng(latLng.lat, latLng.lng),
                        label: "" + (i + 1),
                        clickable: true,
                        map: map
                    });
                    //This is required since initially I was finding that the call back below was reading the value of i
                    //For the iteration the loop was on at the time of the callback rather than the correct value
                    const index = i;
                    $.get('mustache/infowindow.html', function (template) {
                        var infowindow = new google.maps.InfoWindow({
                            content: Mustache.to_html(template, data.pois[index])
                        });

                        infowindow.addListener('closeclick', function () {
                            centreMap();
                        });

                        infowindows.push(infowindow);

                        marker.addListener('click', function () {
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
            }
        })
    }

    return false;
}