package com.meetupinthemiddle.services.poi
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.LatLng
import com.google.maps.model.Photo
import com.google.maps.model.PlaceDetails
import com.google.maps.model.PlacesSearchResult
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POI
import com.meetupinthemiddle.model.POIType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable

import java.util.function.Function
import java.util.stream.Collectors

import static java.util.Arrays.stream

abstract class GooglePlacesPOIFinderTemplate implements POIFinder {
  @Autowired
  protected GeoApiContext context

  @Value('${google.maps.photos.url}')
  private String photoUrlFormat

  @Override
  @Cacheable("pois")
  POI[] findPOIs(LatLong location, int numberToFind, POIType type) {
    PlacesSearchResult[] googleResponse = doSearch(location, type)

    stream(googleResponse)
        .limit(numberToFind)
        .parallel()
        .map(mapPlaceToPoiFunction)
        .collect(Collectors.toList())
  }

  protected abstract PlacesSearchResult[] doSearch(LatLong location, POIType type)

  private final Function<PlacesSearchResult, POI> mapPlaceToPoiFunction =
      {
        place ->
          PlaceDetails placeDetails = PlacesApi.placeDetails(context, place.placeId).await()

          new POI().with {
            name = place.name
            address = placeDetails.formattedAddress
            distanceFromCentrePoint = 0
            latLong = mapLatLngToMeetModel(place.geometry.location)
            phoneNumber = placeDetails.formattedPhoneNumber
            imageUrl = extractPhotoUrl(place)
            website = extractWebsite(placeDetails)
            openingTimes = placeDetails.openingHours?.weekdayText
            it
          }
      }

  protected String extractWebsite(PlaceDetails placeDetails) {
    placeDetails.website?.toString()
  }

  protected String extractPhotoUrl(PlacesSearchResult place) {
    if (place.photos?.length > 0) {
      formatPhotoUrl(place.photos[0])
    } else {
      place.icon.toString()
    }
  }

  protected String formatPhotoUrl(final Photo photo) {
    return String.format(photoUrlFormat, photo.photoReference)
  }


  protected LatLng mapLatLongToGoogleModel(location) {
    new LatLng(location.getLat(), location.getLng())
  }

  protected LatLong mapLatLngToMeetModel(location) {
    new LatLong(lat: location.lat, lng: location.lng)
  }
}