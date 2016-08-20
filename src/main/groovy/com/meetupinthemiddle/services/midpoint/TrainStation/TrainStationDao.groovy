package com.meetupinthemiddle.services.midpoint.trainStation
import com.meetupinthemiddle.model.BoundingBox
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

  List<TrainStation> findStations(BoundingBox boundingBox) {
    MapSqlParameterSource params = getParamMap(boundingBox)

    jdbcTemplate.query(SQL, params, trainStationRowMapper)
  }

  private getParamMap(BoundingBox boundingBox) {
    def minLatLong = boundingBox.minLatLong
    def maxLatLong = boundingBox.maxLatLong

    def params = new MapSqlParameterSource()
    params.addValue("MIN_LAT", minLatLong.lat)
    params.addValue("MIN_LONG", minLatLong.lng)
    params.addValue("MAX_LAT", maxLatLong.lat)
    params.addValue("MAX_LONG", maxLatLong.lng)

    def halfBoundingBox = boundingBox.scale(2)

    params.addValue("HALF_MIN_LAT", halfBoundingBox.minLatLong.lat)
    params.addValue("HALF_MIN_LONG", halfBoundingBox.minLatLong.lng)
    params.addValue("HALF_MAX_LAT", halfBoundingBox.maxLatLong.lat)
    params.addValue("HALF_MAX_LONG", halfBoundingBox.maxLatLong.lng)

    def qtrBoundingBox = boundingBox.scale(4)

    params.addValue("QTR_MIN_LAT", qtrBoundingBox.minLatLong.lat)
    params.addValue("QTR_MIN_LONG", qtrBoundingBox.minLatLong.lng)
    params.addValue("QTR_MAX_LAT", qtrBoundingBox.maxLatLong.lat)
    params.addValue("QTR_MAX_LONG", qtrBoundingBox.maxLatLong.lng)

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