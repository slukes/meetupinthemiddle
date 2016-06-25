package com.meetupinthemiddle.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class RequestTest {
  @Test
  void test(){
    def test = Request.builder()
        .people([Person.builder()
                     .withName("Sam")
                     .withFrom("Reading")
                     .withTransportMode(TransportMode.PUBLIC)
                     .build()
                 , Person.builder()
                     .withName("George")
                     .withFrom("Woking")
                     .withTransportMode(TransportMode.DRIVING)
                     .build()] as Person[]).poiType(POIType.MEETING)
        .build()

    ObjectMapper objectMapper = new ObjectMapper()
    println objectMapper.writeValueAsString(test)
  }
}
