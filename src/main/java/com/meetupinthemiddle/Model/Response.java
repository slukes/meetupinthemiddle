package com.meetupinthemiddle.Model;

import java.util.ArrayList;
import java.util.List;

public class Response {
  private String html;
  private List<POI> POIs;
  private CentrePoint centrePoint;
  private POIType poiType;
  private List<Person> people;

  public List<POI> getPOIs() {
    return POIs;
  }

  public void setPOIs(final List<POI> POIs) {
    this.POIs = POIs;
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

  public POIType getPoiType() {
    return poiType;
  }

  public void setPoiType(final POIType poiType) {
    this.poiType = poiType;
  }

  public List<Person> getPeople() {
    return people;
  }

  public void setPeople(final List<Person> people) {
    this.people = people;
  }

  public static class ResponseBuilder {
    private String html;
    private List<POI> POIs = new ArrayList<>();
    private CentrePoint centrePoint;
    private POIType poiType;
    private List<Person> people;

    private ResponseBuilder() {
    }

    public static ResponseBuilder aResponse() {
      return new ResponseBuilder();
    }

    public ResponseBuilder withHtml(String html) {
      this.html = html;
      return this;
    }

    public ResponseBuilder withLocation(POI POI) {
      this.POIs.add(POI);
      return this;
    }

    public ResponseBuilder withCentrePoint(CentrePoint centrePoint) {
      this.centrePoint = centrePoint;
      return this;
    }

    public ResponseBuilder withPoiType(POIType poiType) {
      this.poiType = poiType;
      return this;
    }

    public ResponseBuilder withPeople(List<Person> people) {
      this.people = people;
      return this;
    }

    public Response build() {
      Response response = new Response();
      response.setHtml(html);
      response.setPOIs(POIs);
      response.setCentrePoint(centrePoint);
      response.setPoiType(poiType);
      response.setPeople(people);
      return response;
    }
  }
}