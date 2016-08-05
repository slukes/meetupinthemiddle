package com.meetupinthemiddle.model

import groovy.transform.builder.Builder;

@Builder
public class Response {
  String html
  POI[] POIs
  CentrePoint centrePoint
  POIType poiType
  Person[] people
}