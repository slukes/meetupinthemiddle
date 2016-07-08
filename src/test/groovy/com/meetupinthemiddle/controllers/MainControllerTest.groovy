package com.meetupinthemiddle.controllers
import com.meetupinthemiddle.MeetupinthemiddleApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringRunner)
@ContextConfiguration(classes = MeetupinthemiddleApplication)
class MainControllerTest {
  @Autowired
  private MainController mainController

  private MockMvc mockMvc

  @Before
  void initMockMvc() {
    mockMvc = MockMvcBuilders.standaloneSetup(mainController).build()
  }

  @Test
  void testIndex() {
    mockMvc.perform(get("/"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("index"))
  }

  @Test
  void testSomeOtherPage() {
    mockMvc.perform(get("/doesn'texist"))
        .andExpect(flash().attribute("errornotfound", "doesn'texist"))
        .andExpect(redirectedUrl("/"))
  }

  @Test
  void testFacadeIsCalled(){

  }
}