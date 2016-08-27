package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.exceptions.NoResultsException
import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.springframework.stereotype.Service

@Service
class MaxMinSumOfDifferenceCentrePicker implements CentrePicker {
  @Override
  Tuple2<CentrePoint, Map<Person, Long>> pickBestPoint(List<Person> people, Map<LatLong, List<Integer>> allTimes) {
    //Remove nulls
    allTimes = allTimes.findAll {
      it?.value?.size() == people.size() && !it.value.contains(null)
    }

    if(allTimes.size() < 1){
      throw new NoResultsException()
    }

    def initialMaxSum =
        allTimes.values().collect {it.sum()}.min() * 1.25

    def initialMaxStandardDeviationPercentage = 0.1

    def candidateTimes =
        getCandidatePoints(allTimes, initialMaxSum, initialMaxStandardDeviationPercentage)

    def resultLatLng = getBestCandidateBasedOnStDev(candidateTimes)

    new Tuple2<>(resultLatLng, allTimes.get(resultLatLng))
  }

  private getBestCandidateBasedOnStDev(candidateTimes) {
    def lowestSoFar = Integer.MAX_VALUE
    def resultLatLng = null

    candidateTimes.each {
      def times = it.value
      def stDev = new DescriptiveStatistics(times as double[]).getStandardDeviation()
      if (lowestSoFar > stDev) {
        lowestSoFar = stDev
        resultLatLng = it.key
      }
    }

    resultLatLng
  }

  private getCandidatePoints(Map<LatLong, List<Integer>> allTimes, maxTotalAllowable, maxPercentStDev) {
    def candidateTimes = allTimes.findAll {
      def sum = it.value.sum()
      def stDev = new DescriptiveStatistics(it.value as double[]).standardDeviation
      sum < maxTotalAllowable && stDev / sum < maxPercentStDev
    }

    if(candidateTimes.size() > 0){
      candidateTimes
    } else {
      getCandidatePoints(allTimes, maxTotalAllowable * 1.01, maxPercentStDev + 0.01)
    }
  }
}