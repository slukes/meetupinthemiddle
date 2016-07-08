package com.meetupinthemiddle.model

import groovy.transform.builder.Builder

@Builder
class POI {
  String name
  String address
  String phoneNumber
  String website
  LatLong latLong
  float distanceFromCentrePoint
  String imageUrl
  String [] openingTimes
}