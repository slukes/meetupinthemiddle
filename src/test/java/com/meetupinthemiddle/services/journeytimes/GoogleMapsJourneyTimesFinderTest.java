package com.meetupinthemiddle.services.journeytimes;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import com.meetupinthemiddle.Stubs;
import com.meetupinthemiddle.model.LatLong;
import com.meetupinthemiddle.model.Person;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DistanceMatrixApi.class, DistanceMatrixApiRequest.class})
public class GoogleMapsJourneyTimesFinderTest {
  @Mock
  private GeoApiContext ctx;

  @InjectMocks
  private GoogleMapsJourneyTimesFinder googleMapsJourneyTimesFinder;

  @Mock
  private DistanceMatrixApiRequest request;

  @Test
  public void testApiIsCalled() throws Exception {
    List<Person> people = (List<Person>) Stubs.twoPeopleBothDriving();
    List<LatLong> points = latLongs();
    mockGoogle(people, points);

    googleMapsJourneyTimesFinder.getJourneyTimes(people, points);

    verifyStatic();
    DistanceMatrixApi.newRequest(ctx);

    ArgumentCaptor<LatLng[]> originCaptor = ArgumentCaptor.forClass(LatLng[].class);
    verify(request).origins(originCaptor.capture());
    List origins = originCaptor.getAllValues();

    assertEquals(people.get(0).getLatLong().getLat(), ((LatLng)origins.get(0)).lat);
    assertEquals(people.get(0).getLatLong().getLng(), ((LatLng)origins.get(0)).lng);
    assertEquals(people.get(1).getLatLong().getLat(), ((LatLng)origins.get(1)).lat);
    assertEquals(people.get(1).getLatLong().getLng(), ((LatLng)origins.get(1)).lng);
  }

  @Test
  public void testDifferentTransportModesAreSplit() throws Exception {
    List<Person> people = (List<Person>) Stubs.twoPeople();
    List<LatLong> points = latLongs();
    mockGoogle(people, points);

    googleMapsJourneyTimesFinder.getJourneyTimes(people, points);

    verifyStatic(times(2));
    DistanceMatrixApi.newRequest(ctx);
  }

  @Test
  public void testBatching() throws Exception {
    List<Person> people = (List<Person>) Stubs.twoPeople();
    List<LatLong> points = bigLatLongs();
    mockGoogle(people, points);

    googleMapsJourneyTimesFinder.getJourneyTimes(people, points);

    verifyStatic(times(4));
    DistanceMatrixApi.newRequest(ctx);
  }

  private void mockGoogle(final List<Person> people, final List<LatLong> points) throws Exception {
    DistanceMatrix distanceMatrix = aResponse(people, points);

    mockStatic(DistanceMatrixApi.class);

    when(DistanceMatrixApi.newRequest(ctx))
        .thenReturn(request);

    when(request.mode(any(TravelMode.class)))
        .thenReturn(request);

    when(request.origins(any(LatLng[].class)))
        .thenReturn(request);

    when(request.destinations(any(LatLng[].class)))
        .thenReturn(request);

    when(request.await())
        .thenReturn(distanceMatrix);
  }

  private DistanceMatrix aResponse(List<Person> people, List<LatLong> points) {
    String[] origins = new String[people.size()];
    String[] destinations = new String[points.size()];
    DistanceMatrixRow[] rows = new DistanceMatrixRow[people.size()];

    for (int i = 0; i < people.size(); i++) {
      origins[i] = RandomStringUtils.random(20);
    }

    for (int i = 0; i < points.size(); i++) {
      destinations[i] = RandomStringUtils.random(20);
    }

    for (int i = 0; i < rows.length; i++) {
      rows[i] = new DistanceMatrixRow();
      rows[i].elements = new DistanceMatrixElement[destinations.length];
      for (int j = 0; j < destinations.length; j++) {
        rows[i].elements[j] = new DistanceMatrixElement();
        Duration duration = new Duration();
        duration.inSeconds = RandomUtils.nextInt();
        rows[i].elements[j].duration = duration;
      }
    }

    return new DistanceMatrix(origins, destinations, rows);
  }

  private List<LatLong> latLongs() {
    return asList(new LatLong(10, 10), new LatLong(20, 20));
  }

  private List<LatLong> bigLatLongs() {
    List<LatLong> result = new ArrayList<>();
    for(int i = 0; i < 30; i++){
      result.add(new LatLong(RandomUtils.nextDouble(), RandomUtils.nextDouble()));
    }

    return result;
  }
}