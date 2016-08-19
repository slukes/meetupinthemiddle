package com.meetupinthemiddle.services.poi

import com.google.maps.PlaceDetailsRequest
import com.google.maps.PlacesApi
import com.google.maps.model.PlaceDetails
import com.google.maps.model.PlacesSearchResult
import com.meetupinthemiddle.services.AbstractGoogleMapsService
import groovy.transform.PackageScope
import org.springframework.stereotype.Service

@PackageScope
@Service
class GooglePlacesDetailsFinder extends AbstractGoogleMapsService<PlaceDetails, PlaceDetailsRequest> {
  PlaceDetails getDetails(PlacesSearchResult place) {
    doCall(PlacesApi.placeDetails(context, place.placeId))
  }
}
