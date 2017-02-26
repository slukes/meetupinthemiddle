package com.meetupinthemiddle.model
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import static com.meetupinthemiddle.Stubs.aRequestObj

class RequestTest {

  public static final String JSON_DIR = 'src/test/resources/modeljson'

  @Test
  void testMarshall() {
    //Given
    def reqObj = aRequestObj()
    def excpectedJson = new File("$JSON_DIR/request.json").text

    //When
    def marshalledJson = new ObjectMapper().writeValueAsString(reqObj)

    //Then
    JSONAssert.assertEquals(excpectedJson, marshalledJson, true)
  }


}
