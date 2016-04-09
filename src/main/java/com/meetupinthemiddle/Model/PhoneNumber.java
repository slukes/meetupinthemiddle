package com.meetupinthemiddle.Model;

public class PhoneNumber {
  private String stdCode;
  private String number;

  public String getNumber() {
    return number;
  }

  public void setNumber(final String number) {
    this.number = number;
  }

  public String getStdCode() {
    return stdCode;
  }

  public void setStdCode(final String stdCode) {
    this.stdCode = stdCode;
  }


  public static class PhoneNumberBuilder {
    private String stdCode;
    private String number;

    private PhoneNumberBuilder() {
    }

    public static PhoneNumberBuilder aPhoneNumber() {
      return new PhoneNumberBuilder();
    }

    public PhoneNumberBuilder withStdCode(String stdCode) {
      this.stdCode = stdCode;
      return this;
    }

    public PhoneNumberBuilder withNumber(String number) {
      this.number = number;
      return this;
    }

    public PhoneNumberBuilder but() {
      return aPhoneNumber().withStdCode(stdCode).withNumber(number);
    }

    public PhoneNumber build() {
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.setStdCode(stdCode);
      phoneNumber.setNumber(number);
      return phoneNumber;
    }
  }
}
