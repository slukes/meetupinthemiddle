package com.meetupinthemiddle.services

import com.google.maps.GeoApiContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
@PropertySource("classpath:application.properties")
class GoogleMapsJourneyTimesFinderTestConfig {
  @Bean
  JourneyTimesFinder journeyTimesFinder(){
    return new GoogleMapsJourneyTimesFinder()
  }

  @Bean
  GeoApiContext geoApiContext(@Value('${google.maps.api.key}') String apiKey) {
    return new GeoApiContext().setApiKey(apiKey)
  }

  @Bean
  static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer()
  }
}
