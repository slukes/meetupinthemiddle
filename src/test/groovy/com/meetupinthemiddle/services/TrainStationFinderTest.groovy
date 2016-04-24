package com.meetupinthemiddle.services
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.services.TrainStationFinder.TrainStationResponse
import com.meetupinthemiddle.services.TrainStationFinder.TrainStationResponse.TrainStation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

import static java.lang.String.format
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.reset
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

@ContextConfiguration(classes = TrainStationFinderTestConfig)
@RunWith(SpringRunner)
public class TrainStationFinderTest {

  @Autowired
  PointFinder trainStationFinder

  @Autowired
  RestTemplate restTemplate

  @Autowired
  Geocoder geocoder

  @Value('${train.station.api.url.format}')
  String trainStationApiBaseUrl

  @Before
  void resetMocks(){
    reset(restTemplate)
  }

  @Test
  void apiIsCalled() throws Exception {
    //Given
    when(restTemplate.getForObject(anyString(), eq(TrainStationResponse))).thenReturn(aTrainStationResponse())
    //When
    trainStationFinder.find(new LatLong(lat: 1, lng: 1), new LatLong(lat: 3, lng: 4))
    //Then
    def url = format(trainStationApiBaseUrl, "1.0", "1.0", "3.0", "4.0")
    verify(restTemplate).getForObject(url, TrainStationResponse)
  }

  @Test
  void latLongObjectContainsCoords() throws Exception {
    //Given
    when(restTemplate.getForObject(anyString(), eq(TrainStationResponse))).thenReturn(aTrainStationResponse())
    when(geocoder.geocode(anyString())).thenReturn(aLatLong())
    //When
    def resp = trainStationFinder.find(new LatLong(lat: 1, lng: 1), new LatLong(lat: 3, lng: 4))
    //Then
    assert resp[0].lat == 1.2f
    assert resp[0].lng == 2.2f
  }

  private aLatLong() {
    new LatLong(lat: 1.2f, lng: 2.2f)
  }

  private aTrainStationResponse() {
    new TrainStationResponse().with {
      success = true
      result =
      [new TrainStation().with {
        stationname =  "Birmingham New Street"
        latlong = new TrainStationResponse.TrainStationLatLong(coordinates: [1.2f, 2.2f])
        it
      }]
      it
    }
  }
}