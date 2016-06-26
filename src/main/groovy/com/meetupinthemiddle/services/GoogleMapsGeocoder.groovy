package com.meetupinthemiddle.services

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.meetupinthemiddle.model.LatLong
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class GoogleMapsGeocoder implements Geocoder{
  @Autowired
  private GeoApiContext context

  @Override
  @Cacheable("geocodes")
  LatLong geocode(final String location) {
    def resp = GeocodingApi.geocode(context, location).await()
    if(resp.length > 0) {
      new LatLong(lat: resp[0].geometry.location.lat, lng: resp[0].geometry.location.lng)
    } else {
      return null
    }
  }

  @Override
  String reverseGeocode() {
    return null
  }
}
