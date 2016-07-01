package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.Person

interface MidpointFinder {
  Tuple2<CentrePoint, Map<Person, Long>> findMidpoint(List<Person> people);
}