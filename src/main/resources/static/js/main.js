$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();
});

var map;
function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 54.152141, lng: -3.032227},
        zoom: 7,
        mapTypeControl: false
    });

    var personCount = 0;
    var newName = $("#newName"),
        newFrom = $("#newFrom");

    var haveNewValue = false;

    $(newName).add(newFrom).on('input', function (e) {
        haveNewValue = $.trim(newName.val()).length > 0
            && $.trim(newFrom.val()).length > 0;

        if (haveNewValue) {
            $('#add-person').removeClass('disabled');
        }
    });

    $('#add-person').click(function (e) {
        if (!haveNewValue) return;
        var name = $('#newName').val();
        var location = $('#newFrom').val();
        var mode = $('#newMode').val();
        addRowToPeopleTable(name, location, mode);
        addPinToMap(location, name);
        $('#newPersonForm')[0].reset();
        haveNewValue = false;
        $('#add-person').addClass('disabled');
        personCount++;
        if (personCount >= 2) {
            $('#submitButton').prop('disabled', false);
        }
    });

    var bounds = new google.maps.LatLngBounds();
    var geocoder = new google.maps.Geocoder();
    var markers = [];
    var infowindows = [];

    function addPinToMap(location, name) {
        geocoder.geocode({'address': location}, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                var marker = new google.maps.Marker({
                    position: results[0].geometry.location,
                    label: name,
                    clickable: false,
                    map: map
                });
                markers.push(marker);
                bounds.extend(marker.getPosition());
                centreMap();
            }
        });
    }

    function addRowToPeopleTable(name, from, mode) {
        var person = {
            name: name,
            from: from,
            mode: mode,
            personCount: personCount
        };

        $.get('mustache/personTableRow.html', function (template) {
            $('#peopleTable').append(Mustache.to_html(template, person));
        });
    }

    $('body').on('click', '.removePerson', function (e) {
        var idToRemove = e.target.id.replace(/remove\[(\d)\]/, "$1");
        markers[idToRemove].setMap(null);
        bounds = new google.maps.LatLngBounds();
        markers.splice(idToRemove, 1);
        markers.forEach(function (marker) {
            bounds.extend(marker.getPosition());
        });
        centreMap();
        $('#peopleTable').find('tr').each(function () {
            if (this.rowIndex > idToRemove) {
                var newIndex = this.rowIndex - 1;
                $(this).find('.name').attr('name', 'name[' + newIndex + ']');
                $(this).find('.from').attr('name', 'from[' + newIndex + ']');
                $(this).find('.mode').attr('name', 'mode[' + newIndex + ']');
                $(this).find('.removePerson').attr('id', 'remove[' + newIndex + ']');

                markers[newIndex] = markers[this.rowIndex];
            }
        });
        $(this).closest('tr').remove();
        personCount--;
        if (personCount < 2) {
            $('#submitButton').prop('disabled', true);
        }
    });

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

    var autocomplete = new google.maps.places.Autocomplete(document.getElementById('newFrom'),
        {
            componentRestrictions: {country: 'gb'}
        });

    $('#submitButton').click(function (e) {
        e.preventDefault();
        $.ajax({
            url: '/search',
            contentType: 'application/json',
            method: 'post',
            success: function (data) {
                $('#overlayContent').html(data.html);
                for (var i = 0; i < data.pois.length; i++) {
                    var latLng = data.pois[i].geocode;
                    const marker = new google.maps.Marker({
                        position: new google.maps.LatLng(latLng.lat, latLng.lng),
                        label: "" + (i + 1),
                        clickable: true,
                        map: map
                    });
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
    });
    return false;
}