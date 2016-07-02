package com.meetupinthemiddle.controllers
import org.junit.Test
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap

import static org.junit.Assert.assertEquals

class MainControllerTest {
  MainController mainController = new MainController()

  @Test
  void testRedirectAttrIsAdded(){
    //Given
    def redirectAttributes = new RedirectAttributesModelMap()
    //When
    mainController.other("some-other-page", redirectAttributes)

    //Then
    assertEquals("some-other-page",
        redirectAttributes.getFlashAttributes().get("errornotfound") as String)
  }
}
