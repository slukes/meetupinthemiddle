package com.meetupinthemiddle.services.poi
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.POI
import com.meetupinthemiddle.model.POIType

interface POIFinder {
   POI[] findPOIs(LatLong location, int numberToFind, POIType type)
}