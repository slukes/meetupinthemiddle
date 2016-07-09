package com.meetupinthemiddle.services.geocode;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.*;
import com.meetupinthemiddle.model.LatLong;
import com.meetupinthemiddle.model.TownAndPostcode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.google.maps.model.AddressComponentType.*;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.same;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GeocodingApi.class, GeocodingApiRequest.class})
public class GoogleMapsGeocoderTest {
  @InjectMocks
  GoogleMapsGeocoder googleMapsGeocoder = new GoogleMapsGeocoder();

  @Mock
  GeocodingApiRequest request;

  @Mock
  GeoApiContext ctx;

  @Test
  public void verifyGoogleMapsApiCalled() throws Exception {
    //Given
    mockStatic(GeocodingApi.class);

    when(GeocodingApi.geocode(ctx, "Woking"))
        .thenReturn(request);

    when(request.await())
        .thenReturn(new GeocodingResult[]{readingResult(), usGeocodingResult()});

    //When
    googleMapsGeocoder.geocode("Woking");

    //Then
    verifyStatic();
    GeocodingApi.geocode(ctx, "Woking");
    Mockito.verify(request).await();
  }

  @Test
  public void onlyGbResultsIncluded() throws Exception {
    //Given
    mockStatic(GeocodingApi.class);

    when(GeocodingApi.geocode(ctx, "Woking"))
        .thenReturn(request);

    GeocodingResult gbResult = readingResult();
    GeocodingResult usResult = usGeocodingResult();
    when(request.await())
        .thenReturn(new GeocodingResult[]{gbResult, usResult});

    //When
    LatLong result = googleMapsGeocoder.geocode("Woking");

    //Then
    assertThat(result, equalTo(new LatLong(gbResult.geometry.location.lat, gbResult.geometry.location.lng)));
  }

  @Test
  public void nullIfNoResults() throws Exception {
    //Given
    mockStatic(GeocodingApi.class);

    when(GeocodingApi.geocode(ctx, "Woking"))
        .thenReturn(request);

    when(request.await())
        .thenReturn(new GeocodingResult[]{});

    //When
    LatLong result = googleMapsGeocoder.geocode("Woking");

    //Then
    assertThat(result, nullValue());
  }

  @Test
  public void testReverseGeocodeApiIsCalled() throws Exception {
    //Given
    mockStatic(GeocodingApi.class);

    //Have to use any since the google maps model doesn't implement equals and hashcode.
    when(GeocodingApi.reverseGeocode(Mockito.any(GeoApiContext.class), Mockito.any(LatLng.class)))
        .thenReturn(request);

    when(request.await())
        .thenReturn(new GeocodingResult[]{readingResult(), usGeocodingResult()});

    //When
    googleMapsGeocoder.reverseGeocode(new LatLong(123, 321));

    //Then
    verifyStatic();
    ArgumentCaptor<LatLng> captor = ArgumentCaptor.forClass(LatLng.class);
    GeocodingApi.reverseGeocode(same(ctx), captor.capture());
    LatLng calledLatLng = captor.getValue();
    assertEquals(calledLatLng.lat, 123.0);
    assertEquals(calledLatLng.lng, 321.0);
  }

  @Test
  public void testReverseGeocodeResult() throws Exception {
    //Given
    mockStatic(GeocodingApi.class);

    //Have to use any since the google maps model doesn't implement equals and hashcode.
    when(GeocodingApi.reverseGeocode(Mockito.any(GeoApiContext.class), Mockito.any(LatLng.class)))
        .thenReturn(request);

    when(request.await())
        .thenReturn(new GeocodingResult[]{readingResult()});

    //When
    TownAndPostcode result = googleMapsGeocoder.reverseGeocode(new LatLong(123, 321));

    //Then
    assertEquals("Reading", result.getTown());
    assertEquals("RG1 8DE", result.getPostcode());
  }

