package com.meetupinthemiddle.model


enum TransportMode {
  DRIVING("Driving"), PUBLIC("Public Transport");

  String text;

  TransportMode(String text) {
    this.text = text;
  }

}