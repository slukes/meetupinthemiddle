package com.meetupinthemiddle

import com.google.maps.model.*
import com.meetupinthemiddle.model.*
import org.apache.commons.lang.math.RandomUtils

import static org.apache.commons.lang.RandomStringUtils.random

class Stubs {
  static aRequestObj() {
    def test = Request.builder()
        .people(twoPeople())
        .poiType(POIType.MEETING)
        .build()
    test
  }

  static twoPeople() {
    [Person.builder()
         .withName("Sam")
         .withFrom("Reading")
         .withTransportMode(TransportMode.PUBLIC)
         .withLatLong(new LatLong(123.1, 234.2))
         .build()
     , Person.builder()
         .withName("George")
         .withFrom("Woking")
         .withLatLong(new LatLong(125.1, 237.2))
         .withTransportMode(TransportMode.DRIVING)
         .build()]
  }

  static randomLatLong() {
    new LatLong(RandomUtils.nextDouble(), RandomUtils.nextDouble())
  }

  static twoPeopleBothDriving() {
    [Person.builder()
         .withName("Sam")
         .withFrom("Reading")
         .withTransportMode(TransportMode.DRIVING)
         .withLatLong(new LatLong(123.1, 234.2))
         .build()
     , Person.builder()
         .withName("George")
         .withFrom("Woking")
         .withLatLong(new LatLong(124.1, 235.2))
         .withTransportMode(TransportMode.DRIVING)
         .build()]
  }

  static aCentrePointAndJourneyTimes() {
    def request = aRequestObj()
    new Tuple2<CentrePoint, Map<Person, Long>>(CentrePoint.builder()
        .latLong(new LatLong(1f, 2f))
        .locality("Guildford")
        .postCode("GU22 7UF")
        .build(), [(request.people[0]): 10, (request.people[1]): 20])
  }

  static Response aResponseObj() {
    Response.builder()
        .centrePoint(aCentrePointAndJourneyTimes().first)
        .people(twoPeople() as Person[])
        .POIs([aPoi(), aPoi()] as POI[])
        .poiType(POIType.RESTAURANT)
        .build()
  }

  private static aPoi() {
    POI.builder()
        .address("an address")
        .distanceFromCentrePoint(5)
        .imageUrl("http://url")
        .latLong(new LatLong(123, 456))
        .distanceFromCentrePoint(5)
        .name("a name")
        .build()
  }

  static PlaceDetails aPlaceDetails() throws MalformedURLException {
    PlaceDetails placeDetails = new PlaceDetails();
    placeDetails.website = new URL("http://website.com");
    placeDetails.formattedAddress = "An address somewhere";
    placeDetails.formattedPhoneNumber = "0121 237 3878";
    return placeDetails;
  }

  static PlacesSearchResponse aPlacesSearchResponse() {
    PlacesSearchResponse response = new PlacesSearchResponse();
    response.results = [aResultWithEverything(), aLimitedResult()]
    return response;
  }

  private static PlacesSearchResult aResultWithEverything() {
    PlacesSearchResult resultOne = new PlacesSearchResult();
    try {
      resultOne.icon = new URL("http://icon.com");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    Photo photo = new Photo();
    photo.photoReference = "photoRef";
    resultOne.photos = [photo];
    resultOne.openingHours = new OpeningHours();
    resultOne.openingHours.weekdayText = [
        "Monday some hours",
        "Tuesday some hours",
        "Weds some hours",
        "Thurs some hours",
        "Fri some hours",
        "Sat some hours",
        "Sun some hours"
    ]
    Geometry geometry = new Geometry();
    geometry.location = new LatLng(10, 10);
    resultOne.geometry = geometry;
    resultOne.name = "A really cool place";

    return resultOne;
  }

  private static PlacesSearchResult aLimitedResult() {
    PlacesSearchResult resultOne = new PlacesSearchResult();
    try {
      resultOne.icon = new URL("http://icon.com");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    Geometry geometry = new Geometry();
    geometry.location = new LatLng(10, 10);
    resultOne.geometry = geometry;
    resultOne.name = "A really cool place";

    return resultOne;
  }

  static ContactFormBean randomFormBean() {
    ContactFormBean.builder()
        .name(random(20))
        .email("random@random.com")
        .subject(random(20))
        .message(random(2000))
        .sendCopy(RandomUtils.nextBoolean())
        .build()
  }
}
