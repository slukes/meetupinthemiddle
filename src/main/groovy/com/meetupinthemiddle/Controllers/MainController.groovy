package com.meetupinthemiddle.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class MainController {
  @RequestMapping("/")
  String index() {
    "index"
  }

  //If we don't have what is being asked for, show the home page
  @RequestMapping("/{page}")
  String other(@PathVariable("page") String page, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errornotfound", page)
    "redirect:/"
  }

}