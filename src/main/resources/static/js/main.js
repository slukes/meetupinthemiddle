$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});

var map;
function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 54.152141, lng: -3.032227},
        zoom: 7,
        mapTypeControl: false
    });
}