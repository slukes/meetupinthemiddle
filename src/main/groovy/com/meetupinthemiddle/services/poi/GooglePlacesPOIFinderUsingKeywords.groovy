package com.meetupinthemiddle.services.poi

import com.google.maps.PlacesApi
import com.google.maps.model.PlacesSearchResponse
import com.google.maps.model.RankBy
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POIType
import org.springframework.stereotype.Service

import static POIType.MEETING

@Service
public class GooglePlacesPOIFinderUsingKeywords extends GooglePlacesPOIFinderTemplate {
  private static final def POI_PLACE_TYPE_MAPPING = [(MEETING): "meeting room"]

  protected PlacesSearchResponse doSearch(LatLong location, POIType type, String pageToken) {
    def req = PlacesApi.nearbySearchQuery(context, mapLatLongToGoogleModel(location))
        .keyword(POI_PLACE_TYPE_MAPPING[type])
        .rankby(RankBy.DISTANCE)

    if(pageToken){
      req.pageToken(pageToken)
    }

    doCall(req)
  }
}