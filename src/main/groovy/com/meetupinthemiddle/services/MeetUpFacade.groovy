package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.model.Response
import org.springframework.cache.annotation.Cacheable

interface MeetUpFacade {
  @Cacheable("response")
  Response doSearch(Request request)
}