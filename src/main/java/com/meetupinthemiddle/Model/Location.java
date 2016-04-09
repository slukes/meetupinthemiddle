package com.meetupinthemiddle.Model;

public class Location {
  private String name;
  private Address address;
  private PhoneNumber phoneNumber;
  private String emailAddress;
  private String website;
  private LatLong geocode;
  private float distanceFromCentrePoint;

  private Location(){}

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(final Address address) {
    this.address = address;
  }

  public PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(final PhoneNumber phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(final String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(final String website) {
    this.website = website;
  }

  public LatLong getGeocode() {
    return geocode;
  }

  public void setGeocode(final LatLong geocode) {
    this.geocode = geocode;
  }

  public float getDistanceFromCentrePoint() {
    return distanceFromCentrePoint;
  }

  public void setDistanceFromCentrePoint(final float distanceFromCentrePoint) {
    this.distanceFromCentrePoint = distanceFromCentrePoint;
  }

  public static class LocationBuilder {
    private String name;
    private Address address;
    private PhoneNumber phoneNumber;
    private String emailAddress;
    private String website;
    private LatLong geocode;
    private float distanceFromCentrePoint;

    private LocationBuilder() {
    }

    public static LocationBuilder aLocation() {
      return new LocationBuilder();
    }

    public LocationBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public LocationBuilder withAddress(Address address) {
      this.address = address;
      return this;
    }

    public LocationBuilder withPhoneNumber(PhoneNumber phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public LocationBuilder withEmailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
      return this;
    }

    public LocationBuilder withWebsite(String website) {
      this.website = website;
      return this;
    }

    public LocationBuilder withGeocode(LatLong geocode) {
      this.geocode = geocode;
      return this;
    }

    public LocationBuilder withDistanceFromCentrePoint(float distanceFromCentrePoint) {
      this.distanceFromCentrePoint = distanceFromCentrePoint;
      return this;
    }

    public Location build() {
      Location location = new Location();
      location.setName(name);
      location.setAddress(address);
      location.setPhoneNumber(phoneNumber);
      location.setEmailAddress(emailAddress);
      location.setWebsite(website);
      location.setGeocode(geocode);
      location.setDistanceFromCentrePoint(distanceFromCentrePoint);
      return location;
    }
  }
}