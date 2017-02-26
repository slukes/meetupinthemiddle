package com.meetupinthemiddle.services.midpoint.trainStation

import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@RunWith(MockitoJUnitRunner)
class TrainStationDaoTestWithNoDb {
  //No need to connect to the database for this one
  @Mock
  NamedParameterJdbcTemplate jdbcTemplate

  @InjectMocks
  TrainStationDao trainStationDao


}
