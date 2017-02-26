package com.meetupinthemiddle.model;

import java.util.Arrays;

public class POIInJava {
  private String name;
  private String address;
  private String phoneNumber;
  private String website;
  private LatLong latLong;
  private String imageUrl;
  private String [] openingTimes;
  private float rating;

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

  public String getWebsite() {
    return website;
  }

  public void setWebsite(final String website) {
    this.website = website;
  }

  public LatLong getLatLong() {
    return latLong;
  }

  public void setLatLong(final LatLong latLong) {
    this.latLong = latLong;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(final String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String[] getOpeningTimes() {
    return openingTimes;
  }

  public void setOpeningTimes(final String[] openingTimes) {
    this.openingTimes = openingTimes;
  }

  public float getRating() {
    return rating;
  }

  public void setRating(final float rating) {
    this.rating = rating;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    POIInJava poiInJava = (POIInJava) o;

    if (Float.compare(poiInJava.getRating(), getRating()) != 0) return false;
    if (getName() != null ? ! getName().equals(poiInJava.getName()) : poiInJava.getName() != null) return false;
    if (getAddress() != null ? ! getAddress().equals(poiInJava.getAddress()) : poiInJava.getAddress() != null)
      return false;
    if (getPhoneNumber() != null ? ! getPhoneNumber().equals(poiInJava.getPhoneNumber()) : poiInJava.getPhoneNumber() != null)
      return false;
    if (getWebsite() != null ? ! getWebsite().equals(poiInJava.getWebsite()) : poiInJava.getWebsite() != null)
      return false;
    if (getLatLong() != null ? ! getLatLong().equals(poiInJava.getLatLong()) : poiInJava.getLatLong() != null)
      return false;
    if (getImageUrl() != null ? ! getImageUrl().equals(poiInJava.getImageUrl()) : poiInJava.getImageUrl() != null)
      return false;
    return Arrays.equals(getOpeningTimes(), poiInJava.getOpeningTimes());
  }

  @Override
  public int hashCode() {
    int result = getName() != null ? getName().hashCode() : 0;
    result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
    result = 31 * result + (getPhoneNumber() != null ? getPhoneNumber().hashCode() : 0);
    result = 31 * result + (getWebsite() != null ? getWebsite().hashCode() : 0);
    result = 31 * result + (getLatLong() != null ? getLatLong().hashCode() : 0);
    result = 31 * result + (getImageUrl() != null ? getImageUrl().hashCode() : 0);
    result = 31 * result + Arrays.hashCode(getOpeningTimes());
    result = 31 * result + (getRating() != + 0.0f ? Float.floatToIntBits(getRating()) : 0);
    return result;
  }

  public static class POIInJavaBuilder {
    private String name;
    private String address;
    private String phoneNumber;
    private String website;
    private LatLong latLong;
    private String imageUrl;
    private String [] openingTimes;
    private float rating;

    private POIInJavaBuilder() {
    }

    public static POIInJavaBuilder aPOIInJava() {
      return new POIInJavaBuilder();
    }

    public POIInJavaBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public POIInJavaBuilder withAddress(String address) {
      this.address = address;
      return this;
    }

    public POIInJavaBuilder withPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public POIInJavaBuilder withWebsite(String website) {
      this.website = website;
      return this;
    }

    public POIInJavaBuilder withLatLong(LatLong latLong) {
      this.latLong = latLong;
      return this;
    }

    public POIInJavaBuilder withImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public POIInJavaBuilder withOpeningTimes(String[] openingTimes) {
      this.openingTimes = openingTimes;
      return this;
    }

    public POIInJavaBuilder withRating(float rating) {
      this.rating = rating;
      return this;
    }

    public POIInJava build() {
      POIInJava pOIInJava = new POIInJava();
      pOIInJava.setName(name);
      pOIInJava.setAddress(address);
      pOIInJava.setPhoneNumber(phoneNumber);
      pOIInJava.setWebsite(website);
      pOIInJava.setLatLong(latLong);
      pOIInJava.setImageUrl(imageUrl);
      pOIInJava.setOpeningTimes(openingTimes);
      pOIInJava.setRating(rating);
      return pOIInJava;
    }
  }
}