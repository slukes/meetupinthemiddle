package com.meetupinthemiddle.services

import com.meetupinthemiddle.model.Request
import com.meetupinthemiddle.model.Response

interface MeetUpFacade {
  Response doSearch(Request request)
}