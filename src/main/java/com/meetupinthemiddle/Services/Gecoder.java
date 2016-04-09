package com.meetupinthemiddle.Services;

import com.meetupinthemiddle.Model.LatLong;

public interface Gecoder {
  public LatLong geocode();
  public String reverseGeocode();
}
