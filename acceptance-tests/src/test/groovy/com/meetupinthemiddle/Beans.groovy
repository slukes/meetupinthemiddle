package com.meetupinthemiddle

import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericXmlApplicationContext
import org.springframework.stereotype.Component

@Component
class Beans {

  private static ApplicationContext CTX

  static ApplicationContext getCtx() {
    if (CTX == null) {
      CTX = new GenericXmlApplicationContext("beans.xml")
    }
    CTX
  }
}
