package com.meetupinthemiddle.services.poi
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.*
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POI
import com.meetupinthemiddle.model.POIType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import java.util.function.Function
import java.util.stream.Collectors

import static java.util.Arrays.stream

@Service
public class GooglePlacesPOIFinder implements POIFinder {
  @Autowired
  private GeoApiContext context

  @Value('${google.maps.photos.url}')
  private String photoUrlFormat

  private static final def POI_PLACE_TYPE_MAPPING = [(POIType.RESTAURANT) : PlaceType.RESTAURANT,
                                                     (POIType.PUB) : PlaceType.BAR]

  @Override
  @Cacheable("pois")
  public POI[] findPOIs(LatLong location, int numberToFind, POIType type) {
    PlacesSearchResult[] googleResponse = PlacesApi.nearbySearchQuery(context, mapLatLongToGoogleModel(location))
        .type(POI_PLACE_TYPE_MAPPING[type])
        .rankby(RankBy.DISTANCE)
        .await()
        .results

    stream(googleResponse)
        .limit(numberToFind)
        .parallel()
        .map(mapPlaceToPoiFunction)
        .collect(Collectors.toList())
  }

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
            it
          }
      }

  private String extractWebsite(PlaceDetails placeDetails) {
    placeDetails.website?.toString()
  }

  private String extractPhotoUrl(PlacesSearchResult place) {
    if (place.photos?.length > 0) {
      formatPhotoUrl(place.photos[0])
    } else {
      place.icon.toString()
    }
  }

  private String formatPhotoUrl(final Photo photo) {
    return String.format(photoUrlFormat, photo.photoReference)
  }


  private LatLng mapLatLongToGoogleModel(final LatLong location) {
    new LatLng(location.getLat(), location.getLng())
  }

  private LatLong mapLatLngToMeetModel(final LatLng location) {
    new LatLong(lat: location.lat, lng: location.lng)
  }
}