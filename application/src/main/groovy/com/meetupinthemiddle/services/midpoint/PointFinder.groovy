package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.BoundingBox
import com.meetupinthemiddle.model.LatLong

interface PointFinder {
  List<LatLong> doFind(BoundingBox boundingBox)
}