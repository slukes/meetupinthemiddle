package com.meetupinthemiddle.services
import com.meetupinthemiddle.model.LatLong

interface PointFinder {
  LatLong [] find(LatLong minLatLong, LatLong maxLatLong)
}