package com.meetupinthemiddle

import com.meetupinthemiddle.model.*

class Stubs {
  static aRequestObj() {
    def test = Request.builder()
        .people(twoPeople())
        .poiType(POIType.MEETING)
        .build()
    test
  }

  static twoPeople() {
    [Person.builder()
         .withName("Sam")
         .withFrom("Reading")
         .withTransportMode(TransportMode.PUBLIC)
         .withLatLong(new LatLong(123.1, 234.2))
         .build()
     , Person.builder()
         .withName("George")
         .withFrom("Woking")
         .withLatLong(new LatLong(123.1, 234.2))
         .withTransportMode(TransportMode.DRIVING)
         .build()]
  }

  static aCentrePointAndJourneyTimes() {
    def request = aRequestObj()
    new Tuple2<CentrePoint, Map<Person, Long>>(CentrePoint.builder()
        .latLong(new LatLong(1f, 2f))
        .locality("Guildford")
        .postCode("GU22 7UF")
        .build(), [(request.people[0]): 10, (request.people[1]): 20])
  }

  static Response aResponseObj() {
    Response.builder()
        .centrePoint(aCentrePointAndJourneyTimes().first)
        .people(twoPeople() as Person[])
        .POIs([aPoi(), aPoi()] as POI[])
        .poiType(POIType.RESTAURANT)
        .build()
  }

  private static aPoi() {
    POI.builder()
        .address("an address")
        .distanceFromCentrePoint(5)
        .imageUrl("http://url")
        .latLong(new LatLong(123, 456))
        .distanceFromCentrePoint(5)
        .name("a name")
        .build()
  }
}
