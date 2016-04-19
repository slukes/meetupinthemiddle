package com.meetupinthemiddle.services

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
  PointFinder trainStationFinder(){
    new TrainStationFinder()
  }

  @Bean
  RestTemplate restTemplate(){
    mock(RestTemplate)
  }

  @Bean
  static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer()
  }
}
