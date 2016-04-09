package com.meetupinthemiddle.Controllers;

import com.meetupinthemiddle.Model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static com.meetupinthemiddle.Model.Address.AddressBuilder.anAddress;
import static com.meetupinthemiddle.Model.CentrePoint.CentrePointBuilder.aCentrePoint;
import static com.meetupinthemiddle.Model.LatLong.LatLongBuilder.aLatLong;
import static com.meetupinthemiddle.Model.Location.LocationBuilder.aLocation;
import static com.meetupinthemiddle.Model.Response.ResponseBuilder.aResponse;

/**
 * Created by Sam Lukes on 02/04/16.
 */
@Controller
public class MainController {
  @Autowired
  private TemplateEngine templateEngine;

  @RequestMapping("/")
  public String index() {
    return "index";
  }

  @RequestMapping("/search")
  public @ResponseBody Response search(Model model) {

    Response response = aResponse()
        .withCentrePoint(aCentrePoint()
            .withLatLong(aLatLong()
                .withLat(51.3162688)
                .withLng(0.5719808)
                .build()
            ).withPostCode("GU21 6NE")
            .build()
        )
        .withLocation(aLocation()
            .withAddress(anAddress()
                .withAddressLine1("Hilbre")
                .withAddressLine2("Maybourne Rise")
                .withPostalTown("Woking")
                .withCounty("Surrey")
                .withPostcode("GU22 0SH")
                .build())
            .withDistanceFromCentrePoint(5.2f)
            .withEmailAddress("brian@goldsworthbooks.com")
            .withGeocode(aLatLong()
                .withLat(51.2880247)
                .withLng(0.5720539)
                .build()
            ).withName("Goldsworth Books")
            .withWebsite("http://goldsworthbooks.com")
            .build()
        )
        .build();

    Context ctx = new Context();
    ctx.setVariable("response", response);
    ctx.setVariable("hi", "hello");
    String html =  templateEngine.process("result",ctx);

    response.setHtml(html);
    return response;
  }
}
