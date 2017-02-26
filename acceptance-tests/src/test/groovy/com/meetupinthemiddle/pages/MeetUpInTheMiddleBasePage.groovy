package com.meetupinthemiddle.pages
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions

class MeetUpInTheMiddleBasePage extends AbstractBasePage {

  private static final String COOKIE_BAR_ID = "cookie-bar"
  private static final String CLOSE_COOKIE_BAR_ID = "close-cookie-bar"

   MeetUpInTheMiddleBasePage(String url) {
    super(url)
  }

  void checkForCookieBanner() {
    def bar = webDriver.findElement(By.id(COOKIE_BAR_ID))
    assert bar.getCssValue("display") != "none"
  }

  void checkForNoJSBanner() {
    assert webDriver.findElement(By.tagName("noscript"))
        .findElement(By.className("top-bar"))
        .displayed
  }

  void closeCookieBanner() {
    webDriver.findElement(By.id(CLOSE_COOKIE_BAR_ID)).click()
  }

  void checkCookieBannerIsNotPresent() {
    def bar = webDriver.findElement(By.id(COOKIE_BAR_ID))
    assert bar.getCssValue("display") == "none"
  }

  void checkForConsentCookie() {
    assert webDriver.manage().cookies.find {
      it.domain == new URI(webDriver.currentUrl).host
      it.name == "cookie-consent"
    } != null
  }

  void deleteCookie() {
    webDriver.manage().deleteCookieNamed("cookie-consent")
  }

  void clickLink(page) {
    webDriver.findElements(By.xpath("//footer/a"))
        .find { it.getText().toLowerCase().contains(page.toLowerCase()) }
        .click()
  }

  void clickLogo() {
    try {
      closeCookieBanner()
    } catch (Exception e){
      //It wasn't there - no problem
    }

    webDriver.findElement(By.id("logo"))
        .click()
  }

  void checkPage(page) {
    //On a redirect, sometime a jsession id gets appended.
    def path = webDriver.currentUrl
    path = path.substring(path.lastIndexOf("/"), path.length())

    def idxOfSemiColon = path.indexOf(";")

    if (idxOfSemiColon != -1) {
      path = path.substring(0, idxOfSemiColon)
    }

    if (page == "home") {
      assert path == "/"
    } else {
      //IDE warning here is fine.  You can compare String == GString, but you cannot String.equals(GString)
      assert path == "/$page"
    }
  }

  void checkForSocialMediaButtons() {
    //Facebook
    getFacebookButton()

    webDriver.switchTo().defaultContent()

    //Twitter
    getTwitterButton()

    webDriver.switchTo().defaultContent()
  }

  void clickFacebookButton() {
    getFacebookButton().click()
    webDriver.switchTo().defaultContent()
  }

  void clickTwitterButton() {
    getTwitterButton().click()
    webDriver.switchTo().defaultContent()
  }

  void checkForFacebookWindow() {
    //This can't be more clever than this since it would require a login
    assert windowWithUrlContaining("facebook") != null
  }

  void checkForTwitterWindow() {
    //This can't be more clever than this since it would require a login
    assert windowWithUrlContaining("twitter") != null
  }

  private String windowWithUrlContaining(urlPart) {
    webDriver.getWindowHandles().collect {
      webDriver.switchTo().window(it)
      webDriver.currentUrl
    }.find {
      it.contains(urlPart)
    }

    webDriver.switchTo().defaultContent()
  }

  //Must call  webDriver.switchTo().defaultContent() after using the result of this
  private WebElement getTwitterButton() {
    def twitterFrame = webDriver.findElement(By.xpath("//iframe[contains(@id,'twitter')]")).getAttribute("id")
    webDriver.switchTo().frame(twitterFrame)
    webDriver.findElement(By.tagName("a"))
  }

  //Must call  webDriver.switchTo().defaultContent() after using the result of this
  private WebElement getFacebookButton() {
    def facebookFrame = webDriver.findElement(By.xpath("//iframe[contains(@title,'fb:share_button')]")).getAttribute("name")
    webDriver.switchTo().frame(facebookFrame)
    webDriver.findElement(By.tagName("button"))
  }

  //TODO - do something to get rid of this horrible method!
  void toogleJs() {
    webDriver.get("about:config");
    try {
      webDriver.findElement(By.tagName("window")).click()
    } catch (Exception e) {
      //No problem
    }
    webDriver.findElement(By.tagName("button")).click()
    def act = new Actions(webDriver)
    act.sendKeys("javascript.enabled").pause(1000).perform()
    act.sendKeys(Keys.TAB).perform()
    act.sendKeys(Keys.RETURN).perform()
  }


}