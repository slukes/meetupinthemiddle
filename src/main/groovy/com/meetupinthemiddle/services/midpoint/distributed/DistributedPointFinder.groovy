package com.meetupinthemiddle.services.midpoint.distributed

import com.meetupinthemiddle.model.BoundingBox
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.springframework.stereotype.Service

@Service
class DistributedPointFinder implements PointFinder {

  @Override
  List<LatLong> doFind(final BoundingBox boundingBox) {
    if (boundingBox.minLatLong == boundingBox.maxLatLong) {
      return [boundingBox.minLatLong] //No point in doing 100 if they would all be the same!
    }

    //The size of the box with distribute in can be changed by altering this figure,
    // in testing I found that restricting to the middle 25% or 50% had a negative impact on the results so left at 1.

    def scaledBoundingBox = boundingBox.scale(1)
    def minLatLong = scaledBoundingBox.minLatLong
    def maxLatLong = scaledBoundingBox.maxLatLong

    def result = []
    def latStep = Math.abs((maxLatLong.lat - minLatLong.lat) / 10)
    def longStep = Math.abs((maxLatLong.lng - minLatLong.lng) / 10)

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