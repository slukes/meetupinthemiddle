package com.meetupinthemiddle.services.midpoint

import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import groovy.transform.PackageScope

@PackageScope
/**
 * This may go away in the future but exists for now as a means of developing
 * a new way of picking the centre point with an easy way of putting the old one back!
 */
interface CentrePicker {
  //Returning both things here is possibly a violation of SOLID (S)
  //However, its a waste to have found both peices of data and then recalculate else where.
  Tuple2<CentrePoint, Map<Person, Long>> pickBestPoint(List<Person> people, Map<LatLong, List<Integer>> allTimes)
}