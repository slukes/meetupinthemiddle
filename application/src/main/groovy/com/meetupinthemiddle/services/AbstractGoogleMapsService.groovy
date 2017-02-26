package com.meetupinthemiddle.services

import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.errors.OverDailyLimitException
import com.meetupinthemiddle.exceptions.OverQuotaException
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import static org.slf4j.LoggerFactory.getLogger

/**
 * Class added in order to mop up the requirement catch certain types of Google exception and rethrow as other types.
 * @param < R >     the type of the request being submitted e.g. GeocodingApiRequest
 * @param < T >     the type returned by R::await e.g. DistanceMatrix
 */
abstract class AbstractGoogleMapsService<T, R extends PendingResult<T>> {
  @Autowired
  protected GeoApiContext context

  private static final Logger LOGGER = getLogger(this.getClass())

  protected T doCall(R request) throws OverDailyLimitException{
    try {
      request.await()
    } catch (OverDailyLimitException e) {
      LOGGER.error("Over daily quota for Google APIs", e)
      throw new OverQuotaException()
    }
  }
}
