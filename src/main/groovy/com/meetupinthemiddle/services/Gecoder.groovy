package com.meetupinthemiddle.services;

import com.meetupinthemiddle.model.LatLong;

interface Gecoder {
  LatLong geocode(String location)
  String reverseGeocode()
}