package com.meetupinthemiddle.Services;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.meetupinthemiddle.Model.LatLong;
import com.meetupinthemiddle.Model.POI;
import com.meetupinthemiddle.Model.POIType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meetupinthemiddle.Model.LatLong.LatLongBuilder.aLatLong;
import static com.meetupinthemiddle.Model.POI.POIBuilder.aPOI;

@Service
public class GooglePOIFinderService implements POIFinderService {
  @Autowired
  private GeoApiContext context;

  private final Function<PlacesSearchResult, POI> mapPlaceToPoiFunction = place -> {
    try {
      PlaceDetails placeDetails = PlacesApi.placeDetails(context, place.placeId).await();
      return
          aPOI().withName(place.name)
              .withAddress(place.formattedAddress)
              .withDistanceFromCentrePoint(0)
              .withGeocode(mapLatLngToMeetModel(place.geometry.location))
              .withPhoneNumber(placeDetails.formattedPhoneNumber)
              .withImageUrl(place.icon.toString())
              .withWebsite(placeDetails.website.toString())
              .build();
    } catch (Exception e) {
      //TODO - proper exception handling
      throw new RuntimeException(e);
    }
  };

  private static final Map<POIType, PlaceType> POI_PLACE_TYPE_MAPPING = new HashMap<>();

  static {
    POI_PLACE_TYPE_MAPPING.put(POIType.RESTAURANT, PlaceType.RESTAURANT);
    POI_PLACE_TYPE_MAPPING.put(POIType.PUB, PlaceType.BAR);
  }

  @Override
  //TODO find out how best to handle this exception
  public List<POI> findPOIs(final LatLong location, final int numberToFind, final POIType type) throws Exception {
    PlacesSearchResult[] googleResponse = PlacesApi.nearbySearchQuery(context, mapLatLongToGoogleModel(location))
        .type(POI_PLACE_TYPE_MAPPING.get(type))
        .rankby(RankBy.DISTANCE)
        .await()
        .results;

    return Arrays.stream(googleResponse)
        .limit(numberToFind)
        .parallel()
        .map(mapPlaceToPoiFunction)
        .collect(Collectors.toList());
  }

  private LatLng mapLatLongToGoogleModel(final LatLong location) {
    return new LatLng(location.getLat(), location.getLng());
  }

  private LatLong mapLatLngToMeetModel(final LatLng location) {
    return aLatLong()
        .withLat(location.lat)
        .withLng(location.lng)
        .build();
  }
}