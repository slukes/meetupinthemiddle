package com.meetupinthemiddle.services.midpoint.trainStation
import com.meetupinthemiddle.MeetupinthemiddleApplication
import com.meetupinthemiddle.model.LatLong
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

import static org.hamcrest.Matchers.hasSize
import static org.junit.Assert.assertThat

@ContextConfiguration(classes = MeetupinthemiddleApplication)
@RunWith(SpringRunner)
class TrainStationDaoTest {

  @Autowired
  TrainStationDao trainStationDao

  //This is technically more of an integration test,
  // however it makes sense to have it here to make sure the query is correct etc.

  @Test
  void testBoundary(){
    //LatLong for woking station
    def result = trainStationDao.findStations(new LatLong(51.31846765018483,-0.5569596839599814),
        new LatLong(51.31846765018483,-0.5569596839599814))
    assertThat(result, hasSize(1))
  }

  @Test
  void testArea(){
    //LatLong for Woking station and Guildford station expecting Woking, Guildford and Guildford London Road
    def result = trainStationDao.findStations(new LatLong(51.23696848697492,-0.5569596839599814),
        new LatLong(51.31846765018483,-0.5804250214773753))
    assertThat(result, hasSize(3))
  }

  @Test
  void testNoResults(){
    //Mainly just concerned there would be no exceptions ...
    def result = trainStationDao.findStations(new LatLong(0, 0), new LatLong(0,0))
    assertThat(result, hasSize(0))
  }
}