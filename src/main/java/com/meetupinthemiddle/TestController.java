package com.meetupinthemiddle;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Sam Lukes on 02/04/16.
 */
@Controller
public class TestController {
  @RequestMapping("/test/{text}")
  public String index(@PathVariable("text") String text, Model model){
    model.addAttribute("text", text);
    return "index";
  }
}
