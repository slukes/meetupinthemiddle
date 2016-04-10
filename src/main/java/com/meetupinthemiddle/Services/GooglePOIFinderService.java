package com.meetupinthemiddle.Services;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.meetupinthemiddle.Model.LatLong;
import com.meetupinthemiddle.Model.POI;
import com.meetupinthemiddle.Model.POIType;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${google.maps.photos.url}")
  private String photoUrlFormat;

  private static final Map<POIType, PlaceType> POI_PLACE_TYPE_MAPPING = new HashMap<>();

  static {
    POI_PLACE_TYPE_MAPPING.put(POIType.RESTAURANT, PlaceType.RESTAURANT);
    POI_PLACE_TYPE_MAPPING.put(POIType.PUB, PlaceType.BAR);
  }

  private final Function<PlacesSearchResult, POI> mapPlaceToPoiFunction = place -> {
    try {
      PlaceDetails placeDetails = PlacesApi.placeDetails(context, place.placeId).await();
      return
          aPOI().withName(place.name)
              .withAddress(placeDetails.formattedAddress)
              .withDistanceFromCentrePoint(0)
              .withGeocode(mapLatLngToMeetModel(place.geometry.location))
              .withPhoneNumber(placeDetails.formattedPhoneNumber)
              .withImageUrl(extractPhotoUrl(place))
              .withWebsite(extractWebsite(placeDetails))
              .build();
    } catch (Exception e) {
      //TODO - proper exception handling
      throw new RuntimeException(e);
    }
  };

  private String extractWebsite(final PlaceDetails placeDetails) {
    return placeDetails.website != null ? placeDetails.website.toString() : null;
  }

  private String extractPhotoUrl(final PlacesSearchResult place) {
    if (ArrayUtils.isNotEmpty(place.photos)) {
      return formatPhotoUrl(place.photos[0]);
    } else {
      return place.icon.toString();
    }
  }

  private String formatPhotoUrl(final Photo photo) {
    return String.format(photoUrlFormat, photo.photoReference);
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