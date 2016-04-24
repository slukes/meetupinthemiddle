package com.meetupinthemiddle.services

import com.google.maps.DistanceMatrixApi
import com.google.maps.model.DistanceMatrix
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.TransportMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GoogleMapsJourneyTimesFinder implements JourneyTimesFinder {
  @Autowired
  private RestTemplate restTemplate

  @Value('${}')

  private static final def TRANSPORT_MODE_TO_GOOGLE_MODEL =
      [(TransportMode.DRIVING): TravelMode.DRIVING, (TransportMode.PUBLIC): TravelMode.TRANSIT]

  @Override
  Map<Person, Map<LatLong, Integer>> getJourneyTimes(final Person[] people, final LatLong[] points) {
    String[] gmPoints = Arrays.stream(points).map({new LatLng(it.lat, it.lng)}).toArray()

    Map result = [:]

    def mode = TRANSPORT_MODE_TO_GOOGLE_MODEL.get(people[0].transportMode)
    def resp = doGoogleMapsRequest(gmOrigins, gmPoints, mode)
    addResults(people, resp, gmPoints, result)
    result
  }

  private addResults(people, resp, points, result) {
    for (def i = 0; i < people.size(); i++) {
      def row = resp.rows[i].elements
      def times = [:]
      for (def j = 0; j < points.length; j++) {
        times[points[j]] =  row[j].duration.inSeconds
      }
      result[people[i]] = times
    }
  }

  private DistanceMatrix doGoogleMapsRequest(origins, points, mode) {
    DistanceMatrixApi.newRequest(context)
        .mode(mode)
        .origins(origins)
        .destinations(points)
        .await()
  }
}