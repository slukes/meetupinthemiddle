package com.meetupinthemiddle.Model;

public class Person {
  private String name;
  private float distance;
  private int travelTime;
  private String from;
  private TransportMode transportMode;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(final float distance) {
    this.distance = distance;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(final String from) {
    this.from = from;
  }

  public TransportMode getTransportMode() {
    return transportMode;
  }

  public void setTransportMode(final TransportMode transportMode) {
    this.transportMode = transportMode;
  }


  public int getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(final int travelTime) {
    this.travelTime = travelTime;
  }

  public static class PersonBuilder {
    private String name;
    private float distance;
    private int travelTime;
    private String from;
    private TransportMode transportMode;

    private PersonBuilder() {
    }

    public static PersonBuilder aPerson() {
      return new PersonBuilder();
    }

    public PersonBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public PersonBuilder withDistance(float distance) {
      this.distance = distance;
      return this;
    }

    public PersonBuilder withTravelTime(int travelTime) {
      this.travelTime = travelTime;
      return this;
    }

    public PersonBuilder withFrom(String from) {
      this.from = from;
      return this;
    }

    public PersonBuilder withTransportMode(TransportMode transportMode) {
      this.transportMode = transportMode;
      return this;
    }

    public Person build() {
      Person person = new Person();
      person.setName(name);
      person.setDistance(distance);
      person.setTravelTime(travelTime);
      person.setFrom(from);
      person.setTransportMode(transportMode);
      return person;
    }
  }
}