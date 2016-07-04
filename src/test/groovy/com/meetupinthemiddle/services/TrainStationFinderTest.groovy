package com.meetupinthemiddle.services

import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@ContextConfiguration(classes = TrainStationFinderTestConfig)
@RunWith(SpringRunner)
class TrainStationFinderTest {

//  @Autowired
//  PointFinder trainStationFinder
//
//  @Autowired
//  RestTemplate restTemplate
//
//  @Autowired
//  Geocoder geocoder
//
//  @Value('${train.station.api.url.format}')
//  String trainStationApiBaseUrl
//
//  @Before
//  void resetMocks(){
//    reset(restTemplate)
//  }
//
//  @Test
//  void apiIsCalled() throws Exception {
//    //Given
//    when(restTemplate.getForObject(anyString(), eq(TrainStationResponse))).thenReturn(aTrainStationResponse())
//    //When
//    trainStationFinder.doFind(new LatLong(lat: 1, lng: 1), new LatLong(lat: 3, lng: 4))
//    //Then
//    def url = format(trainStationApiBaseUrl, "1.0", "1.0", "3.0", "4.0")
//    verify(restTemplate).getForObject(url, TrainStationResponse)
//  }
//
//  @Test
//  void latLongObjectContainsCoords() throws Exception {
//    //Given
//    when(restTemplate.getForObject(anyString(), eq(TrainStationResponse))).thenReturn(aTrainStationResponse())
////    when(geocoder.geocode(anyString())).thenReturn(aLatLong())
//    //When
//    def resp = trainStationFinder.doFind(new LatLong(lat: 1, lng: 1), new LatLong(lat: 3, lng: 4))
//    //Then
//    assert resp[0].lat == 2.2f
//    assert resp[0].lng == 1.2f
//  }
//
//  private aLatLong() {
//    new LatLong(lat: 1.2f, lng: 2.2f)
//  }
//
//  private aTrainStationResponse() {
//    new TrainStationResponse().with {
//      success = true
//      result =
//      [new TrainStation().with {
//        stationname =  "Birmingham New Street"
//        latlong = new TrainStationResponse.TrainStationLatLong(coordinates: [1.2f, 2.2f])
//        it
//      }]
//      it
//    }
//  }
}