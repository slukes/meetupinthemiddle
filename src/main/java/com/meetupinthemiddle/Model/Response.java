package com.meetupinthemiddle.Model;

import java.util.ArrayList;
import java.util.List;

public class Response {
  private String html;
  private List<Location> locations;
  private CentrePoint centrePoint;
  private ResponseType type;

  public List<Location> getLocations() {
    return locations;
  }

  public void setLocations(final List<Location> locations) {
    this.locations = locations;
  }

  public CentrePoint getCentrePoint() {
    return centrePoint;
  }

  public void setCentrePoint(final CentrePoint centrePoint) {
    this.centrePoint = centrePoint;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(final String html) {
    this.html = html;
  }

  public ResponseType getType() {
    return type;
  }

  public void setType(final ResponseType type) {
    this.type = type;
  }


  public static class ResponseBuilder {
    private CentrePoint centrePoint;
    private List<Location> locations = new ArrayList<>();
    private String html;
    private ResponseType type;

    private ResponseBuilder() {
    }

    public static ResponseBuilder aResponse() {
      return new ResponseBuilder();
    }

    public ResponseBuilder withCentrePoint(CentrePoint centrePoint) {
      this.centrePoint = centrePoint;
      return this;
    }

    public ResponseBuilder withLocation(Location location) {
      this.locations.add(location);
      return this;
    }

    public ResponseBuilder withHtml(String html){
      this.html = html;
      return this;
    }

    public ResponseBuilder withType(ResponseType type) {
      this.type = type;
      return this;
    }

    public Response build() {
      Response response = new Response();
      response.setLocations(locations);
      response.setCentrePoint(centrePoint);
      response.setHtml(html);
      response.setType(type);
      return response;
    }
  }
}
