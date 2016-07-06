package com.meetupinthemiddle.displayUtils

import org.springframework.stereotype.Component

@Component
class TimeFormatter {
  int getHours(int minutes){
    minutes / 60;
  }

  int getMinutes(int minutes){
    minutes % 60;
  }
}
