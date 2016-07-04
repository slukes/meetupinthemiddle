package com.meetupinthemiddle.services.journeytimes

import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DistanceMatrix
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors

import static com.google.maps.model.TravelMode.TRANSIT
import static com.meetupinthemiddle.model.TransportMode.DRIVING
import static com.meetupinthemiddle.model.TransportMode.PUBLIC

@Service
class GoogleMapsJourneyTimesFinder implements JourneyTimesFinder {
  private static final int MAX_PER_REQ = 25
  @Autowired
  GeoApiContext context

  private static final def TRANSPORT_MODE_TO_GOOGLE_MODEL =
      [(DRIVING): TravelMode.DRIVING, (PUBLIC): TRANSIT]

  private ExecutorCompletionService<DistanceMatrix> executor = new ExecutorCompletionService<>(Executors.newCachedThreadPool())

  @Override
  Map<LatLong, List<Integer>> getJourneyTimes(List<Person> people, List<LatLong> points) {
    def result = [:]
    def publicResults = []
    def drivingResults = []

    Person[] publicPeople = people.findAll { it.transportMode == PUBLIC }
    Person[] drivingPeople = people.findAll { it.transportMode == DRIVING }
    LatLng[] publicGmStarts = getGoogleStartsForPeople(publicPeople)
    LatLng[] drivingGmStarts = getGoogleStartsForPeople(drivingPeople)
    LatLng[] gmDestinations = mapDestinations(points)
    def destLength = gmDestinations.length

    def i = 0
    while (i < destLength - 1) {
      def batchSize = destLength - i >= MAX_PER_REQ ? i + MAX_PER_REQ : destLength % 25
      LatLng[] batch = gmDestinations[i .. i + batchSize - 1]
      if(publicGmStarts.length > 0) {
        publicResults << new Tuple2<>(executor.submit { doBatch(publicGmStarts, batch, PUBLIC) }, batch)
      }

      if(drivingGmStarts.length > 0) {
        drivingResults << new Tuple2<>(executor.submit { doBatch(drivingGmStarts, batch, DRIVING) }, batch)
      }

      i += 25
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
    points.stream()
        .map({ it != null ? new LatLng(it.lat, it.lng) : null })
        .filter({ it != null })
        .toArray()
  }

  private getGoogleStartsForPeople(Person[] people) {
    people.collect { new LatLng(it.latLong.lat, it.latLong.lng) }
  }

  private addResults(Person[] people, DistanceMatrix resp, LatLng[] points, Map<LatLong, List<Integer>> result) {
    for (def i = 0; i < points.length; i++) {
      //Each row in the google model represents a person
      //Each element in the row represents a destination
      def latLong = new LatLong(points[i].lat, points[i].lng)
      def list = []
      for (def j = 0; j < people.size(); j++) {
        list.add(resp.rows[j].elements[i].duration?.inSeconds)
      }
      if (result[latLong] == null) {
        result[latLong] = list
      } else {
        result[latLong].addAll(list)
      }
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