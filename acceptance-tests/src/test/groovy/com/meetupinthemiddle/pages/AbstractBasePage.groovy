package com.meetupinthemiddle.pages

import org.openqa.selenium.WebDriver

import static com.meetupinthemiddle.Beans.ctx

abstract class AbstractBasePage {
  WebDriver webDriver

  String url

  protected AbstractBasePage(String url) {
    this.webDriver = getCtx().getBean(WebDriver)
    this.url = url
  }

  void open(){
    webDriver.get(url)
  }

  void refresh(){
    webDriver.navigate().refresh()
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize()
    webDriver.close()
  }
}
