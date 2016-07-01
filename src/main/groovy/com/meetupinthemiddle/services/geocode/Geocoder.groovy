package com.meetupinthemiddle.services.geocode

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.TownAndPostcode

interface Geocoder {
  LatLong geocode(String location)
  TownAndPostcode reverseGeocode(LatLong latLong)
}