package com.meetupinthemiddle

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POIType
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.model.TransportMode

class Stubs {
   static aRequestObj() {
    def test = Request.builder()
        .people([Person.builder()
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
                     .build()])
        .poiType(POIType.MEETING)
        .build()
    test
  }

  static aCentrePointAndJourneyTimes(){
    def request = aRequestObj()
    new Tuple2<CentrePoint, Map<Person, Long>>(CentrePoint.builder()
        .latLong(new LatLong(1f, 2f))
        .locality("Guildford")
        .postCode("GU22 7UF")
        .build(), [(request.people[0]): 10, (request.people[1]) : 20])
  }
}
