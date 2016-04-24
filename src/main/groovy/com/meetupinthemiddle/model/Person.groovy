package com.meetupinthemiddle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder

@Builder(prefix = 'with')
@EqualsAndHashCode
class Person {
  String name
  float distance
  int travelTime
  String from
  LatLong latLong
  TransportMode transportMode
}
