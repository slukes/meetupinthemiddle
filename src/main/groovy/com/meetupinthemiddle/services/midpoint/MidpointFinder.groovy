package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.Person
import org.springframework.cache.annotation.Cacheable

interface MidpointFinder {
  @Cacheable("midpoints")
  Tuple2<CentrePoint, Map<Person, Long>> findMidpoint(List<Person> people);
}