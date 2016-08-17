package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.TownAndPostcode
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
  MinSumOfDifferenceCentrePicker centrePicker

  Tuple2<CentrePoint, Map<Person, Long>> findMidpoint(final List<Person> people) {
    def latlongs = getMinAndMaxLatLng(people)
    def points = []

    pointFinders.each {
      points += it.doFind(latlongs.first, latlongs.second)
    }

    def journeyTimes = journeyTimesFinder.getJourneyTimes(people, points)
    def centre = centrePicker.pickBestPoint(people, journeyTimes)
    def townAndPostCode = geocoder.reverseGeocode(centre.getFirst())

    buildResponse(centre, townAndPostCode, people)
  }

  private Tuple2<LatLong, LatLong> getMinAndMaxLatLng(final List<Person> people) {
    def min = new LatLong(Double.MAX_VALUE, Double.MAX_VALUE)
    def max = new LatLong(-Double.MAX_VALUE, -Double.MAX_VALUE) //Double.MIN_VALUE is actually positive!

    people.each
        {
          if (it.latLong.lat < min.lat) {
            min.lat = it.latLong.lat
          }

          if (it.latLong.lng < min.lng) {
            min.lng = it.latLong.lng
          }

          if (it.latLong.lat > max.lat) {
            max.lat = it.latLong.lat
          }

          if (it.latLong.lng > max.lng) {
            max.setLng(it.latLong.lng)
          }
        }

    new Tuple2<LatLong, LatLong>(min, max)
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