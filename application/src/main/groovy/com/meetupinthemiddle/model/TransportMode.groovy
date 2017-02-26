package com.meetupinthemiddle.model

import groovy.transform.TupleConstructor

@TupleConstructor
enum TransportMode {
    DRIVING("Driving"), PUBLIC("Public Transport")

  String text;
}