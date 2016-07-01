package com.meetupinthemiddle.services.journeytimes

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person

interface JourneyTimesFinder {
  Map<LatLong, List<Integer>> getJourneyTimes(List<Person> people, List<LatLong> points)
}