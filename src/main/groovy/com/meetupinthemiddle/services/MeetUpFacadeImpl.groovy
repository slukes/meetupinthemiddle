package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.model.Response
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import com.meetupinthemiddle.services.midpoint.MidpointFinder
import com.meetupinthemiddle.services.poi.POIFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MeetUpFacadeImpl implements MeetUpFacade {
  @Autowired
  MidpointFinder allMidpointFinder

  @Autowired
  private POIFinder poiFinderService

  @Autowired
  private JourneyTimesFinder journeyTimesFinder

  @Autowired
  private Geocoder geocoder

  @Override
  Response doSearch(Request request) {
    setLatLongs(request.people)
    def midPointAndTimes = allMidpointFinder.findMidpoint(request.people as List<Person>)
    def midPoint = midPointAndTimes.getFirst()
    def pois = poiFinderService.findPOIs(midPoint.latLong, 5, request.poiType)

    setTravelTimes(request.people, midPointAndTimes.second)

    Response.builder()
        .people(request.people as Person[])
        .centrePoint(midPoint)
        .poiType(request.poiType)
        .POIs(pois)
        .build()
  }

  private setTravelTimes(List<Person> people, Map<Person, Long> times) {
    people.each {
      it.travelTime = times.get(it) ? times.get(it) / 60 : 0
    }
  }

  private setLatLongs(final List<Person> people) {
    people.each {
      if (it.latLong == null) {
        it.latLong = geocoder.geocode(it.from)
      }
    }
  }
}