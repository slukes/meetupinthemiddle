package com.meetupinthemiddle.midpoint
import com.meetupinthemiddle.model.BoundingBox
import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.model.TownAndPostcode
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import com.meetupinthemiddle.services.midpoint.AllMidPointFinder
import com.meetupinthemiddle.services.midpoint.MinJourneyTimeAndStandardDeviationCentrePicker
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static com.meetupinthemiddle.Stubs.twoPeople
import static org.junit.Assert.assertEquals
import static org.mockito.Matchers.*
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

  @Mock
  MinJourneyTimeAndStandardDeviationCentrePicker centrePicker

  @InjectMocks
  AllMidPointFinder allMidPointFinder = new AllMidPointFinder()

  @Before
  void injectPointFinders() {
    this.allMidPointFinder.pointFinders = [pointFinder1, pointFinder2]
  }

  @Test
  @Ignore
  void pointFindersAreCalled() {
    def people = twoPeople()
    people[0].latLong = new LatLong(0.1, 10)
    people[1].latLong = new LatLong(10, 0.1)

    when(geocoder.reverseGeocode(any(LatLong)))
        .thenReturn(new TownAndPostcode("Woking", "GU22 0SH"))

    when(journeyTimesFinder.getJourneyTimes(anyList(), anyList()))
        .thenReturn([(new LatLong(10000, 999999)): [10, 15],
                     (new LatLong(999999, 10000)): [15, 26]])

    when(centrePicker.pickBestPoint(anyList(), anyMap())).thenReturn(new Tuple2<CentrePoint, Map<Person,Long>>(null, null))

    allMidPointFinder.findMidpoint(people)

    verify(pointFinder1).doFind(new BoundingBox(new LatLong(0.1, 0.1), new LatLong(10, 10)))
  }



  @Test
  @Ignore
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
    when(pointFinder1.doFind(any(BoundingBox)))
        .thenReturn(points)

    allMidPointFinder.findMidpoint(people)

    verify(journeyTimesFinder).getJourneyTimes(people, points)
  }

  @Test
  @Ignore
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
    when(pointFinder1.doFind(BoundingBox))
        .thenReturn(points)

    allMidPointFinder.findMidpoint(people)

    verify(geocoder).reverseGeocode(new LatLong(10000, 999999))
  }

  @Test
  @Ignore
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