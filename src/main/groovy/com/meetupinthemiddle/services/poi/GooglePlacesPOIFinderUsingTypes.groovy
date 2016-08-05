package com.meetupinthemiddle.services.poi
import com.google.maps.PlacesApi
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResult
import com.google.maps.model.RankBy
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POIType
import org.springframework.stereotype.Service

import static com.meetupinthemiddle.model.POIType.RESTAURANT

@Service
public class GooglePlacesPOIFinderUsingTypes extends GooglePlacesPOIFinderTemplate {
  private static final def POI_PLACE_TYPE_MAPPING = [(RESTAURANT) : PlaceType.RESTAURANT,
                                                     (POIType.PUB): PlaceType.BAR]

  protected PlacesSearchResult[] doSearch(LatLong location, POIType type) {
    PlacesSearchResult[] googleResponse = PlacesApi.nearbySearchQuery(context, mapLatLongToGoogleModel(location))
        .type(POI_PLACE_TYPE_MAPPING[type])
        .rankby(RankBy.DISTANCE)
        .await()
        .results
    googleResponse
  }
}