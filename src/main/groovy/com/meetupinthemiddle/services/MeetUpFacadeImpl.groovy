package com.meetupinthemiddle.services
import com.meetupinthemiddle.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class MeetUpFacadeImpl implements MeetUpFacade {
  @Autowired
  private TemplateEngine templateEngine

  @Autowired
  private PointFinder trainStationFinder

  @Autowired
  private POIFinder poiFinderService

  @Autowired
  private Geocoder geocoder

  @Override
  Response doSearch(Request request) {
    def midPoint = request.people[0].latLong

    def pois = poiFinderService.findPOIs(midPoint,5, request.poiType)

    def response = fakeResponse(pois, request)

    def ctx = new Context()
    ctx.setVariable("response", response)
    def html = templateEngine.process("result", ctx)

    response.setHtml(html)
    response
  }

  private fakeResponse(List<POI> pois, Request request) {
    new Response().with {
      centrePoint = new CentrePoint().with {
        latLong = request.people[0].latLong
        postCode = "Postcode will be here"
        locality = "Town will be here"
        it
      }

      POIs = pois
      poiType = request.poiType
      people = request.people
      it
    }
  }
}