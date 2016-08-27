package com.meetupinthemiddle.model

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
class PreviousSearches {
  List<Request> requests = new ArrayList<>()
}
