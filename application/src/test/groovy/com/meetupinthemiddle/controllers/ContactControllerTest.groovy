package com.meetupinthemiddle.controllers

import com.meetupinthemiddle.model.ContactFormBean
import com.meetupinthemiddle.services.contact.ContactService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.view.InternalResourceViewResolver

import static org.mockito.Matchers.any
import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringRunner)
class ContactControllerTest {

  private MockMvc mockMvc

  @InjectMocks
  private ContactController contactController = new ContactController()

  @Mock
  private ContactService contactService

  private static final String CONTROLLER_URL = "/contact"

  @Before
  void init() {
    initMocks(ContactControllerTest)

    def viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/templates/");
    viewResolver.setSuffix(".html");

    mockMvc = MockMvcBuilders.standaloneSetup(contactController)
        .setViewResolvers(viewResolver)
        .build()
  }

  @Test
  void testInvalidHaveAttributesAddedToModel() {
    mockMvc.perform(post(CONTROLLER_URL)
        .contentType(APPLICATION_FORM_URLENCODED))
    //No attributes set so expect complaints about being null
        .andExpect(status().is(200))
        .andExpect(model().attributeHasFieldErrorCode("contactBean", "email", "NotNull"))
        .andExpect(model().attributeHasFieldErrorCode("contactBean", "name", "NotNull"))
        .andExpect(model().attributeHasFieldErrorCode("contactBean", "subject", "NotNull"))
        .andExpect(model().attributeHasFieldErrorCode("contactBean", "message", "NotNull"))
        .andExpect(view().name("contact"))
  }

  @Test
  void succesForwardsToHomePage() {
    when(contactService.sendMessage(any(ContactFormBean)))
        .thenReturn(true)

    mockMvc.perform(post(CONTROLLER_URL)
        .contentType(APPLICATION_FORM_URLENCODED)
        .param("name", "foo")
        .param("email", "foo@bar.com")
        .param("subject", "bar")
        .param("message", "baz"))
        .andExpect(status().is(302))
        .andExpect(view().name("redirect:/"))
  }
}
