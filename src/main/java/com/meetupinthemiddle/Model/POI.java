package com.meetupinthemiddle.Model;

public class POI {
  private String name;
  private String address;
  private String phoneNumber;
  private String emailAddress;
  private String website;
  private LatLong geocode;
  private float distanceFromCentrePoint;
  private String imageUrl;

  private POI(){}

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(final String phoneNumber) {
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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(final String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public static class POIBuilder {
    private String name;
    private String address;
    private String phoneNumber;
    private String emailAddress;
    private String website;
    private LatLong geocode;
    private float distanceFromCentrePoint;
    private String imageUrl;

    private POIBuilder() {
    }

    public static POIBuilder aPOI() {
      return new POIBuilder();
    }

    public POIBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public POIBuilder withAddress(String address) {
      this.address = address;
      return this;
    }

    public POIBuilder withPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public POIBuilder withEmailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
      return this;
    }

    public POIBuilder withWebsite(String website) {
      this.website = website;
      return this;
    }

    public POIBuilder withGeocode(LatLong geocode) {
      this.geocode = geocode;
      return this;
    }

    public POIBuilder withDistanceFromCentrePoint(float distanceFromCentrePoint) {
      this.distanceFromCentrePoint = distanceFromCentrePoint;
      return this;
    }

    public POIBuilder withImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public POIBuilder but() {
      return aPOI().withName(name).withAddress(address).withPhoneNumber(phoneNumber).withEmailAddress(emailAddress).withWebsite(website).withGeocode(geocode).withDistanceFromCentrePoint(distanceFromCentrePoint).withImageUrl(imageUrl);
    }

    public POI build() {
      POI POI = new POI();
      POI.setName(name);
      POI.setAddress(address);
      POI.setPhoneNumber(phoneNumber);
      POI.setEmailAddress(emailAddress);
      POI.setWebsite(website);
      POI.setGeocode(geocode);
      POI.setDistanceFromCentrePoint(distanceFromCentrePoint);
      POI.setImageUrl(imageUrl);
      return POI;
    }
  }
}