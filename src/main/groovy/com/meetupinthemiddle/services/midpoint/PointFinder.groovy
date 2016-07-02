package com.meetupinthemiddle.services.midpoint
import com.meetupinthemiddle.model.LatLong
import org.springframework.cache.annotation.Cacheable

interface PointFinder {
  @Cacheable("points")
  List<LatLong> doFind(LatLong minLatLong, LatLong maxLatLong)
}