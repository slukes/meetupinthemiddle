package com.meetupinthemiddle.services.midpoint.distributed

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.springframework.stereotype.Service

@Service
class DistributedPointFinder implements PointFinder {

  @Override
  List<LatLong> doFind(final LatLong minLatLong, final LatLong maxLatLong) {
    def result = []
    def latStep = Math.abs((maxLatLong.lat - minLatLong.lat) / 10)
    def longStep = Math.abs((maxLatLong.lng - minLatLong.lng) / 10)

    if (minLatLong == maxLatLong) {
      return [minLatLong] //No point in doing 100 if they would all be the same!
    }

    0.upto(9) {
      latStage ->
        0.upto(9) {
          lngStage ->
            result << new LatLong(minLatLong.lat + (latStep * latStage), minLatLong.lng + (longStep * lngStage))
        }
    }

    result
  }
}
