package com.meetupinthemiddle.services.midpoint.TrainStation

import com.meetupinthemiddle.model.LatLong
import com.meetupinthemiddle.services.midpoint.PointFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class TrainStationDao implements PointFinder {
  private static final String SQL =
      """SELECT st_y(GEOM) AS LAT, st_x(GEOM) AS LONG
         FROM stations
         WHERE GEOM && ST_MAKEENVELOPE(:MIN_LONG,:MIN_LAT,:MAX_LONG,:MAX_LAT)"""


  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate

  @Override
  List<LatLong> doFind(LatLong minLatLong, LatLong maxLatLong) {
    def params = new MapSqlParameterSource()
    params.addValue("MIN_LAT", minLatLong.lat)
    params.addValue("MIN_LONG", minLatLong.lng)
    params.addValue("MAX_LAT", maxLatLong.lat)
    params.addValue("MAX_LONG", maxLatLong.lng)

    jdbcTemplate.query(SQL, params,
        (RowMapper) { rs, i -> new LatLong(rs.getDouble("LAT"), rs.getDouble("LONG")) })
  }
}