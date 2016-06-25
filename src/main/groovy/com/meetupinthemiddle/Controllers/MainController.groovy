package com.meetupinthemiddle.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ViewResolver
import org.thymeleaf.TemplateEngine

@Controller
class MainController {
  @Autowired
  TemplateEngine templateEngine

  @Autowired
  ViewResolver viewResolver

  @RequestMapping("/")
  String index() {
    "index"
  }

  @RequestMapping("/contact")
  String contact() {
    "contact"
  }

  @RequestMapping("/terms")
  String terms() {
    "terms"
  }

  //If we don't have what is being asked for, show the home page
  @RequestMapping("/*")
  String other() {
    "redirect:/"
  }
}