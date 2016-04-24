package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person

interface JourneyTimesFinder {
  Map<Person, Map<LatLong, Integer>> getJourneyTimes(Person [] people, LatLong [] points)
}