package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.LatLong

/**
 * Created by Sam Lukes on 14/04/2016.
 */
class GoogleGeocoder implements Gecoder{
  @Override
  LatLong geocode(final String location) {
    return null
  }

  @Override
  String reverseGeocode() {
    return null
  }
}
