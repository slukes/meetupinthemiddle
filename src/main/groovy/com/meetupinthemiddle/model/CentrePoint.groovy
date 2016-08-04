package com.meetupinthemiddle.model

import groovy.transform.builder.Builder

@Builder
class CentrePoint {
  LatLong latLong;
  String postCode;
  String locality;
}
