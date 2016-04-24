package com.meetupinthemiddle.services
import com.google.maps.DistanceMatrixApi
import com.google.maps.DistanceMatrixApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.model.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

import static org.mockito.AdditionalMatchers.aryEq
import static org.mockito.Matchers.any
import static org.mockito.Mockito.mock
import static org.powermock.api.mockito.PowerMockito.*

@RunWith(PowerMockRunner)
@PrepareForTest([DistanceMatrixApi, DistanceMatrixApiRequest])
class GoogleMapsJourneyTimesFinderTest {

  JourneyTimesFinder journeyTimesFinder = new GoogleMapsJourneyTimesFinder(mock(GeoApiContext))

  @Test
  void test() {
    DistanceMatrixApiRequest request = mock(DistanceMatrixApiRequest)
    when(request.destinations(Matchers.<LatLng>aryEq(destinations()))).thenReturn(request)
    when(request.origins(aryEq(destinations()))).thenReturn(request)
    when(request.await()).thenReturn(aDistanceMatrix())
    mockStatic(DistanceMatrixApi)
    when(DistanceMatrixApi.newRequest(any(GeoApiContext))).thenReturn(request)

    verifyStatic()
    DistanceMatrixApi.newRequest(any(GeoApiContext))
  }

  LatLng[] destinations(){
    LatLng[] arr = [new LatLng(1, 2), new LatLng(3, 4)]
    arr
  }

  LatLng[] origins(){
    LatLng[] arr = [new LatLng(1, 2), new LatLng(3, 4)]
    arr
  }

  def aDistanceMatrix() {
    def row1 = new DistanceMatrixRow().with {
      elements = [new DistanceMatrixElement(duration: new Duration(inSeconds: 10)),
                  new DistanceMatrixElement(duration: new Duration(inSeconds: 20))]
      it
    }

    def row2 = new DistanceMatrixRow().with {
      elements = [new DistanceMatrixElement(duration: new Duration(inSeconds: 30)),
                  new DistanceMatrixElement(duration: new Duration(inSeconds: 40))]
      it
    }

    new DistanceMatrix(null, null, [row1, row2] as DistanceMatrixRow[])
  }
}
