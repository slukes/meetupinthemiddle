package com.meetupinthemiddle.services;

import com.meetupinthemiddle.model.LatLong;

interface Geocoder {
  LatLong geocode(String location)
  String reverseGeocode()
}