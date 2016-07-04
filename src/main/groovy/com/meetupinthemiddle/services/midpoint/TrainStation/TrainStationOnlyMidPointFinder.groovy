package com.meetupinthemiddle.services.midpoint.TrainStation
import com.meetupinthemiddle.model.CentrePoint
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.model.Person
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.journeytimes.JourneyTimesFinder
import com.meetupinthemiddle.services.midpoint.AbstractMidpointFinder
import org.apache.commons.lang.time.StopWatch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
//Iteration that will be replaced.
@Service
class TrainStationOnlyMidPointFinder extends AbstractMidpointFinder {
  @Autowired
  TrainStationDao trainStationDao

  @Autowired
  JourneyTimesFinder journeyTimesFinder

  @Autowired
  Geocoder geocoder

  @Override
  Tuple2<CentrePoint, Map<Person, Long>> findMidpoint(final List<Person> people) {
    def latlongs = getMinAndMaxLatLng(people)
    def stopwatch = new StopWatch()
    stopwatch.start()
    def stations = trainStationDao.findStationsInBox(latlongs.first, latlongs.second)
    stopwatch.stop()
    println stopwatch.time
    def journeyTimes = journeyTimesFinder.getJourneyTimes(people, stations as List<LatLong>)
    def centre = minSum(people, journeyTimes)
    def townAndPostCode = geocoder.reverseGeocode(centre.getFirst())
    buildResponse(centre, townAndPostCode, people)
  }
}