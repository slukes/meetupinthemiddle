package com.meetupinthemiddle;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Sam Lukes on 02/04/16.
 */
@Controller
public class TestController {
  @RequestMapping("/")
  public String index(Model model){
    return "index";
  }
}
