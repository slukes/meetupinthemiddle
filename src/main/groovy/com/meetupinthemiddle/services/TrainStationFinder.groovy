package com.meetupinthemiddle.services
import com.meetupinthemiddle.model.LatLong
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.util.stream.Collectors

@Service
class TrainStationFinder implements PointFinder {
  @Value('${train.station.api.url.format}')
  private String trainStationApiBaseUrl

  @Autowired
  private RestTemplate restTemplate

  @Override
  LatLong[] find(final LatLong minLatLong, final LatLong maxLatLong) {
    def response = restTemplate.getForObject(String.format(trainStationApiBaseUrl, minLatLong.lat, minLatLong.lng, maxLatLong.lat, maxLatLong.lng), TrainStationResponse)
    response.toLatLongs()
  }

  static class TrainStationResponse {
    Boolean success
    TrainStation [] result

    LatLong[] toLatLongs(){
      result.toList().stream()
          .map({new LatLong(lat: it.latlong.coordinates[0], lng: it.latlong.coordinates[1])})
          .collect(Collectors.toList())
    }

    static class TrainStation {
      String stationname
      TrainStationLatLong latlong
    }

    static class TrainStationLatLong {
      float [] coordinates
    }
  }


}