package com.meetupinthemiddle.services.midpoint.trainStation;

import com.meetupinthemiddle.model.BoundingBox;
import com.meetupinthemiddle.model.LatLong;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.meetupinthemiddle.Stubs.randomLatLong;
import static com.meetupinthemiddle.services.midpoint.trainStation.TrainStationDao.TrainStation;
import static com.meetupinthemiddle.services.midpoint.trainStation.TrainStationDao.TrainStation.Size.*;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrainStationFinderTest {
  @Mock
  TrainStationDao trainStationDao;

  @InjectMocks
  TrainStationFinderService trainStationFinderService;

  @Ignore
  @Test
  public void testUnder150DoesntgetReduced() {
    //Given
    List<TrainStation> stations = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      stations.add(randomTrainStation(RandomUtils.nextBoolean(), RandomUtils.nextBoolean(), SMALL));
    }

    when(trainStationDao.findStations(any(BoundingBox.class)))
        .thenReturn(stations);

    //When
    List<LatLong> result = trainStationFinderService.doFind(new BoundingBox(new LatLong(1, 2), new LatLong(1, 2)));

    //Then
    assertThat(result, hasSize(20));
  }

  @Ignore
  @Test
  public void testMiddleHalf() {
    //Given
    List<TrainStation> stations = new ArrayList<>();
    for (int i = 0; i < 150; i++) {
      stations.add(randomTrainStation(true, false, SMALL));
    }

    for (int i = 0; i < 100; i++) {
      stations.add(randomTrainStation(false, false, SMALL));
    }

    when(trainStationDao.findStations(any(BoundingBox.class)))
        .thenReturn(stations);

    //When
    List<LatLong> result = trainStationFinderService.doFind(new BoundingBox(new LatLong(1, 2), new LatLong(1, 2)));

    //Then
    assertThat(result, hasSize(150));
  }

  @Ignore
  @Test
  public void testMiddleQtr() {
    //Given
    List<TrainStation> stations = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      stations.add(randomTrainStation(true, true, SMALL));
    }

    for (int i = 0; i < 150; i++) {
      stations.add(randomTrainStation(true, RandomUtils.nextBoolean(), SMALL));
    }

    for (int i = 0; i < 100; i++) {
      stations.add(randomTrainStation(false, RandomUtils.nextBoolean(), SMALL));
    }

    when(trainStationDao.findStations(any(BoundingBox.class)))
        .thenReturn(stations);

    //When
    List<LatLong> result = trainStationFinderService.doFind(new BoundingBox(new LatLong(1, 2), new LatLong(1, 2)));

    //Then
    assertThat(result, hasSize(20));
  }

  @Ignore
  @Test
  public void testSmall() {
    //Given
    List<TrainStation> stations = new ArrayList<>();

    for (int i = 0; i < 160; i++) {
      stations.add(randomTrainStation(true, true, SMALL));
    }

    for (int i = 1; i < 160; i++) {
      stations.add(randomTrainStation(true, true, LARGE));
    }

    for (int i = 0; i < 150; i++) {
      stations.add(randomTrainStation(true, RandomUtils.nextBoolean(), SMALL));
    }

    for (int i = 0; i < 100; i++) {
      stations.add(randomTrainStation(false, RandomUtils.nextBoolean(), SMALL));
    }

    when(trainStationDao.findStations(any(BoundingBox.class)))
        .thenReturn(stations);

    //When
    List<LatLong> result = trainStationFinderService.doFind(new BoundingBox(new LatLong(1, 2), new LatLong(1, 2)));

    //Then
    assertThat(result, hasSize(160));
  }

  @Ignore
  @Test
  public void testMedium() {
    //Given
    List<TrainStation> stations = new ArrayList<>();

    for (int i = 0; i < 160; i++) {
      stations.add(randomTrainStation(true, true, SMALL));
    }

    for (int i = 0; i < 160; i++) {
      stations.add(randomTrainStation(true, true, MEDIUM));
    }

    for (int i = 1; i < 160; i++) {
      stations.add(randomTrainStation(true, true, LARGE));
    }

    for (int i = 0; i < 150; i++) {
      stations.add(randomTrainStation(true, RandomUtils.nextBoolean(), SMALL));
    }

    for (int i = 0; i < 100; i++) {
      stations.add(randomTrainStation(false, RandomUtils.nextBoolean(), SMALL));
    }

    when(trainStationDao.findStations(any(BoundingBox.class)))
        .thenReturn(stations);

    //When
    List<LatLong> result = trainStationFinderService.doFind(new BoundingBox(new LatLong(1, 2), new LatLong(1, 2)));

    //Then
    assertThat(result, hasSize(160));
  }

  //Can't move to stubs because it is protected in the production code
  private TrainStation randomTrainStation(boolean middleHalf, boolean middleQtr, TrainStation.Size size) {
    TrainStation trainStation = new TrainStationDao.TrainStation();
    trainStation.setName(randomAlphabetic(10));
    trainStation.setLatLong((LatLong) randomLatLong());
    trainStation.setInMiddleHalf(middleHalf);
    trainStation.setInMiddleQuarter(middleQtr);
    trainStation.setSize(size);
    return trainStation;
  }
}