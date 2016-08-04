package com.meetupinthemiddle.services.poi;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import com.meetupinthemiddle.model.LatLong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.meetupinthemiddle.Stubs.aPlacesSearchResponse;
import static com.meetupinthemiddle.model.POIType.MEETING;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PlacesApi.class, PlacesSearchResult.class, NearbySearchRequest.class})
public class GoogleMapsPOIFinderUsingKeywordTest {
  @InjectMocks
  GooglePlacesPOIFinderUsingKeywords googleMapsPoiFinder = new GooglePlacesPOIFinderUsingKeywords();

  @Mock
  GeoApiContext ctx;

  @Mock
  NearbySearchRequest nearbySearchRequest;

  @Test
  public void testApiIsCalled() throws Exception {
    mockGoogle();

    googleMapsPoiFinder.doSearch(new LatLong(10, 10), MEETING);

    verifyStatic();
    ArgumentCaptor<LatLng> argumentCaptor = ArgumentCaptor.forClass(LatLng.class);
    PlacesApi.nearbySearchQuery(same(ctx), argumentCaptor.capture());

    LatLng actual = argumentCaptor.getValue();
    assertEquals(10d, actual.lat);
    assertEquals(10d, actual.lng);

    verify(nearbySearchRequest).keyword("meeting room");
  }

  private void mockGoogle() throws Exception {
    mockStatic(PlacesApi.class);

    when(PlacesApi.nearbySearchQuery(any(GeoApiContext.class), any(LatLng.class)))
        .thenReturn(nearbySearchRequest);

    when(nearbySearchRequest.keyword("meeting room"))
        .thenReturn(nearbySearchRequest);

    when(nearbySearchRequest.rankby(any(RankBy.class)))
        .thenReturn(nearbySearchRequest);

    when(nearbySearchRequest.await())
        .thenReturn(aPlacesSearchResponse());
  }
}