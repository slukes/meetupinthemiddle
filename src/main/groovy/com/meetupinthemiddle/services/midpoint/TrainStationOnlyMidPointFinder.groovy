package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

//Iteration that will be replaced.
@Service
class TrainStationOnlyMidPointFinder implements MidpointFinder {
  @Autowired
  PointFinder trainStationFinder

  @Autowired
  JourneyTimesFinder journeyTimesFinder

  @Autowired
  Geocoder geocoder

  @Override
  CentrePoint findMidpoint(final List<Person> people) {
    def latlongs = getMinAndMaxLatLng(people)
    def stations = trainStationFinder.doFind(latlongs.first, latlongs.second)
    def journeyTimes = journeyTimesFinder.getJourneyTimes(people, stations)
    def centre = minSum(people, journeyTimes)
    def townAndPostCode = geocoder.reverseGeocode(centre)
    CentrePoint.builder()
        .latLong(centre)
        .locality(townAndPostCode.town)
        .postCode(townAndPostCode.postcode)
        .build()
  }

  private LatLong minSum(List<Person> people, Map<LatLong, List<Integer>> allTimes) {
    def lowestSoFar = Integer.MAX_VALUE
    def result = null

    allTimes.keySet().forEach({
      def times = allTimes.get(it)
      println("" + it + "" + times)
      if (times.size() == people.size()) {
        def maxDifference = times.max() - times.min()
        if (maxDifference < lowestSoFar) {
          lowestSoFar = maxDifference
          result = it
        }
      }
    })
    result
  }

  private LatLong findLowestTimeForAll(List<Person> people, Map<LatLong, List<Integer>> allTimes) {
    def result = null
    def minSum = Integer.MAX_VALUE
    for (LatLong eachLatLong : allTimes.keySet()) {
      if (allTimes.get(eachLatLong).size() == people.size()) {
        def sum = allTimes.get(eachLatLong).sum()
        if (sum < minSum) {
          sum = minSum
          result = eachLatLong
        }
      }
    }
    result
  }

  private Tuple2<LatLong, LatLong> getMinAndMaxLatLng(final List<Person> people) {
    def min = new LatLong()
    def max = new LatLong()

    people.forEach(
        {
          if (min.lat == 0 || it.latLong.lat < min.lat) {
            min.lat = it.latLong.lat
          }

          if (min.lng == 0 || it.latLong.lng < min.lng) {
            min.lng = it.latLong.lng
          }

          if (max.lat == 0 || it.latLong.lat > max.lat) {
            max.lat = it.latLong.lat
          }

          if (max.lng == 0 || it.latLong.lng > max.lng) {
            max.lng = it.latLong.lng
          }
        }
    )

    new Tuple2<LatLong, LatLong>(min, max)
  }
}