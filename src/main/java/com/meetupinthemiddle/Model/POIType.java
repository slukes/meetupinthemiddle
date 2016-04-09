package com.meetupinthemiddle.Model;

public enum POIType {
  RESTAURANT("Restaurants"), PUB("Pubs"), MEETING("Meeting Rooms");

  private final String text;

  POIType(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}