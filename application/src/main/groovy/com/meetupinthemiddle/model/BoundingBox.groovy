package com.meetupinthemiddle.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class BoundingBox {
  final LatLong minLatLong
  final LatLong maxLatLong

  BoundingBox(LatLong minLatLong, LatLong maxLatLong) {
    this.minLatLong = minLatLong
    this.maxLatLong = maxLatLong
  }

  BoundingBox(List<LatLong> latLongs) {
    def min = new LatLong(Double.MAX_VALUE, Double.MAX_VALUE)
    def max = new LatLong(-Double.MAX_VALUE, -Double.MAX_VALUE) //Double.MIN_VALUE is actually positive!

    latLongs.each {
      if (it.lat < min.lat) {
        min.lat = it.lat
      }

      if (it.lng < min.lng) {
        min.lng = it.lng
      }

      if (it.lat > max.lat) {
        max.lat = it.lat
      }

      if (it.lng > max.lng) {
        max.setLng(it.lng)
      }
    }

    this.minLatLong = min
    this.maxLatLong = max
  }

  BoundingBox scale(double factor) {
    def diffInLat = (maxLatLong.lat - minLatLong.lat)
    def diffInLong = (maxLatLong.lng - minLatLong.lng)

    def widthOfBox = diffInLat / factor
    def widthPadding = (diffInLat - widthOfBox) / 2

    def heightOfBox = diffInLong / factor
    def heightPadding = (diffInLong - heightOfBox) / 2

    new BoundingBox((new LatLong(minLatLong.lat + widthPadding, minLatLong.lng + heightPadding)),
        new LatLong(maxLatLong.lat - widthPadding, maxLatLong.lng - heightPadding))
  }
}