package com.meetupinthemiddle

import com.google.maps.GeoApiContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
@PropertySource('classpath:application.properties')
public class MeetupinthemiddleApplication {

  static void main(String[] args) {
    SpringApplication.run MeetupinthemiddleApplication, args
  }

  @Bean
  GeoApiContext geoApiContext(@Value('${google.maps.api.key}') String apiKey) {
    return new GeoApiContext().setApiKey(apiKey)
  }
}
