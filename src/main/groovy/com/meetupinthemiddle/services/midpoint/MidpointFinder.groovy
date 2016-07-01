package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.Person

interface MidpointFinder {
  CentrePoint findMidpoint(List<Person> people);
}