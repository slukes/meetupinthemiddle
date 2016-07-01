package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.model.Response
import com.meetupinthemiddle.services.midpoint.MidpointFinder
import com.meetupinthemiddle.services.poi.POIFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MeetUpFacadeImpl implements MeetUpFacade {
  @Autowired
  MidpointFinder midPointFinder

  @Autowired
  private POIFinder poiFinderService

  @Override
  Response doSearch(Request request) {
    def midPoint = midPointFinder.findMidpoint(request.people as List<Person>)
    def pois = poiFinderService.findPOIs(midPoint.latLong,5, request.poiType)

    Response.builder()
        .people(request.people)
        .centrePoint(midPoint)
        .poiType(request.poiType)
        .POIs(pois)
        .build()
  }
}