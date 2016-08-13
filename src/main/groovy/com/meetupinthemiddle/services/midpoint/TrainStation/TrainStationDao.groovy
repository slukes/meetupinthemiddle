package com.meetupinthemiddle.services.midpoint.trainStation

import com.meetupinthemiddle.model.LatLong
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

import static com.meetupinthemiddle.services.midpoint.trainStation.TrainStationDao.TrainStation.Size.*

@Repository
class TrainStationDao {
  private static final String SQL =
      """SELECT STATION_NAME, st_y(GEOM) AS LAT, st_x(GEOM) AS LONG, SIZE,
         GEOM && ST_MAKEENVELOPE(:HALF_MIN_LONG, :HALF_MIN_LAT, :HALF_MAX_LONG, :HALF_MAX_LAT) AS IN_MIDDLE_HALF,
         GEOM && ST_MAKEENVELOPE(:QTR_MIN_LONG, :QTR_MIN_LAT, :QTR_MAX_LONG, :QTR_MAX_LAT) AS IN_MIDDLE_QUARTER
         FROM stations
         WHERE GEOM && ST_MAKEENVELOPE(:MIN_LONG,:MIN_LAT,:MAX_LONG,:MAX_LAT)
      """


  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate

  List<TrainStation> findStations(LatLong minLatLong, LatLong maxLatLong) {
    MapSqlParameterSource params = getParamMap(minLatLong, maxLatLong)

    jdbcTemplate.query(SQL, params, trainStationRowMapper)
  }

  private getParamMap(LatLong minLatLong, LatLong maxLatLong) {
    def params = new MapSqlParameterSource()
    params.addValue("MIN_LAT", minLatLong.lat)
    params.addValue("MIN_LONG", minLatLong.lng)
    params.addValue("MAX_LAT", maxLatLong.lat)
    params.addValue("MAX_LONG", maxLatLong.lng)

    def qtrDiffInLat = (maxLatLong.lat - minLatLong.lat) / 4
    def qtrDiffInLong = (maxLatLong.lng - minLatLong.lng) / 4

    def halfMinLat = minLatLong.lat + qtrDiffInLat
    def halfMinLong = minLatLong.lng + qtrDiffInLong
    def halfMaxLat = maxLatLong.lat - qtrDiffInLat
    def halfMaxLong = maxLatLong.lng - qtrDiffInLong

    params.addValue("HALF_MIN_LAT", halfMinLat)
    params.addValue("HALF_MIN_LONG", halfMinLong)
    params.addValue("HALF_MAX_LAT", halfMaxLat)
    params.addValue("HALF_MAX_LONG", halfMaxLong)

    params.addValue("QTR_MIN_LAT", halfMinLat + qtrDiffInLat / 2)
    params.addValue("QTR_MIN_LONG", halfMinLong + qtrDiffInLong / 2)
    params.addValue("QTR_MAX_LAT", halfMaxLat - qtrDiffInLat / 2)
    params.addValue("QTR_MAX_LONG", halfMaxLong - qtrDiffInLong / 2)

    params
  }

  private RowMapper<TrainStation> trainStationRowMapper =
      {
        rs, i ->
          new TrainStation().with {
            name = rs.getString("STATION_NAME")
            latLong = new LatLong(rs.getDouble("LAT"), rs.getDouble("LONG"))
            inMiddleHalf = rs.getBoolean("IN_MIDDLE_HALF")
            inMiddleQuarter = rs.getBoolean("IN_MIDDLE_QUARTER")
            size = rs.getString("SIZE")
            it
          }
      }

  @EqualsAndHashCode
  @ToString
  protected static class TrainStation {
    enum Size {
      SMALL, MEDIUM, LARGE
    }

    //Not used right now but useful for debug
    String name
    LatLong latLong
    boolean inMiddleHalf
    boolean inMiddleQuarter
    Size size

    void setSize(sizeString) {
      switch (sizeString) {
        case "L":
          size = LARGE
          break
        case "S":
          size = SMALL
          break
        default:
          size = MEDIUM
      }
    }
  }
}