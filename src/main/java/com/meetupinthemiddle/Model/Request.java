package com.meetupinthemiddle.Model;

import java.util.List;

public class Request {
  private List<Person> people;
  private POIType poiType;

  public List<Person> getPeople() {
    return people;
  }

  private void setPeople(final List<Person> people) {
    this.people = people;
  }

  public POIType getPoiType() {
    return poiType;
  }

  public void setPoiType(final POIType poiType) {
    this.poiType = poiType;
  }


  public static class RequestBuilder {
    private List<Person> people;
    private POIType poiType;

    private RequestBuilder() {
    }

    public static RequestBuilder aRequest() {
      return new RequestBuilder();
    }

    public RequestBuilder withPeople(List<Person> people) {
      this.people = people;
      return this;
    }

    public RequestBuilder withPoiType(POIType poiType) {
      this.poiType = poiType;
      return this;
    }

    public Request build() {
      Request request = new Request();
      request.setPeople(people);
      request.setPoiType(poiType);
      return request;
    }
  }
}
