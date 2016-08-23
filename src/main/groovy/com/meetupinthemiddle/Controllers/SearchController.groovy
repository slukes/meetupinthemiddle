package com.meetupinthemiddle.controllers
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.meetupinthemiddle.exceptions.InvalidBodyException
import com.meetupinthemiddle.exceptions.OverQuotaException
import com.meetupinthemiddle.model.ErrorResponse
import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.model.Response
import com.meetupinthemiddle.services.MeetUpFacade
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

import static com.meetupinthemiddle.model.ErrorResponse.ErrorReason.*
import static org.springframework.http.HttpStatus.*
import static org.springframework.web.bind.annotation.RequestMethod.POST

@Controller
class SearchController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchController)
  @Autowired
  private MeetUpFacade meetUpFacade

  @Autowired
  private TemplateEngine templateEngine

  @RequestMapping(path = "/search", method = POST, produces = "application/json")
  @ResponseBody
  Response search(@Valid @RequestBody Request request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new InvalidBodyException(bindingResult.fieldErrors)
    }

    performSearch(request)
  }

  private Response performSearch(Request request) {
    def response = meetUpFacade.doSearch(request)

    def ctx = new Context()
    ctx.setVariable("response", response)
    def html = templateEngine.process("result", ctx)

    response.setHtml(html)
    response
  }

  @ExceptionHandler(InvalidBodyException)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  ErrorResponse handleBodyError(InvalidBodyException exception) {
    new ErrorResponse().with {
      exception.fieldError.forEach({
        err ->
          def field = err.field
          if (field.endsWith("poiType")) {
            it.addReason(MISSING_OR_INVALID_POI_TYPE)
          } else if (field.endsWith("people")) {
            it.addReason(NOT_ENOUGH_PEOPLE)
          } else if (field.endsWith("name")) {
            it.addReason(MISSING_NAME)
          } else if (field.endsWith("from")) {
            it.addReason(MISSING_FROM)
          } else if (field.endsWith("transportMode")) {
            it.addReason(MISSING_OR_INVALID_TRANSPORT_MODE)
          }
      })

      if (it.errorReasons.size() < 1) {
        it.addReason(UNKNOWN)
      }
      it
    }
  }

  //This deals with any dodgey AJAX requests with invalid enum constants - shouldn't really be used under normal operation
  //However, if this controller later becomes part of a webservice to power an app, it could be hit during develop time.
  @ExceptionHandler(HttpMessageNotReadableException)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  ErrorResponse handleInvalidEnumConstant(HttpMessageNotReadableException exception) {
    new ErrorResponse().with {
      if (exception.cause instanceof InvalidFormatException) {
        if (exception?.message?.contains("Enum")) {

          if (exception.message.contains("POIType")) {
            addReason(MISSING_OR_INVALID_POI_TYPE)
          }

          if (exception.message.contains("TransportMode")) {
            addReason(MISSING_OR_INVALID_TRANSPORT_MODE)
          }
        }
      }

      if (errorReasons.size() < 1) {
        addReason(UNKNOWN)
      }
      it
    }
  }

  @ExceptionHandler(OverQuotaException)
  @ResponseBody
  @ResponseStatus(SERVICE_UNAVAILABLE)
  ErrorResponse handleOverQuota(Exception e) {
    new ErrorResponse().with {
      addReason(OVER_QUOTA)
      it
    }
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  ErrorResponse handleUnknownError(Exception e, HttpServletRequest req) {
    LOGGER.error("Error serving $req.requestURI", e)
    new ErrorResponse().with {
      addReason(UNKNOWN)
      it
    }
  }
}