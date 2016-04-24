package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.LatLong
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.util.stream.Collectors

import static java.lang.String.format

@Service
class TrainStationFinder implements PointFinder {
  @Value('${train.station.api.url.format}')
  private String trainStationApiBaseUrl

  @Autowired
  private RestTemplate restTemplate

  @Autowired
  private Geocoder geocoder

  /**
   * Finds train stations with in a given rectangular area
   * @param minLatLong the bottom left corner of the area
   * @param maxLatLong the top right corner of the area
   * @return
   */
  @Override
  List<LatLong> find(final LatLong minLatLong, final LatLong maxLatLong) {
    def response = restTemplate.getForObject(format(trainStationApiBaseUrl, minLatLong.lat, minLatLong.lng,
        maxLatLong.lat, maxLatLong.lng), TrainStationResponse)
    toLatLongs(response)
  }

   /*
   Converts between train station response and MeetUpInTheMiddle model LatLongs
   This has had to be converted to call the geocoder since it appears that the types of coordinates are different
   To what is used by google maps
   */
  //TODO find out if I can get around having to geocode - can I convert between the types of coords?
  private List<LatLong> toLatLongs(response) {
    response.result.toList().stream().parallel()
        .map({ geocoder.geocode(it.stationname) })
        .collect(Collectors.toList())
  }

  //Reprsentation of the model returned by the api
  //I have chosen to have this as an inner class, since there is no need for other classes to be aware of it.
  static class TrainStationResponse {
    Boolean success
    TrainStation[] result

    static class TrainStation {
      String stationname
      TrainStationLatLong latlong
    }

    static class TrainStationLatLong {
      float[] coordinates
    }
  }
}