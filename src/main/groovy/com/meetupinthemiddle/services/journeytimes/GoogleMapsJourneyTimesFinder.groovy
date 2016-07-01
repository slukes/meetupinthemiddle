package com.meetupinthemiddle.services.journeytimes
import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DistanceMatrix
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.TransportMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GoogleMapsJourneyTimesFinder implements JourneyTimesFinder {
  @Autowired
  GeoApiContext context

  private static final def TRANSPORT_MODE_TO_GOOGLE_MODEL =
      [(TransportMode.DRIVING): TravelMode.DRIVING, (TransportMode.PUBLIC): TravelMode.TRANSIT]

  @Override
  Map<LatLong, List<Integer>> getJourneyTimes(List<Person> people, List<LatLng> points) {
    LatLng[] gmStarts = people
        .stream()
        .map({ new LatLng(it.latLong.lat, it.latLong.lng) })
        .toArray()

    LatLng[] gmDestinations = points.stream()
        .map({ it != null ? new LatLng(it.lat, it.lng) : null })
        .filter({ it != null })
        .toArray()

    def result = [:]
    def mode = TRANSPORT_MODE_TO_GOOGLE_MODEL.get(people[0].transportMode)
    def resp = doGoogleMapsRequest(gmStarts, gmDestinations, mode)
    addResults(people, resp, gmDestinations, result)
    result
  }

  private addResults(List<Person> people, DistanceMatrix resp, LatLng[] points, Map<LatLong, List<Integer>> result) {
    for (def i = 0; i < points.length; i++) {
      //Each row in the google model represents a person
      //Each element in the row represents a destination
      def latLong = new LatLong(points[i].lat, points[i].lng)
      def list = []
      for(def j = 0; j < people.size(); j++){
        list.add(resp.rows[j].elements[i].duration?.inSeconds)
      }
      result.put(latLong, list)
    }
  }

  private DistanceMatrix doGoogleMapsRequest(origins, points, mode) {
    if (points.length > 100) {
      points = Arrays.copyOfRange(points, 0, 99)
    }
    DistanceMatrixApi.newRequest(context)
        .mode(mode)
        .origins(origins)
        .destinations(points)
        .await()
  }
}