package com.meetupinthemiddle.services.geocode
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.TownAndPostcode
import org.springframework.cache.annotation.Cacheable

interface Geocoder {
  @Cacheable("geocodes")
  LatLong geocode(String location)
  @Cacheable("reverse-geocodes")
  TownAndPostcode reverseGeocode(LatLong latLong)
}