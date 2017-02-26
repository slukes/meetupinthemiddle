package com.meetupinthemiddle.services.poi;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import com.meetupinthemiddle.model.LatLong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.meetupinthemiddle.Stubs.aPlacesSearchResponse;
import static com.meetupinthemiddle.model.POIType.RESTAURANT;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PlacesApi.class, PlacesSearchResult.class, NearbySearchRequest.class})
public class GoogleMapsPOIFinderUsingTypesTest {
  @InjectMocks
  GooglePlacesPOIFinderTemplate googleMapsPoiFinder = new GooglePlacesPOIFinderUsingTypes();

  @Mock
  GeoApiContext ctx;

  @Mock
  NearbySearchRequest nearbySearchRequest;

  @Test
  public void testApiIsCalled() throws Exception {
    mockGoogle();

    googleMapsPoiFinder.doSearch(new LatLong(10, 10), RESTAURANT, null);

    verifyStatic();
    ArgumentCaptor<LatLng> argumentCaptor = ArgumentCaptor.forClass(LatLng.class);
    PlacesApi.nearbySearchQuery(same(ctx), argumentCaptor.capture());

    LatLng actual = argumentCaptor.getValue();
    assertEquals(10d, actual.lat);
    assertEquals(10d, actual.lng);

    Mockito.verify(nearbySearchRequest).type(PlaceType.RESTAURANT);
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
        .thenReturn(aPlacesSearchResponse());
  }
}