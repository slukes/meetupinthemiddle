package com.meetupinthemiddle.services.midpoint
import com.meetupinthemiddle.model.LatLong

interface PointFinder {
  List<LatLong> doFind(LatLong minLatLong, LatLong maxLatLong)
}