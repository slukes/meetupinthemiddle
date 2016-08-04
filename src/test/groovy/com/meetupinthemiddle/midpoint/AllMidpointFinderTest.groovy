package com.meetupinthemiddle.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.TownAndPostcode
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import com.meetupinthemiddle.services.midpoint.AllMidPointFinder
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static com.meetupinthemiddle.Stubs.twoPeople
import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat
import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyList
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

@RunWith(MockitoJUnitRunner)
class AllMidpointFinderTest {
  @Mock
  PointFinder pointFinder1

  @Mock
  PointFinder pointFinder2

  @Mock
  Geocoder geocoder

  @Mock
  JourneyTimesFinder journeyTimesFinder

  @InjectMocks
  AllMidPointFinder allMidPointFinder = new AllMidPointFinder()

  @Before
  void injectPointFinders() {
    this.allMidPointFinder.pointFinders = [pointFinder1, pointFinder2]
  }

  @Test
  void pointFindersAreCalledWithMinMaxLatLongs() {
    def people = twoPeople()
    people[0].latLong = new LatLong(0.1, 10)
    people[1].latLong = new LatLong(10, 0.1)

    when(geocoder.reverseGeocode(any(LatLong)))
        .thenReturn(new TownAndPostcode("Woking", "GU22 0SH"))

    when(journeyTimesFinder.getJourneyTimes(anyList(), anyList()))
        .thenReturn([(new LatLong(10000, 999999)): [10, 15],
                     (new LatLong(999999, 10000)): [15, 26]])

    allMidPointFinder.findMidpoint(people)

    verify(pointFinder1).doFind(new LatLong(0.1, 0.1), new LatLong(10, 10))
  }

  @Test
  void minLatLongCalculationCopesWithAllBeingIdentical() {
    def people = twoPeople()
    people[0].latLong = new LatLong(15, 15)
    people[1].latLong = new LatLong(15, 15)

    when(geocoder.reverseGeocode(any(LatLong)))
        .thenReturn(new TownAndPostcode("Woking", "GU22 0SH"))

    when(journeyTimesFinder.getJourneyTimes(anyList(), anyList()))
        .thenReturn([(new LatLong(10000, 999999)): [10, 15],
                     (new LatLong(999999, 10000)): [15, 26]])

    allMidPointFinder.findMidpoint(people)

    verify(pointFinder1).doFind(new LatLong(15, 15), new LatLong(15, 15))
  }

  @Test
  void journeyTimesFinderIsCalled() {
    def people = twoPeople()
    people[0].latLong = new LatLong(15, 15)
    people[1].latLong = new LatLong(15, 15)

    when(geocoder.reverseGeocode(any(LatLong)))
        .thenReturn(new TownAndPostcode("Woking", "GU22 0SH"))

    when(journeyTimesFinder.getJourneyTimes(anyList(), anyList()))
        .thenReturn([(new LatLong(10000, 999999)): [10, 15],
                     (new LatLong(999999, 10000)): [15, 26]])

    def points = [new LatLong(12345, 45321)]
    when(pointFinder1.doFind(any(LatLong), any(LatLong)))
        .thenReturn(points)

    allMidPointFinder.findMidpoint(people)

    verify(journeyTimesFinder).getJourneyTimes(people, points)
  }

  @Test
  void pointWithLowestTravelTimeIsPicked() {
    def people = twoPeople()
    people[0].latLong = new LatLong(15, 15)
    people[1].latLong = new LatLong(15, 15)

    when(geocoder.reverseGeocode(any(LatLong)))
        .thenReturn(new TownAndPostcode("Woking", "GU22 0SH"))

    when(journeyTimesFinder.getJourneyTimes(anyList(), anyList()))
        .thenReturn([(new LatLong(10000, 999999)): [10, 15],
                     (new LatLong(999999, 10000)): [15, 26]])

    Tuple2<CentrePoint, Map<Person, Long>> result = allMidPointFinder.findMidpoint(people)

    assertThat(result.first.latLong, equalTo(new LatLong(10000, 999999)))
  }

  @Test
  void reverseGeocodeIsCalled() {
    def people = twoPeople()
    people[0].latLong = new LatLong(15, 15)
    people[1].latLong = new LatLong(15, 15)

    when(geocoder.reverseGeocode(any(LatLong)))
        .thenReturn(new TownAndPostcode("Woking", "GU22 0SH"))

    when(journeyTimesFinder.getJourneyTimes(anyList(), anyList()))
        .thenReturn([(new LatLong(10000, 999999)): [10, 15],
                     (new LatLong(999999, 10000)): [15, 26]])

    def points = [new LatLong(12345, 45321)]
    when(pointFinder1.doFind(any(LatLong), any(LatLong)))
        .thenReturn(points)

    allMidPointFinder.findMidpoint(people)

    verify(geocoder).reverseGeocode(new LatLong(10000, 999999))
  }

  @Test
  void responseIsBuilt() {
    def people = twoPeople()
    people[0].latLong = new LatLong(15, 15)
    people[1].latLong = new LatLong(15, 15)

    when(geocoder.reverseGeocode(any(LatLong)))
        .thenReturn(new TownAndPostcode("Woking", "GU22 0SH"))

    when(journeyTimesFinder.getJourneyTimes(anyList(), anyList()))
        .thenReturn([(new LatLong(10000, 999999)): [10, 15],
                     (new LatLong(999999, 10000)): [15, 26]])

    def result = allMidPointFinder.findMidpoint(people)
    assert result
    assertEquals(result.first.latLong, new LatLong(10000, 999999))
    assertEquals(result.first.postCode, "GU22 0SH")
    assertEquals(result.first.locality, "Woking")
  }
}