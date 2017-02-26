package com.meetupinthemiddle.model

import org.hamcrest.Matchers
import org.junit.Test

import static org.hamcrest.Matchers.equalTo

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

class BoundingBoxTest {
  @Test
  void testMiddleBoundingBoxIsCalculatedCorrectly() {
    //Given
    def minLatLng = new LatLong(0, 0)
    def maxLatLng = new LatLong(4, 4)

    //When
    def result = new BoundingBox(minLatLng, maxLatLng).scale(2)

    //Then
    assertEquals(result.minLatLong.lat, 1, 0)
    assertEquals(result.minLatLong.lng, 1, 0)
    assertEquals(result.maxLatLong.lat, 3, 0)
    assertEquals(result.maxLatLong.lng, 3, 0)
  }

  @Test
  void testQtrBoundingBoxIsCalculatedCorrectly() {
    //Given
    def minLatLng = new LatLong(0, 0)
    def maxLatLng = new LatLong(5, 5)

    //When
    def result = new BoundingBox(minLatLng, maxLatLng).scale(4)

    //Then
    assertEquals(result.minLatLong.lat, 1.875, 0)
    assertEquals(result.minLatLong.lng, 1.875, 0)
    assertEquals(result.maxLatLong.lat, 3.125, 0)
    assertEquals(result.maxLatLong.lng, 3.125, 0)
  }

  @Test
  void minLatLongCalculationCopesWithAllBeingIdentical() {
    def latLong1 = new LatLong(15, 15)
    def latLong2 = new LatLong(15, 15)

    def result = new BoundingBox([latLong1, latLong2])

    assertThat(result.minLatLong, Matchers.is(equalTo(new LatLong(15, 15))))
    assertThat(result.maxLatLong, Matchers.is(equalTo(new LatLong(15, 15))))
  }

  @Test
  void testMinAndMaxAreSelected(){
    def latLong1 = new LatLong(0, 75)
    def latLong2 = new LatLong(9, -18)

    def result = new BoundingBox([latLong1, latLong2])

    assertThat(result.minLatLong, Matchers.is(equalTo(new LatLong(0, -18))))
    assertThat(result.maxLatLong, Matchers.is(equalTo(new LatLong(9, 75))))
  }

}
