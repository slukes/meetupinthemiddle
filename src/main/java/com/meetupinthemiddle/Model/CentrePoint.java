package com.meetupinthemiddle.Model;

public class CentrePoint {
  private LatLong latLong;
  private String postCode;

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


  public static class CentrePointBuilder {
    private LatLong latLong;
    private String postCode;

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

    public CentrePointBuilder but() {
      return aCentrePoint().withLatLong(latLong).withPostCode(postCode);
    }

    public CentrePoint build() {
      CentrePoint centrePoint = new CentrePoint();
      centrePoint.setLatLong(latLong);
      centrePoint.setPostCode(postCode);
      return centrePoint;
    }
  }
}
