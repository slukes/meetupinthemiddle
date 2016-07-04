package com.meetupinthemiddle.services
import com.meetupinthemiddle.services.geocode.Geocoder
import com.meetupinthemiddle.services.midpoint.PointFinder
import com.meetupinthemiddle.services.midpoint.TrainStation.TrainStationDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.web.client.RestTemplate

import static org.mockito.Mockito.mock

@Configuration
@PropertySource("classpath:application.properties")
class TrainStationFinderTestConfig {
  @Bean
  PointFinder trainStationFinder() {
    new TrainStationDao()
  }

  @Bean
  RestTemplate restTemplate() {
    mock(RestTemplate)
  }

  @Bean
  Geocoder geocoder() {
    mock(Geocoder)
  }

  @Bean
  static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer()
  }
}
