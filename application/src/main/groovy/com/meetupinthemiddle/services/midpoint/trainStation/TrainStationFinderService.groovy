package com.meetupinthemiddle.services.midpoint.trainStation

import com.meetupinthemiddle.model.BoundingBox
import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.meetupinthemiddle.services.midpoint.trainStation.TrainStationDao.TrainStation.Size.SMALL

@Service
class TrainStationFinderService implements PointFinder {
  private static final int MAX_SIZE = 150

  @Autowired
  TrainStationDao trainStationDao

  @Override
  List<LatLong> doFind(BoundingBox boundingBox) {
    def stations = trainStationDao.findStations(boundingBox)
    reduceStationsToMaxSize(stations)
    stations.collect { it.latLong }
  }

  //Doesn't actually neccessarily get down to MAX_SIZE - but should be nearly there.
  private reduceStationsToMaxSize(List<TrainStationDao.TrainStation> stations) {
    if (stations.size() > MAX_SIZE) {
      stations.removeAll { !it.inMiddleHalf }
    }

    if (stations.size() > MAX_SIZE) {
      stations.removeAll { !it.inMiddleQuarter }
    }

    if (stations.size() > MAX_SIZE) {
      stations.removeAll { it.size == SMALL }
    }
  }
}