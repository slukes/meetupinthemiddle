package com.meetupinthemiddle.services.midpoint.distributed
import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import com.meetupinthemiddle.services.midpoint.AbstractMidpointFinder
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
//Iteration that will be replaced.
@Service
class DistributedOnlyMidpointFinder extends AbstractMidpointFinder {
  @Autowired
  PointFinder distributedPointFinder

  @Autowired
  JourneyTimesFinder journeyTimesFinder

  @Autowired
  Geocoder geocoder

  @Override
  Tuple2<CentrePoint, Map<Person, Long>> findMidpoint(final List<Person> people) {
    def latlongs = getMinAndMaxLatLng(people)
    def points = distributedPointFinder.doFind(latlongs.first, latlongs.second)
    def journeyTimes = journeyTimesFinder.getJourneyTimes(people, points)
    def centre = minSum(people, journeyTimes)
    def townAndPostCode = geocoder.reverseGeocode(centre.getFirst())

    buildResponse(centre, townAndPostCode, people)
  }
}