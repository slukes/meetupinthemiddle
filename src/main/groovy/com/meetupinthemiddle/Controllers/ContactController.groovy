package com.meetupinthemiddle.controllers
import com.meetupinthemiddle.model.ContactFormBean
import com.meetupinthemiddle.services.contact.ContactService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.POST

@Controller
class ContactController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContactController)

  @Autowired
  ContactService contactService

  @RequestMapping(path = "contact", method = GET)
  String contact(Model model) {
    model.addAttribute("contactBean", new ContactFormBean())
    "contact"
  }

  @RequestMapping(path = "contact", method = POST)
  String sendContactForm(
      @Valid @ModelAttribute("contactBean") ContactFormBean contactBean, BindingResult bindingResult, HttpServletRequest httpServletRequest,
      RedirectAttributes redirectAttributes, Model model) {

    if (bindingResult.hasErrors()) {
      return "contact"
    }

    def success = contactService.sendMessage(contactBean)

    if (success) {
      redirectAttributes.addFlashAttribute("contact_success", true)
      "redirect:/"
    } else {
      handleOtherError(null, model)
    }
  }

  @ExceptionHandler(Exception)
  String handleOtherError(Exception e, Model model) {
    LOGGER.error("Error occured serving contact page. Form bean is ${model.asMap()?.get("contactBean")}", e)
    model.addAttribute("error", "UNKNOWN")
    "contact"
  }
}
