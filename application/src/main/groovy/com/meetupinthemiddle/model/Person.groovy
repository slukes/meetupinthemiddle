package com.meetupinthemiddle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder

import javax.validation.constraints.NotNull

@Builder(prefix = 'with')
@EqualsAndHashCode
class Person {
  @NotNull
  String name
  float distance
  long travelTime
  @NotNull
  String from
  //Not currently used, but might be by a future implementation
  LatLong latLong
  @NotNull
  TransportMode transportMode
}