package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import org.springframework.stereotype.Service

@Service
class MinSumOfDifferenceCentrePicker implements CentrePicker{
  @Override
  Tuple2<CentrePoint, Map<Person, Long>> pickBestPoint(List<Person> people, Map<LatLong, List<Integer>> allTimes) {
    def lowestSoFar = Integer.MAX_VALUE
    def resultLatLng = null

    allTimes.keySet().each({
      def times = allTimes.get(it)
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
}
