package com.meetupinthemiddle.model

import groovy.transform.TupleConstructor

@TupleConstructor
enum POIType {
  RESTAURANT("Restaurants"), PUB("Pubs"), MEETING("Meeting Rooms")

  String text;
}