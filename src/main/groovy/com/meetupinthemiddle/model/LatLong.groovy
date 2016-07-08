package com.meetupinthemiddle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@TupleConstructor
@EqualsAndHashCode
@ToString
class LatLong {
  double lat;
  double lng;
}