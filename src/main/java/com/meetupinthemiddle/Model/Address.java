package com.meetupinthemiddle.Model;

public class Address {
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String locality;
  private String postalTown;
  private String county;
  private String postcode;

  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(final String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(final String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public String getAddressLine3() {
    return addressLine3;
  }

  public void setAddressLine3(final String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(final String locality) {
    this.locality = locality;
  }

  public String getPostalTown() {
    return postalTown;
  }

  public void setPostalTown(final String postalTown) {
    this.postalTown = postalTown;
  }

  public String getCounty() {
    return county;
  }

  public void setCounty(final String county) {
    this.county = county;
  }

  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(final String postcode) {
    this.postcode = postcode;
  }

  public static class AddressBuilder {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String locality;
    private String postalTown;
    private String county;
    private String postcode;

    private AddressBuilder() {
    }

    public static AddressBuilder anAddress() {
      return new AddressBuilder();
    }

    public AddressBuilder withAddressLine1(String addressLine1) {
      this.addressLine1 = addressLine1;
      return this;
    }

    public AddressBuilder withAddressLine2(String addressLine2) {
      this.addressLine2 = addressLine2;
      return this;
    }

    public AddressBuilder withAddressLine3(String addressLine3) {
      this.addressLine3 = addressLine3;
      return this;
    }

    public AddressBuilder withLocality(String locality) {
      this.locality = locality;
      return this;
    }

    public AddressBuilder withPostalTown(String postalTown) {
      this.postalTown = postalTown;
      return this;
    }

    public AddressBuilder withCounty(String county) {
      this.county = county;
      return this;
    }

    public AddressBuilder withPostcode(String postcode) {
      this.postcode = postcode;
      return this;
    }

    public Address build() {
      Address address = new Address();
      address.setAddressLine1(addressLine1);
      address.setAddressLine2(addressLine2);
      address.setAddressLine3(addressLine3);
      address.setLocality(locality);
      address.setPostalTown(postalTown);
      address.setCounty(county);
      address.setPostcode(postcode);
      return address;
    }
  }
}