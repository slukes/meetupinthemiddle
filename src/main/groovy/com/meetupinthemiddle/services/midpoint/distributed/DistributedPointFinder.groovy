package com.meetupinthemiddle.services.midpoint.distributed

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.springframework.stereotype.Service

@Service
class DistributedPointFinder implements PointFinder {

  @Override
  List<LatLong> doFind(final LatLong minLatLong, final LatLong maxLatLong) {
    List<LatLong> result = []
    def latStep = Math.abs((maxLatLong.lat - minLatLong.lat) / 10)
    def longStep = Math.abs((maxLatLong.lng - minLatLong.lng) / 10)

    for (def i = 0; i < 10; i++) {
      for (def j = 0; j < 10; j++) {
        result << new LatLong(minLatLong.lat + (latStep * i), minLatLong.lng + (longStep * j))
      }
    }

    result
  }
}
