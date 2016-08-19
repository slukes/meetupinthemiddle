package com.meetupinthemiddle.services.midpoint
import com.meetupinthemiddle.model.*
import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AllMidPointFinder implements MidpointFinder {
  @Autowired
  PointFinder[] pointFinders

  @Autowired
  JourneyTimesFinder journeyTimesFinder

  @Autowired
  Geocoder geocoder

  @Autowired //Using class not interface since I want to swap later, with out confusing Spring
  MaxMinSumOfDifferenceCentrePicker centrePicker

  Tuple2<CentrePoint, Map<Person, Long>> findMidpoint(final List<Person> people) {
    def boundingBox = new BoundingBox(people.collect{it.latLong})
    def points = []

    pointFinders.each {
      points += it.doFind(boundingBox)
    }

    def journeyTimes = journeyTimesFinder.getJourneyTimes(people, points)
    def centre = centrePicker.pickBestPoint(people, journeyTimes)
    def townAndPostCode = geocoder.reverseGeocode(centre.getFirst())

    buildResponse(centre, townAndPostCode, people)
  }

  private buildResponse(Tuple2<LatLong, List<Integer>> centre, TownAndPostcode townAndPostCode, List<Person> people) {
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