package com.meetupinthemiddle.services.geocode

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.AddressComponent
import com.google.maps.model.AddressComponentType
import com.google.maps.model.GeocodingResult
import com.google.maps.model.LatLng
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.TownAndPostcode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class GoogleMapsGeocoder implements Geocoder {
  @Autowired
  private GeoApiContext context

  @Override
  @Cacheable("geocodes")
  LatLong geocode(final String location) {
    def resp = GeocodingApi.geocode(context, location).await()
    if (resp.length > 0) {
      def result = Arrays.<GeocodingResult> stream(resp)
          .filter({
        it.addressComponents.find({ it.types.contains(AddressComponentType.COUNTRY) }).shortName == "GB"
      }).findFirst().get()
      new LatLong(lat: result.geometry.location.lat, lng: result.geometry.location.lng)
    } else {
      return null
    }
  }

  @Override
  @Cacheable("reverse-geocodes")
  TownAndPostcode reverseGeocode(LatLong latLong) {
    def resp = GeocodingApi.reverseGeocode(context, new LatLng(latLong.lat, latLong.lng)).await()
    new TownAndPostcode(findTown(resp), findPostCode(resp))
  }

  private String findTown(GeocodingResult[] results) {
    for (GeocodingResult eachResult : results) {
      for (AddressComponent eachComponent : eachResult.addressComponents) {
        if (eachComponent.types.contains(AddressComponentType.POSTAL_TOWN)) {
          return eachComponent.longName
        }
      }
    }
    //If we didn't get a postal town try a level down
    for (GeocodingResult eachResult : results) {
      for (AddressComponent eachComponent : eachResult.addressComponents) {
        if (eachComponent.types.contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_2)) {
          return eachComponent.longName
        }
      }
    }
    return ""
  }

  private String findPostCode(GeocodingResult[] results) {
    for (GeocodingResult eachResult : results) {
      for (AddressComponent eachComponent : eachResult.addressComponents) {
        if (eachComponent.types.contains(AddressComponentType.POSTAL_CODE)) {
          return eachComponent.longName
        }
      }
    }
    return ""
  }
}