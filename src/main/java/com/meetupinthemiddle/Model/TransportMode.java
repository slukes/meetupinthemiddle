package com.meetupinthemiddle.Model;

public enum TransportMode {
  DRIVING("Driving"), PUBLIC("Public Transport");

  private String text;

  TransportMode(final String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}