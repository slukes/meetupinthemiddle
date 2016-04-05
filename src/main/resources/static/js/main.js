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
    $('#add-person').click(function (e) {
        e.preventDefault();
        //TODO validate the inputs
        var name = $('#newName').val();
        var location = $('#newFrom').val();
        var mode = $('#newMode').val();
        addRowToPeopleTable(name, location, mode);
        addPinToMap(location, name);
        $('#newPersonForm')[0].reset();
        personCount++;
        return false;
    });

    var bounds = new google.maps.LatLngBounds();
    var geocoder = new google.maps.Geocoder();
    var markers = [];

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
        $('#peopleTable').append(
            $(
                '<tr>' +
                '<td><span class="glyphicon glyphicon-map-marker"></span></td>' +
                '<td><input type="hidden" class="name" name="name[' + personCount + ']" value="' + name + '" />' + name + '</td>' +
                '<td><input type="hidden" class="from" name="from[' + personCount + ']" value="' + from + '" />' + from + '</td>' +
                '<td><input type="hidden" class="mode" name="mode[' + personCount + ']" value="' + mode + '" />' + mode + '</td>' +
                '<td><span class="removePerson glyphicon glyphicon-remove" id="remove[' + personCount + ']"></span></td>' +
                '</tr>'
            ).click(function (e) {
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
            })
        );
    }

    function centreMap() {
        if (markers.length == 0) {
            map.setCenter(new google.maps.LatLng(54.152141, -3.032227));
            map.setZoom(7);
        } else if (markers.length == 1) {
            map.setCenter(markers[0].getPosition());
            map.setZoom(10);
        } else {
            //TODO experiement to find some sensible value for this.
            //TODO can we take into account the type of the first location?
            map.fitBounds(bounds);
        }
    }
}