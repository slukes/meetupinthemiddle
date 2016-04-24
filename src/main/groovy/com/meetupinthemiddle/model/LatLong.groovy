package com.meetupinthemiddle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@TupleConstructor
@EqualsAndHashCode
class LatLong {
  double lat;
  double lng;
}
