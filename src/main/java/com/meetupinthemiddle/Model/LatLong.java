package com.meetupinthemiddle.Model;

public class LatLong {
  private double lat;
  private double lng;

  public double getLat() {
    return lat;
  }

  public void setLat(final double lat) {
    this.lat = lat;
  }

  public double getLng() {
    return lng;
  }

  public void setLng(final double lng) {
    this.lng = lng;
  }


  public static class LatLongBuilder {
    private double lat;
    private double lng;

    private LatLongBuilder() {
    }

    public static LatLongBuilder aLatLong() {
      return new LatLongBuilder();
    }

    public LatLongBuilder withLat(double lat) {
      this.lat = lat;
      return this;
    }

    public LatLongBuilder withLng(double lng) {
      this.lng = lng;
      return this;
    }

    public LatLongBuilder but() {
      return aLatLong().withLat(lat).withLng(lng);
    }

    public LatLong build() {
      LatLong latLong = new LatLong();
      latLong.setLat(lat);
      latLong.setLng(lng);
      return latLong;
    }
  }
}
