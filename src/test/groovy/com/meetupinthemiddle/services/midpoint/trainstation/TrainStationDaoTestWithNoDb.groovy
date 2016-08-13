package com.meetupinthemiddle.services.midpoint.trainStation
import com.meetupinthemiddle.model.LatLong
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

import static org.junit.Assert.assertEquals
import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString

@RunWith(MockitoJUnitRunner)
class TrainStationDaoTestWithNoDb {
  //No need to connect to the database for this one
  @Mock
  NamedParameterJdbcTemplate jdbcTemplate

  @InjectMocks
  TrainStationDao trainStationDao

  @Test
  void testMiddleBoundingBoxIsCalculatedCorrectly() {
    //Given
    def minLatLng = new LatLong(0, 0)
    def maxLatLng = new LatLong(4, 4)

    //When
    trainStationDao.findStations(minLatLng, maxLatLng)

    //Then
    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource)
    Mockito.verify(jdbcTemplate).query(anyString(), (MapSqlParameterSource) captor.capture(), (RowMapper) any(RowMapper))

    def params = captor.value
    assertEquals(params.getValue("HALF_MIN_LAT"), 1, 0)
    assertEquals(params.getValue("HALF_MIN_LONG"), 1, 0)
    assertEquals(params.getValue("HALF_MAX_LONG"), 3, 0)
    assertEquals(params.getValue("HALF_MAX_LONG"), 3, 0)
  }

  @Test
  void testQtrBoundingBoxIsCalculatedCorrectly() {
    //Given
    def minLatLng = new LatLong(0, 0)
    def maxLatLng = new LatLong(5, 5)

    //When
    trainStationDao.findStations(minLatLng, maxLatLng)

    //Then
    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource)
    Mockito.verify(jdbcTemplate).query(anyString(), (MapSqlParameterSource) captor.capture(), (RowMapper) any(RowMapper))

    def params = captor.value
    assertEquals(params.getValue("QTR_MIN_LAT"), 2, 0)
    assertEquals(params.getValue("QTR_MIN_LONG"), 2, 0)
    assertEquals(params.getValue("QTR_MAX_LONG"), 2, 0)
    assertEquals(params.getValue("QTR_MAX_LONG"), 2, 0)
  }
}
