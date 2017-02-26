package com.meetupinthemiddle.pages
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.support.ui.WebDriverWait

import static java.lang.Integer.parseInt
import static java.util.concurrent.TimeUnit.MINUTES
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated

class SearchResultsPage extends MeetUpInTheMiddleBasePage {
  private int selectedPoi

   SearchResultsPage() {
    //This page is really a fake page which is part of the home page.
    super("http://localhost:8085/?view=test")
  }

  String getMidpoint() {
    webDriver.findElement(By.id("mid-point")).text
  }

  void waitForLoad() {
    new WebDriverWait(webDriver, MINUTES.toSeconds(1)).until(presenceOfElementLocated(By.id("midpoint")))
  }

  void checkForPostcodeAndTown() {
    //RG1, Reading
    //W1G, Marylebone
    //GU22, Woking
    def midpoint = getMidpoint()
    assert midpoint.matches(/^[A-Z]{1,2}\d{1,2}[A-Z]?(?:\s\d[A-Z]{2}), [A-Z].+$/)
  }

  void checkForPoiTable() {
    def rows = webDriver.findElements(By.xpath("//table[@id='poiTable']/tbody/tr//td[3]"))
    assert rows.size() == 5
    rows.each {
      assert it.text != null
    }
  }

  void clickPoi(int poi) {
    webDriver.findElement(By.xpath("//table[@id='poiTable']/tbody/tr[$poi]")).click()
    selectedPoi = poi
  }

  void checkInfoBox() {
    def infoWindows = webDriver.findElements(By.className("infoWindow"))
    assert infoWindows.size() == 1 //Only one should be displayed at a time, GM removes from DOM
    //Check its the correct one
    def poiName = webDriver.findElement(By.xpath("//table[@id='poiTable']/tbody/tr[$selectedPoi]/td[3]")).text
    assert infoWindows[0].findElement(By.className("infoWindowHeading")).text == poiName
  }

  void clickMarkerForPoi(poi) {
    //TODO this is very fragile
    def poiNumber = parseInt(poi)
    ((JavascriptExecutor) webDriver).executeScript("map.setZoom(16)")
    webDriver.findElement(By.cssSelector("#gmimap${poiNumber - 1} > area")).click()
    selectedPoi = poiNumber
  }
}