package com.meetupinthemiddle.model
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder

import javax.validation.Valid
import javax.validation.constraints.NotNull

@Builder
@EqualsAndHashCode
class Request {
  @Valid
  Person[] people;
  @NotNull
  POIType poiType;
}