  @Test
  public void testReverseGeocodeGoesDownToNextLevelIfNoTown() throws Exception {
    //Given
    mockStatic(GeocodingApi.class);

    when(GeocodingApi.reverseGeocode(Mockito.any(GeoApiContext.class), Mockito.any(LatLng.class)))
        .thenReturn(request);

    when(request.await())
        .thenReturn(new GeocodingResult[]{noTownResultWithL2()});

    //When
    TownAndPostcode result = googleMapsGeocoder.reverseGeocode(new LatLong(123, 321));

    //Then
    assertEquals("Woking", result.getTown());
    assertEquals("GU22 0SH", result.getPostcode());
  }

  @Test
  public void testNoTownNoPostcodeAndNoNPE() throws Exception {
    //Given
    mockStatic(GeocodingApi.class);

    when(GeocodingApi.reverseGeocode(Mockito.any(GeoApiContext.class), Mockito.any(LatLng.class)))
        .thenReturn(request);

    when(request.await())
        .thenReturn(new GeocodingResult[]{noTownOrPostcode()});

    //When
    TownAndPostcode result = googleMapsGeocoder.reverseGeocode(new LatLong(123, 321));

    //Then
    assertEquals("", result.getTown());
    assertEquals("", result.getPostcode());
  }

  private GeocodingResult noTownResultWithL2() {
    GeocodingResult geocodingResult = new GeocodingResult();

    AddressComponent country = new AddressComponent();
    country.shortName = "GB";
    country.types = new AddressComponentType[]{COUNTRY};

    AddressComponent adminL2 = new AddressComponent();
    adminL2.shortName = "Woking";
    adminL2.longName = "Woking";
    adminL2.types = new AddressComponentType[]{ADMINISTRATIVE_AREA_LEVEL_2};

    AddressComponent postcode = new AddressComponent();
    postcode.longName = "GU22 0SH";
    postcode.types = new AddressComponentType[]{POSTAL_CODE};

    geocodingResult.addressComponents = new AddressComponent[]{country, adminL2, postcode};
    geocodingResult.geometry = new Geometry();
    geocodingResult.geometry.location = new LatLng(312, 312);
    return geocodingResult;
  }

  private GeocodingResult noTownOrPostcode() {
    GeocodingResult geocodingResult = new GeocodingResult();

    AddressComponent country = new AddressComponent();
    country.shortName = "GB";
    country.types = new AddressComponentType[]{COUNTRY};

    geocodingResult.addressComponents = new AddressComponent[]{country};
    geocodingResult.geometry = new Geometry();
    geocodingResult.geometry.location = new LatLng(312, 312);
    return geocodingResult;
  }

  private GeocodingResult readingResult() {
    GeocodingResult geocodingResult = new GeocodingResult();

    AddressComponent country = new AddressComponent();
    country.shortName = "GB";
    country.types = new AddressComponentType[]{COUNTRY};

    AddressComponent town = new AddressComponent();
    town.shortName = "Reading";
    town.longName = "Reading";
    town.types = new AddressComponentType[]{POSTAL_TOWN};

    AddressComponent postcode = new AddressComponent();
    postcode.longName = "RG1 8DE";
    postcode.types = new AddressComponentType[]{POSTAL_CODE};

    geocodingResult.addressComponents = new AddressComponent[]{country, town, postcode};
    geocodingResult.geometry = new Geometry();
    geocodingResult.geometry.location = new LatLng(123, 123);
    return geocodingResult;
  }

  private GeocodingResult usGeocodingResult() {
    GeocodingResult geocodingResult = new GeocodingResult();
    AddressComponent addressComponent = new AddressComponent();
    addressComponent.shortName = "US";
    addressComponent.types = new AddressComponentType[]{COUNTRY};
    geocodingResult.addressComponents = new AddressComponent[]{addressComponent};
    geocodingResult.geometry = new Geometry();
    geocodingResult.geometry.location = new LatLng(321, 321);
    return geocodingResult;
  }
}