package com.meetupinthemiddle.services.poi

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POI
import com.meetupinthemiddle.model.POIType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.meetupinthemiddle.model.POIType.MEETING

/**
 * This class exists to be a router to the different types of POI finder
 * so that the MeetUpFacade doesn't have to be concerned about which one to invoke.
 *
 * This is born out of the fact that we have two variations of the GooglePlacesPOIFinder,
 * which wasn't originally intended - it was hoped that Regus would be providing the MeetingRooms
 * but they haven't replied to my email.
 *
 * This is somewhere between being a facade and a factory (the objects are singletons) and as it
 * has evolved, this class implements the same interface as the two it is a router for.
 */
@Service
class PoiFinderRouter implements POIFinder {
  @Autowired
  private POIFinder googlePlacesPOIFinderUsingKeywords

  @Autowired
  private POIFinder googlePlacesPOIFinderUsingTypes

  @Override
  POI[] findPOIs(final LatLong location, final int numberToFind, final POIType type) {
    //For now just route everything which isn't a meeting room to googlePlacesPOIFinderUsingTypes
    //If more providers are added in future this should be made more advanced.
    def poiFinder

    switch (type) {
      case MEETING:
        poiFinder = googlePlacesPOIFinderUsingKeywords
        break
      default:
        poiFinder = googlePlacesPOIFinderUsingTypes
    }

    poiFinder.findPOIs(location, numberToFind, type)
  }
}