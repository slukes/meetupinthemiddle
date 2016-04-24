package com.meetupinthemiddle.controllers
import com.meetupinthemiddle.model.*
import com.meetupinthemiddle.services.POIFinder
import com.meetupinthemiddle.services.PointFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Controller
@RequestMapping("/test")
class MainController {
  @Autowired
  private TemplateEngine templateEngine

  @Autowired
  private PointFinder trainStationFinder

  @Autowired
  private POIFinder poiFinderService

  @RequestMapping("/")
  String index() {
    "index"
  }

  @RequestMapping("/search")
  @ResponseBody
  Response search() {
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
      people = [new Person().with{
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
}