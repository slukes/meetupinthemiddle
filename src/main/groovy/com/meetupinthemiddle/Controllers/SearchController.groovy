package com.meetupinthemiddle.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.meetupinthemiddle.exceptions.InvalidBodyException
import com.meetupinthemiddle.model.ErrorResponse
import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.model.Response
import com.meetupinthemiddle.services.MeetUpFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

import static com.meetupinthemiddle.model.ErrorResponse.ErrorReason.*
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.web.bind.annotation.RequestMethod.POST

@Controller
class SearchController {
  @Autowired
  private MeetUpFacade meetUpFacade

  @RequestMapping(path = "/search", method = POST, produces = "application/json")
  @ResponseBody
  Response search(@Valid @RequestBody Request request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new InvalidBodyException(bindingResult.fieldErrors)
    }

    meetUpFacade.doSearch(request)
  }

  @ExceptionHandler(InvalidBodyException)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  ErrorResponse handleBodyError(InvalidBodyException exception) {
    def response = new ErrorResponse()

    exception.fieldError.forEach({
      err ->
        def field = err.field
        if (field != null) {
          if (field.endsWith("poiType")) {
            response.addReason(MISSING_OR_INVALID_POI_TYPE)
          } else if (field.endsWith("name")) {
            response.addReason(MISSING_NAME)
          } else if (field.endsWith("from")) {
            response.addReason(MISSING_FROM)
          } else if (field.endsWith("latLong")) {
            response.addReason(MISSING_LAT_LONG)
          } else if (field.endsWith("transportMode")) {
            response.addReason(MISSING_OR_INVALID_TRANSPORT_MODE)
          }
        }
    })

    if (response.errorReasons.size() < 1) {
      response.addReason(UNKNOWN)
    }

    response
  }

  @ExceptionHandler(HttpMessageNotReadableException)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  ErrorResponse handleInvalidEnumConstant(HttpMessageNotReadableException exception) {
    def response = new ErrorResponse()
    if (exception.cause instanceof InvalidFormatException) {
      if (exception.message != null && exception.message.contains("Enum")) {
        if (exception.message.contains("POIType")) {
          response.addReason(MISSING_OR_INVALID_POI_TYPE)
        } else if (exception.message.contains("TransportMode")) {
          response.addReason(MISSING_OR_INVALID_TRANSPORT_MODE)
        }
      }
    }

    if (response.errorReasons.size() < 1) {
      response.addReason(UNKNOWN)
    }
    response
  }
}