package com.meetupinthemiddle.Services;

import com.meetupinthemiddle.Model.LatLong;
import com.meetupinthemiddle.Model.POI;
import com.meetupinthemiddle.Model.POIType;

import java.util.List;

public interface POIFinderService {
  public List<POI> findPOIs(LatLong location, int numberToFind, POIType type) throws Exception;
}
