package com.meetupinthemiddle.Model;

public enum ResponseType{
  RESTAURANT("Restaurants"), PUB("Pubs"), MEETING("Meeting Rooms");

  private final String text;

  ResponseType(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}