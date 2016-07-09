package com.meetupinthemiddle.services.poi;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.meetupinthemiddle.model.LatLong;
import com.meetupinthemiddle.model.POI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;

import static com.meetupinthemiddle.model.POIType.RESTAURANT;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PlacesApi.class, PlacesSearchResult.class, PlaceDetailsRequest.class})
public class GoogleMapsPOIFinderTest {
  @InjectMocks
  GooglePlacesPOIFinder googleMapsPoiFinder = new GooglePlacesPOIFinder();

  @Mock
  GeoApiContext ctx;

  @Mock
  NearbySearchRequest nearbySearchRequest;

  @Mock
  PlaceDetailsRequest placeDetailsRequest;

  @Before
  public void injectUrl() {
    ReflectionTestUtils.setField(googleMapsPoiFinder, "photoUrlFormat", "http://blah.com/blah?photoreference=%s");
  }

  @Test
  public void testApisAreCalled() throws Exception {
    mockGoogle();

    googleMapsPoiFinder.findPOIs(new LatLong(10, 10), 5, RESTAURANT);

    verifyStatic();
    ArgumentCaptor<LatLng> argumentCaptor = ArgumentCaptor.forClass(LatLng.class);
    PlacesApi.nearbySearchQuery(same(ctx), argumentCaptor.capture());

    LatLng actual = argumentCaptor.getValue();
    assertEquals(10d, actual.lat);
    assertEquals(10d, actual.lng);

    verifyStatic(times(2));
    PlacesApi.placeDetails(same(ctx), anyString());
  }

  @Test
  public void testImageUrl() throws Exception {
    mockGoogle();
    POI[] result = googleMapsPoiFinder.findPOIs(new LatLong(10, 10), 5, RESTAURANT);

    assertEquals("http://blah.com/blah?photoreference=photoRef", result[0].getImageUrl());
  }

  @Test
  public void testNoImageUrlGetsIcon() throws Exception {
    mockGoogle();
    POI[] result = googleMapsPoiFinder.findPOIs(new LatLong(10, 10), 5, RESTAURANT);

    assertEquals("http://icon.com", result[1].getImageUrl());
  }

  @Test
  public void testNoExceptionsIfNoOpeningHours() throws Exception {
    mockGoogle();
    POI[] result = googleMapsPoiFinder.findPOIs(new LatLong(10, 10), 5, RESTAURANT);

    assertNull(result[1].getOpeningTimes());
  }

  private void mockGoogle() throws Exception {
    mockStatic(PlacesApi.class);

    when(PlacesApi.nearbySearchQuery(any(GeoApiContext.class), any(LatLng.class)))
        .thenReturn(nearbySearchRequest);

    when(nearbySearchRequest.type(any(PlaceType.class)))
        .thenReturn(nearbySearchRequest);

    when(nearbySearchRequest.rankby(any(RankBy.class)))
        .thenReturn(nearbySearchRequest);

    when(nearbySearchRequest.await())
        .thenReturn(aResponse());

    when(PlacesApi.placeDetails(any(GeoApiContext.class), anyString()))
        .thenReturn(placeDetailsRequest);

    when(placeDetailsRequest.await())
        .thenReturn(aPlaceDetails());
  }

  private PlaceDetails aPlaceDetails() throws MalformedURLException {
    PlaceDetails placeDetails = new PlaceDetails();
    placeDetails.website = new URL("http://website.com");
    placeDetails.formattedAddress = "An address somewhere";
    placeDetails.formattedPhoneNumber = "0121 237 3878";
    return placeDetails;
  }

  private PlacesSearchResponse aResponse() throws MalformedURLException {
    PlacesSearchResponse response = new PlacesSearchResponse();
    response.results = new PlacesSearchResult[]{
        aResultWithEverything(), aLimitedResult()
    };
    return response;
  }

  private PlacesSearchResult aResultWithEverything() throws MalformedURLException {
    PlacesSearchResult resultOne = new PlacesSearchResult();
    resultOne.icon = new URL("http://icon.com");
    Photo photo = new Photo();
    photo.photoReference = "photoRef";
    resultOne.photos = new Photo[]{photo};
    resultOne.openingHours = new OpeningHours();
    resultOne.openingHours.weekdayText = new String[]{
        "Monday some hours",
        "Tuesday some hours",
        "Weds some hours",
        "Thurs some hours",
        "Fri some hours",
        "Sat some hours",
        "Sun some hours"
    };
    Geometry geometry = new Geometry();
    geometry.location = new LatLng(10, 10);
    resultOne.geometry = geometry;
    resultOne.name = "A really cool place";

    return resultOne;
  }

  private PlacesSearchResult aLimitedResult() throws MalformedURLException {
    PlacesSearchResult resultOne = new PlacesSearchResult();
    resultOne.icon = new URL("http://icon.com");
    Geometry geometry = new Geometry();
    geometry.location = new LatLng(10, 10);
    resultOne.geometry = geometry;
    resultOne.name = "A really cool place";

    return resultOne;
  }
}