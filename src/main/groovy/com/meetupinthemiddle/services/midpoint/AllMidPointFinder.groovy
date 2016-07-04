package com.meetupinthemiddle.services.midpoint
import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AllMidPointFinder extends AbstractMidpointFinder{
  @Autowired
  PointFinder [] pointFinders

  @Autowired
  JourneyTimesFinder journeyTimesFinder

  @Autowired
  Geocoder geocoder

  @Override
  Tuple2<CentrePoint, Map<Person, Long>> findMidpoint(final List<Person> people) {
    def latlongs = getMinAndMaxLatLng(people)
    def points =  []

    //TODO - paralellise these calls
    pointFinders.each {
      points += it.doFind(latlongs.first, latlongs.second)
    }

    def journeyTimes = journeyTimesFinder.getJourneyTimes(people, points)
    def centre = minSum(people, journeyTimes)
    def townAndPostCode = geocoder.reverseGeocode(centre.getFirst())

    buildResponse(centre, townAndPostCode, people)
  }
}
