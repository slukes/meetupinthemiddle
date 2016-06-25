package com.meetupinthemiddle.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class WebAppController {
  @RequestMapping("/")
  String index() {
    "index"
  }

  @RequestMapping

  @RequestMapping("/contact")
  String contact(){
    "contact"
  }

  @RequestMapping("/terms")
  String terms(){
    "terms"
  }

}