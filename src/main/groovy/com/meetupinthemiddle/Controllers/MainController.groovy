package com.meetupinthemiddle.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class MainController {
  @RequestMapping(path = "/")
  String index(Model model, @RequestParam(name = "view", required = false) String view) {
    //Ability to call with url?view=test to enable a param to be passed to GoogleMaps
    //Which allows ensures the map pins etc. are displayed in the DOM which is required
    //By auto tests.  This solution is not quite ideal, however appears to be the most viable.
    model.addAttribute("isTest", "test".equalsIgnoreCase(view))
    "index"
  }

  @RequestMapping("/terms")
  String terms() {
    "terms"
  }

  //If we don't have what is being asked for, show the home page
  @RequestMapping("/{page}")
  String other(@PathVariable("page") String page, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errornotfound", page)
    "redirect:/"
  }
}