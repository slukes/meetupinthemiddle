package com.meetupinthemiddle.services
import com.meetupinthemiddle.model.LatLong

interface PointFinder {
  List<LatLong> find(LatLong minLatLong, LatLong maxLatLong)
}