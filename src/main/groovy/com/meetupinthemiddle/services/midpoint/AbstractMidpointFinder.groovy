package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.TownAndPostcode

abstract class AbstractMidpointFinder implements MidpointFinder {

  protected Tuple2<LatLong, List<Integer>> minSum(List<Person> people, Map<LatLong, List<Integer>> allTimes) {
    def lowestSoFar = Integer.MAX_VALUE
    def resultLatLng = null

    allTimes.keySet().forEach({
      def times = allTimes.get(it)
      println("" + it + "" + times)
      if (times.size() == people.size() && !times.contains(null)) {
        def maxDifference = times.max() - times.min()
        if (maxDifference < lowestSoFar) {
          lowestSoFar = maxDifference
          resultLatLng = it
        }
      }
    })
    new Tuple2<>(resultLatLng, allTimes.get(resultLatLng))
  }

  protected Tuple2<LatLong, LatLong> getMinAndMaxLatLng(final List<Person> people) {
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

  protected buildResponse(Tuple2<LatLong, List<Integer>> centre, TownAndPostcode townAndPostCode, List<Person> people) {
    new Tuple2<CentrePoint, Map<Person, Long>>
        (
            CentrePoint.builder()
                .latLong(centre.getFirst())
                .locality(townAndPostCode.town)
                .postCode(townAndPostCode.postcode)
                .build(),
            [people, centre.getSecond()].transpose().collectEntries { it }
        )
  }
}