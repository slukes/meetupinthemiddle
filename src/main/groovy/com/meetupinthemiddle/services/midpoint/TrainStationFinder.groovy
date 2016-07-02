package com.meetupinthemiddle.services.midpoint
import com.meetupinthemiddle.model.LatLong
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
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

  /**
   * Finds train stations with in a given rectangular area
   * @param minLatLong the bottom left corner of the area
   * @param maxLatLong the top right corner of the area
   * @return
   */
  @Override
  @Cacheable("stations")
  List<LatLong> doFind(final LatLong minLatLong, final LatLong maxLatLong) {
    def response = restTemplate.getForObject(format(trainStationApiBaseUrl, minLatLong.lat, minLatLong.lng,
        maxLatLong.lat, maxLatLong.lng), TrainStationResponse)
    toLatLongs(response)
  }

  private List<LatLong> toLatLongs(TrainStationResponse response) {
    response.result.toList().stream()
        .map({ new LatLong(it.latlong.coordinates[1], it.latlong.coordinates[0]) })
        .collect(Collectors.toList())
  }

  //Representation of the model returned by the api
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