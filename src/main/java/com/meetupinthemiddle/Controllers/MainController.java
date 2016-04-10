package com.meetupinthemiddle.Controllers;

import com.meetupinthemiddle.Model.POI;
import com.meetupinthemiddle.Model.POIType;
import com.meetupinthemiddle.Model.Response;
import com.meetupinthemiddle.Services.POIFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.List;

import static com.meetupinthemiddle.Model.CentrePoint.CentrePointBuilder.aCentrePoint;
import static com.meetupinthemiddle.Model.LatLong.LatLongBuilder.aLatLong;
import static com.meetupinthemiddle.Model.Person.PersonBuilder.aPerson;
import static com.meetupinthemiddle.Model.Response.ResponseBuilder.aResponse;

@Controller
public class MainController {
  @Autowired
  private TemplateEngine templateEngine;

  @Autowired
  POIFinderService poiFinderService;

  @RequestMapping("/")
  public String index() {
    return "index";
  }

  @RequestMapping("/search")
  public
  @ResponseBody
  Response search() throws Exception {

    List<POI> pois = poiFinderService.findPOIs(aLatLong()
        .withLat(51.31627)
        .withLng(-0.571981)
        .build(), 10, POIType.RESTAURANT);

    Response response = aResponse()
        .withCentrePoint(aCentrePoint()
            .withLatLong(aLatLong()
                .withLat(51.31627)
                .withLng(-0.571981)
                .build()
            ).withPostCode("GU21 6NE")
            .withLocality("Woking")
            .build()
        )
        .withLocations(pois)
        .withPoiType(POIType.RESTAURANT)
        .withPeople(Collections.singletonList(aPerson()
            .withName("Sam")
            .withDistance(4.5f)
            .withTravelTime(45)
            .build())
        )
        .withPeople(Collections.singletonList(aPerson()
            .withName("George")
            .withTravelTime(46)
            .build())
        )
        .build();

    Context ctx = new Context();
    ctx.setVariable("response", response);
    String html = templateEngine.process("result", ctx);

    response.setHtml(html);
    return response;
  }
}
