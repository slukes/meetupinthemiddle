package com.meetupinthemiddle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder

@Builder
@EqualsAndHashCode
class POI {
  String name
  String address
  String phoneNumber
  String website
  LatLong latLong
  String imageUrl
  String [] openingTimes
  float rating
}