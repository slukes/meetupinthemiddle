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

  static LatLong fromString(final String latLongString) {
    def arr = latLongString.split(",")
    new LatLong(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]))
  }
}