package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POI
import com.meetupinthemiddle.model.POIType

interface POIFinderService {
   POI[] findPOIs(LatLong location, int numberToFind, POIType type)
}
