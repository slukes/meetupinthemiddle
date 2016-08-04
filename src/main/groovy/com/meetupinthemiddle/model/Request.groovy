package com.meetupinthemiddle.model
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Builder
@EqualsAndHashCode
class Request {
  @Valid
  @Size(min = 2)
  List<Person> people;
  @NotNull
  POIType poiType;
}
