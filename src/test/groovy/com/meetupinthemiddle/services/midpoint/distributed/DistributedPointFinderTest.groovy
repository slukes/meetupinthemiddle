package com.meetupinthemiddle.services.midpoint.distributed

import com.meetupinthemiddle.model.BoundingBox
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.junit.Test

import static org.hamcrest.Matchers.hasSize
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

class DistributedPointFinderTest {
  PointFinder distributedFinder = new DistributedPointFinder()

  @Test
  void testPointsAreDistributed() {
    def result = distributedFinder.doFind(new BoundingBox(new LatLong(0.0, 0.0),
        new LatLong(10, 10)))

    def expected = []

    0.upto(9) {
      lat ->
        0.upto(9) {
          lng ->
            expected << new LatLong(lat, lng)
        }
    }

    assertEquals(expected, result)
  }

  @Test
  void copeWithNegativeNumbers() {
    def result = distributedFinder.doFind(new BoundingBox(new LatLong(-10, -10),
        new LatLong(0, 0)))

    def expected = []

    (-10).upto(-1) {
      lat ->
        (-10).upto(-1) {
          lng ->
            expected << new LatLong(lat, lng)
        }
    }

    assertEquals(expected, result)
  }

  @Test
  void copeWithNoDifference() {
    def result = distributedFinder.doFind(new BoundingBox(new LatLong(10, 10),
        new LatLong(10, 10)))

    assertThat(result, hasSize(1))
  }
}

