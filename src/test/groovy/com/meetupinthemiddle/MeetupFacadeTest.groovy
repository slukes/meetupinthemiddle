package com.meetupinthemiddle
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POIType
import com.meetupinthemiddle.services.MeetUpFacade
import com.meetupinthemiddle.services.MeetUpFacadeImpl
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import com.meetupinthemiddle.services.midpoint.MidpointFinder
import com.meetupinthemiddle.services.poi.POIFinder
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static com.meetupinthemiddle.Stubs.aCentrePointAndJourneyTimes
import static com.meetupinthemiddle.Stubs.aRequestObj
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner)
class MeetupFacadeTest {
  @Mock
  MidpointFinder midPointFinder

  @Mock
  private POIFinder poiFinderService

  @Mock
  private JourneyTimesFinder journeyTimesFinder

  @Mock
  private Geocoder geocoder

  @InjectMocks
  MeetUpFacade meetUpFacade = new MeetUpFacadeImpl()

  @Test
  void testGeocoderIsCalledIfLatLongMissing() {
    //Given
    def request = aRequestObj()
    request.people[0].latLong = null
    when(poiFinderService.findPOIs(any(LatLong), any(Integer), any(POIType)))
        .thenReturn(null)
    when(midPointFinder.findMidpoint(any(List)))
        .thenReturn(aCentrePointAndJourneyTimes())

    //When
    meetUpFacade.doSearch(request)

    //Then
    verify(geocoder).geocode(request.people[0].from)
  }

  @Test
  void testGeocoderIsNotCalledIfLatLongPresent() {
    //Given
    def request = aRequestObj()
    when(poiFinderService.findPOIs(any(LatLong), any(Integer), any(POIType)))
        .thenReturn(null)
    when(midPointFinder.findMidpoint(any(List)))
        .thenReturn(aCentrePointAndJourneyTimes())

    //When
    meetUpFacade.doSearch(request)

    //Then
    verify(geocoder, times(0)).geocode(request.people[0].from)
  }

  @Test
  void testMidpointFinderIsCalled() {
    //Given
    def request = aRequestObj()
    when(poiFinderService.findPOIs(any(LatLong), any(Integer), any(POIType)))
        .thenReturn(null)
    when(midPointFinder.findMidpoint(any(List)))
        .thenReturn(aCentrePointAndJourneyTimes())

    //When
    meetUpFacade.doSearch(request)

    //Then
    verify(midPointFinder).findMidpoint(request.people)
  }

  @Test
  void testPoiFinderIsCalled() {
    //Given
    def request = aRequestObj()
    when(poiFinderService.findPOIs(any(LatLong), any(Integer), any(POIType)))
        .thenReturn(null)

    def centrePointAndJourneyTimes = aCentrePointAndJourneyTimes()
    when(midPointFinder.findMidpoint(any(List)))
        .thenReturn(centrePointAndJourneyTimes)

    //When
    meetUpFacade.doSearch(request)

    //Then
    verify(poiFinderService).findPOIs(centrePointAndJourneyTimes.first.latLong, 5, request.poiType)
  }

  @Test
  void testNoExceptionIfTravelTimeIsZero(){
    //Given
    def request = aRequestObj()
    when(poiFinderService.findPOIs(any(LatLong), any(Integer), any(POIType)))
        .thenReturn(null)

    def centrePointAndJourneyTimes = aCentrePointAndJourneyTimes()
    centrePointAndJourneyTimes.second[request.people[0]] = 0
    when(midPointFinder.findMidpoint(any(List)))
        .thenReturn(centrePointAndJourneyTimes)

    //When
    meetUpFacade.doSearch(request)

    //Then there are no exceptions
  }

  @Test
  void testNoExceptionIfTravelTimeIsNull(){
    //Given
    def request = aRequestObj()
    when(poiFinderService.findPOIs(any(LatLong), any(Integer), any(POIType)))
        .thenReturn(null)

    def centrePointAndJourneyTimes = aCentrePointAndJourneyTimes()
    centrePointAndJourneyTimes.second[request.people[0]] = null
    when(midPointFinder.findMidpoint(any(List)))
        .thenReturn(centrePointAndJourneyTimes)

    //When
    meetUpFacade.doSearch(request)

    //Then there are no exceptions
  }
}