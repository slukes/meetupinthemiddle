package com.meetupinthemiddle.services.journeytimes

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import org.springframework.cache.annotation.Cacheable

interface JourneyTimesFinder {
  @Cacheable("journey-times")
  Map<LatLong, List<Integer>> getJourneyTimes(List<Person> people, List<LatLong> points)
}