package com.meetupinthemiddle.services.poi;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.meetupinthemiddle.model.LatLong;
import com.meetupinthemiddle.model.POI;
import com.meetupinthemiddle.model.POIType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static com.meetupinthemiddle.Stubs.aPlaceDetails;
import static com.meetupinthemiddle.Stubs.aPlacesSearchResponse;
import static com.meetupinthemiddle.model.POIType.RESTAURANT;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PlacesApi.class, PlaceDetailsRequest.class})
public class GoogleMapsPOIFinderTemplateTest {

  @Mock
  GeoApiContext ctx;

  @Mock
  PlaceDetailsRequest placeDetailsRequest;

  PlacesSearchResult result;

  @InjectMocks
  GooglePlacesPOIFinderTemplate googleMapsPoiFinder;


  public GoogleMapsPOIFinderTemplateTest(){
    result = new PlacesSearchResult();
    result.geometry = new Geometry();
    result.geometry.location = new LatLng(10, 10);

    googleMapsPoiFinder = new GooglePlacesPOIFinderTemplate() {
      @Override
      protected PlacesSearchResult[] doSearch(final LatLong location, final POIType type) {
        return aPlacesSearchResponse().results;
      }
    };
  }

  @Before
  public void injectUrl() {
    ReflectionTestUtils.setField(googleMapsPoiFinder, "photoUrlFormat", "http://blah.com/blah?photoreference=%s");
  }

  @Test
  public void testPlacesDetailsApiIsCalled() throws Exception {
    mockGoogle();

    googleMapsPoiFinder.findPOIs(new LatLong(10, 10), 5, RESTAURANT);

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

    when(PlacesApi.placeDetails(any(GeoApiContext.class), anyString()))
        .thenReturn(placeDetailsRequest);

    when(placeDetailsRequest.await())
        .thenReturn(aPlaceDetails());
  }
}