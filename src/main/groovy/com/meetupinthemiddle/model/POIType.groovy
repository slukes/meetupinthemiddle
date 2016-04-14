package com.meetupinthemiddle.model

enum POIType {
  RESTAURANT("Restaurants"), PUB("Pubs"), MEETING("Meeting Rooms");

  String text;

  POIType(String text) {
    this.text = text;
  }
}