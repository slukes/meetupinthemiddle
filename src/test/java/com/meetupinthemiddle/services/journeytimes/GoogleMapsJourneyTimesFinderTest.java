package com.meetupinthemiddle.services.journeytimes;

import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlacesSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PlacesApi.class, PlacesSearchResult.class, PlaceDetailsRequest.class})
public class GoogleMapsJourneyTimesFinderTest {
  @Test
  public void testApiIsCalled(){

  }

  @Test
  public void testDifferentTransportModesAreSplit(){

  }

  @Test
  public void testBatching(){

  }
}
