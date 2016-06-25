package com.meetupinthemiddle.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.meetupinthemiddle.exceptions.InvalidBodyException
import com.meetupinthemiddle.model.*
import com.meetupinthemiddle.services.POIFinder
import com.meetupinthemiddle.services.PointFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

import javax.validation.Valid

import static com.meetupinthemiddle.model.ErrorResponse.ErrorReason.*
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.web.bind.annotation.RequestMethod.POST

@Controller
class SearchController {
  @Autowired
  private TemplateEngine templateEngine

  @Autowired
  private PointFinder trainStationFinder

  @Autowired
  private POIFinder poiFinderService

  @RequestMapping(path = "/search", method = POST, produces = "application/json")
  @ResponseBody
  Response search(@Valid @RequestBody Request request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new InvalidBodyException(bindingResult.fieldErrors)
    }
    def pois = poiFinderService.findPOIs(
        new LatLong(lat: 51.31627, lng: -0.571981), 5, POIType.RESTAURANT)

    def response = new Response().with {
      centrePoint = new MidPoint().with {
        latLong = new LatLong().with {
          lat = 51.31627
          lng = -0.571981
          it
        }
        postCode = "GU21 6NE"
        locality = "Woking"
        it
      }

      POIs = pois
      poiType = POIType.RESTAURANT
      people = [new Person().with {
        name = "Sam"
        distance = 4.5f
        travelTime = 45
        it
      }, new Person().with {
        name = "George"
        distance = 4.5f
        travelTime = 46
        it
      }]
      it
    }

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
    def response = new ErrorResponse()

    exception.fieldError.forEach({ err ->
      def field = err.field
      if (field != null) {
        if (field.endsWith("poiType")) {
          response.addReason(MISSING_OR_INVALID_POI_TYPE)
        } else if (field.endsWith("name")) {
          response.addReason(MISSING_NAME)
        } else if (field.endsWith("from")) {
          response.addReason(MISSING_FROM)
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
