package com.meetupinthemiddle.services.poi
import com.google.maps.GeoApiContext
import com.google.maps.NearbySearchRequest
import com.google.maps.model.*
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POI
import com.meetupinthemiddle.model.POIType
import com.meetupinthemiddle.services.AbstractGoogleMapsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable

abstract class GooglePlacesPOIFinderTemplate extends AbstractGoogleMapsService<PlacesSearchResponse, NearbySearchRequest> implements POIFinder {
  @Autowired
  protected GeoApiContext context

  @Autowired
  protected GooglePlacesDetailsFinder detailsFinder

  @Value('${google.maps.photos.url}')
  private String photoUrlFormat

  //Google maps likes to returns things like Greggs as a restaurant
  // and Oceana as a pub.  Filter out these categories to avoid this.
  // Ideally I would find an alternative provider with better data, since its not ideal
  // to exclude all restaurants who also happen to do takeaway.
  private static final List<String> DISALLOWED_TERMS =
      ["meal_takeaway", "meal_delivery", "cafe", "night_club"]

  @Override
  @Cacheable("pois")
  POI[] findPOIs(LatLong location, int numberToFind, POIType type) {
    def results = []
    def nextPage = null

    while (results.size() < numberToFind) {
      def googleResp = doSearch(location, type, nextPage)

      results.addAll(
          googleResp.results
              .findAll { DISALLOWED_TERMS.intersect(it.types.toList()).size() == 0 }
      )
    }

    results
        .take(numberToFind)
        .collect(mapToPOI)
  }

  protected abstract PlacesSearchResponse doSearch(LatLong location, POIType type, String pageToken)

  private Closure<POI> mapToPOI = {
    place ->
      PlaceDetails placeDetails = owner.detailsFinder.getDetails(place)

      new POI().with {
        name = place.name
        address = placeDetails.formattedAddress
        latLong = mapLatLngToMeetModel(place.geometry.location)
        phoneNumber = placeDetails.formattedPhoneNumber
        imageUrl = extractPhotoUrl(place)
        website = extractWebsite(placeDetails)
        openingTimes = placeDetails.openingHours?.weekdayText
        rating = place.rating
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