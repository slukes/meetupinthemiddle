package com.meetupinthemiddle
import com.google.maps.GeoApiContext
import com.meetupinthemiddle.interceptors.isTestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import static java.util.concurrent.TimeUnit.SECONDS

@SpringBootApplication
@EnableCaching
@PropertySource('classpath:application.properties')
public class MeetupinthemiddleApplication {

  static void main(String[] args) {
    SpringApplication.run MeetupinthemiddleApplication, args
  }

  @Bean
  GeoApiContext geoApiContext(@Value('${google.maps.api.key}') String apiKey) {
    new GeoApiContext().setApiKey(apiKey)
        .setFailFastForDailyLimit(true)
        .setRetryTimeout(20, SECONDS) //Hope to not be hitting this - but Google's rate limit means we might.
  }

  //The app is deployed to tomcat in AWS so we need this
  @Configuration
  class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
      application.sources(MeetupinthemiddleApplication)
    }
  }

  @Configuration
  class WebConfig extends WebMvcConfigurerAdapter {
    @Override
    void addInterceptors(final InterceptorRegistry registry) {
      registry.addInterceptor(new isTestInterceptor())
    }
  }
}
