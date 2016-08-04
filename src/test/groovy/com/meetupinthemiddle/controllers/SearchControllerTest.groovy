package com.meetupinthemiddle.controllers
import com.meetupinthemiddle.MeetupinthemiddleApplication
import com.meetupinthemiddle.exceptions.InvalidBodyException
import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.services.MeetUpFacade
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.thymeleaf.TemplateEngine

import static com.meetupinthemiddle.Stubs.aResponseObj
import static com.meetupinthemiddle.model.ErrorResponse.ErrorReason.*
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.not
import static org.mockito.Matchers.any
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner)
@ContextConfiguration(classes = MeetupinthemiddleApplication)
class SearchControllerTest {
  public static final String CONTROLLER_URL = "/search"
  public static final String JSON_DIR = 'src/test/resources/modeljson'

  @InjectMocks
  private SearchController searchController = new SearchController()

  @Autowired
  private TemplateEngine templateEngine

  private MockMvc mockMvc

  @Mock
  private MeetUpFacade meetUpFacade

  @Before
  void initMockMvc() {
    MockitoAnnotations.initMocks(SearchControllerTest)
    searchController.templateEngine = this.templateEngine
    mockMvc = MockMvcBuilders.standaloneSetup(searchController).build()
  }

  @Test
  void testMissingPoiType() {
    testForError("missingpoi.json", MISSING_OR_INVALID_POI_TYPE, 400)
  }

  @Test
  void testInvalidPoiType() {
    testForError("invalidpoi.json", MISSING_OR_INVALID_POI_TYPE, 400)
  }

  @Test
  void testMissingFrom() {
    testForError("missingfrom.json", MISSING_FROM, 400)
  }

  @Test
  void testMissingName() {
    testForError("missingname.json", MISSING_NAME, 400)
  }

  @Test
  void testOnlyOnePerson() {
    testForError("oneperson.json", NOT_ENOUGH_PEOPLE, 400)
  }

  @Test
  void testInvalidTransportMode() {
    testForError("invalidtransport.json", MISSING_OR_INVALID_TRANSPORT_MODE, 400)
  }

  @Test
  void testUnknown400Error() {
    when(meetUpFacade.doSearch(any(Request)))
        .thenThrow(new HttpMessageNotReadableException("A random Enum problem"))
    testForError("request.json", UNKNOWN, 400)
  }

  @Test
  void testUnknownBody400Error() {
    when(meetUpFacade.doSearch(any(Request)))
        .thenThrow(new InvalidBodyException([]))
    testForError("request.json", UNKNOWN, 400)
  }

  @Test
  void testMissingTransportMode() {
    testForError("missingtransport.json", MISSING_OR_INVALID_TRANSPORT_MODE, 400)
  }

  @Test
  void testSuccess(){
    //Given
    when(meetUpFacade.doSearch(any(Request)))
      .thenReturn(aResponseObj())

    //When / then
    mockMvc.perform(
        post(CONTROLLER_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new File("$JSON_DIR/request.json").bytes))
        .andExpect(status().is(200))
        .andExpect(jsonPath("html", not(null)))
  }

  @Test
  void testFail(){
    //Given
    when(meetUpFacade.doSearch(any(Request)))
        .thenThrow(new RuntimeException())

    testForError("request.json", UNKNOWN, 500)
  }

  private testForError(fileName, errorReason, int code) {
    mockMvc.perform(
        post(CONTROLLER_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new File("$JSON_DIR/$fileName").bytes))
        .andExpect(status().is(code))
        .andExpect(jsonPath("errorReasons[0]", equalTo(errorReason.toString())))
  }
}