package com.meetupinthemiddle.Model;

public class CentrePoint {
  private LatLong latLong;
  private String postCode;
  private String locality;

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(final String postCode) {
    this.postCode = postCode;
  }

  public LatLong getLatLong() {
    return latLong;
  }

  public void setLatLong(final LatLong latLong) {
    this.latLong = latLong;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(final String locality) {
    this.locality = locality;
  }

  public static class CentrePointBuilder {
    private LatLong latLong;
    private String postCode;
    private String locality;

    private CentrePointBuilder() {
    }

    public static CentrePointBuilder aCentrePoint() {
      return new CentrePointBuilder();
    }

    public CentrePointBuilder withLatLong(LatLong latLong) {
      this.latLong = latLong;
      return this;
    }

    public CentrePointBuilder withPostCode(String postCode) {
      this.postCode = postCode;
      return this;
    }

    public CentrePointBuilder withLocality(String locality){
      this.locality = locality;
      return this;
    }

    public CentrePoint build() {
      CentrePoint centrePoint = new CentrePoint();
      centrePoint.setLatLong(latLong);
      centrePoint.setPostCode(postCode);
      centrePoint.setLocality(locality);
      return centrePoint;
    }
  }
}
