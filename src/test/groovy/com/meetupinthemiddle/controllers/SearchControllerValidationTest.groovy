package com.meetupinthemiddle.controllers
import com.meetupinthemiddle.MeetupinthemiddleApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static com.meetupinthemiddle.model.ErrorResponse.ErrorReason.*
import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner)
@ContextConfiguration(classes = MeetupinthemiddleApplication)
class SearchControllerValidationTest {
  public static final String CONTROLLER_URL = "/search"
  public static final String JSON_DIR = 'src/test/resources/modeljson'

  @Autowired
  private SearchController searchController
  private MockMvc mockMvc

  @Before
  void initMockMvc() {
    mockMvc = MockMvcBuilders.standaloneSetup(searchController).build()
  }

  @Test
  void testMissingPoiType() {
    testForError("missingpoi.json", MISSING_OR_INVALID_POI_TYPE)
  }

  @Test
  void testInvalidPoiType() {
    testForError("invalidpoi.json", MISSING_OR_INVALID_POI_TYPE)
  }

  @Test
  void testMissingFrom() {
    testForError("missingfrom.json", MISSING_FROM)
  }

  @Test
  void testMissingName() {
    testForError("missingname.json", MISSING_NAME)
  }

  @Test
  void testOnlyOnePerson() {
    testForError("oneperson.json", NOT_ENOUGH_PEOPLE)
  }

  @Test
  void testInvalidTransportMode() {
    testForError("invalidtransport.json", MISSING_OR_INVALID_TRANSPORT_MODE)
  }

  @Test
  void testMissingTransportMode() {
    testForError("missingtransport.json", MISSING_OR_INVALID_TRANSPORT_MODE)
  }

  private testForError(fileName, errorReason) {
    mockMvc.perform(
        post(CONTROLLER_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new File("$JSON_DIR/$fileName").bytes))
        .andExpect(status().is(400))
        .andExpect(MockMvcResultMatchers.jsonPath("errorReasons[0]", equalTo(errorReason.toString())))
  }
}