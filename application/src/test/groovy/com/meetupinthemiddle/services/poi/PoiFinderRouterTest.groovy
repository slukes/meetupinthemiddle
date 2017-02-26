package com.meetupinthemiddle.services.poi
import com.meetupinthemiddle.model.LatLong
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static com.meetupinthemiddle.Stubs.randomLatLong
import static com.meetupinthemiddle.model.POIType.MEETING
import static com.meetupinthemiddle.model.POIType.PUB
import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions

@RunWith(MockitoJUnitRunner)
class PoiFinderRouterTest {
  @Mock
  POIFinder googlePlacesPOIFinderUsingKeyword

  @Mock
  POIFinder googlePlacesPOIFinderUsingTypes

  @InjectMocks
  PoiFinderRouter router

  @Test
  void testMeetingRoomsGoToKeyword(){
    //When
    router.findPOIs(randomLatLong(), 5, MEETING)

    //Then
    verifyZeroInteractions(googlePlacesPOIFinderUsingTypes)
    verify(googlePlacesPOIFinderUsingKeyword).findPOIs(any(LatLong.class),eq(5), eq(MEETING))
  }

  @Test
  void testPubsGoToTypes(){
    //When
    router.findPOIs(randomLatLong(), 5, PUB)

    //Then
    verifyZeroInteractions(googlePlacesPOIFinderUsingKeyword)
    verify(googlePlacesPOIFinderUsingTypes).findPOIs(any(LatLong.class),eq(5), eq(PUB))
  }
}
