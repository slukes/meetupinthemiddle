package com.meetupinthemiddle.services.journeytimes

import com.google.maps.DistanceMatrixApiRequest
import com.google.maps.model.DistanceMatrix
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.services.AbstractConcurrentGoogleMapsService
import org.springframework.stereotype.Service

import java.util.concurrent.Future

import static com.google.maps.DistanceMatrixApi.newRequest
import static com.google.maps.model.TravelMode.TRANSIT
import static com.meetupinthemiddle.model.TransportMode.DRIVING
import static com.meetupinthemiddle.model.TransportMode.PUBLIC

@Service
class GoogleMapsJourneyTimesFinder extends AbstractConcurrentGoogleMapsService<DistanceMatrix, DistanceMatrixApiRequest> implements JourneyTimesFinder {
  private static final int MAX_PER_REQ = 25

  private static final def TRANSPORT_MODE_TO_GOOGLE_MODEL =
      [(DRIVING): TravelMode.DRIVING, (PUBLIC): TRANSIT]

  @Override
  Map<LatLong, List<Integer>> getJourneyTimes(List<Person> people, List<LatLong> points) {
    def result = [:]
    def publicResults = []
    def drivingResults = []
    def (drivingPeople, publicPeople) = people.split { it.transportMode == DRIVING }
    LatLng[] publicGmStarts = getGoogleStartsForPeople(publicPeople)
    LatLng[] drivingGmStarts = getGoogleStartsForPeople(drivingPeople)
    def gmDestinations = mapDestinations(points)

    gmDestinations.collate(MAX_PER_REQ).each {
      batch ->
        if (publicGmStarts.length > 0) {

          publicResults << new Tuple2<>(doBatch(publicGmStarts, batch as LatLng[], PUBLIC), batch)
        }

        if (drivingGmStarts.length > 0) {
          drivingResults << new Tuple2<>(doBatch(drivingGmStarts, batch as LatLng[], DRIVING), batch)
        }
    }

    publicResults.each {
      addResults(publicPeople, it.getFirst().get(), it.getSecond(), result)
    }

    drivingResults.each {
      addResults(drivingPeople, it.getFirst().get(), it.getSecond(), result)
    }

    result
  }

  private doBatch(LatLng[] gmStarts, LatLng[] batch, mode) {
    if (gmStarts.length > 0) {
      doGoogleMapsRequest(gmStarts, batch, TRANSPORT_MODE_TO_GOOGLE_MODEL[mode])
    }
  }

  private mapDestinations(List<LatLong> points) {
    points - null
    points.collect { new LatLng(it.lat, it.lng) }
  }

  private getGoogleStartsForPeople(List<Person> people) {
    people.collect { new LatLng(it.latLong.lat, it.latLong.lng) }
  }

  private addResults(List<Person> people, DistanceMatrix resp, List<LatLng> points, Map<LatLong, List<Integer>> result) {
    points.eachWithIndex { point, i ->
      //Each row in the google model represents a person
      //Each element in the row represents a destination
      def latLong = new LatLong(points[i].lat, points[i].lng)
      def list = []

      people.eachWithIndex { person, j ->
        list << resp.rows[j].elements[i].duration?.inSeconds
      }

      if (result[latLong] == null) {
        result[latLong] = list
      } else {
        result[latLong].addAll(list)
      }
    }
  }

  private Future<DistanceMatrix> doGoogleMapsRequest(LatLng[] origins, LatLng[] points, TravelMode mode) {
    doConcurrentCall(
        newRequest(context)
            .mode(mode)
            .origins(origins)
            .destinations(points)
    )
  }
}