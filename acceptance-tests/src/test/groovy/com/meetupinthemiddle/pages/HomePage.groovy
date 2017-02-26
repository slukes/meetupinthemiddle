package com.meetupinthemiddle.pages
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.WebDriverWait

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf

class HomePage extends MeetUpInTheMiddleBasePage {

  private static final String NEW_PERSON_FORM_ID = "newPersonForm"
  private static final String NAME_ID = "newName"
  private static final String MODE_ID = "newMode"
  private static final String ADD_PERSON_ID = "add-person"
  private static final String POI_ID = "poi"
  private static final String NEW_FROM_ID = "newFrom"
  private static final String SUBMIT_BUTTON_ID = "submitButton"
  private static final String PERSON_TABLE_ROWS_XPATH = "//table[@id='peopleTable']/tbody/tr"
  public static final String TOOLTIP_ID = "tooltip"

   HomePage() {
    super("http://localhost:8085/?view=test")
  }

  void checkAddPersonFormElements() {
    //Exception gets thrown if it can't find it
    def form = webDriver.findElement(By.id(NEW_PERSON_FORM_ID))
    form.findElement(By.id(NAME_ID))
    form.findElement(By.id(NEW_FROM_ID))
    verifyModes(form.findElement(By.id(MODE_ID)))
    form.findElement(By.id(ADD_PERSON_ID))
  }

  void checkPoiSelector() {
    assert webDriver.findElement(By.id(POI_ID))
        .findElements(By.tagName("option"))
        .collect { it.getAttribute("value") }
        .containsAll("PUB", "RESTAURANT", "MEETING")
  }

  void enterFrom(location) {
    def from = webDriver.findElement(By.id(NEW_FROM_ID))
    from.sendKeys(location)
  }

  void enterName(name) {
    def nameBox = webDriver.findElement(By.id(NAME_ID))
    nameBox.click()
    nameBox.sendKeys(name)
  }

  void selectMode(mode) {
    def modeBox = webDriver.findElement(By.id(MODE_ID))
    modeBox.click()

    modeBox.findElements(By.tagName("option")).find {
      it.getAttribute("value").toUpperCase().contains(mode.toUpperCase())
    }.click()

    modeBox.sendKeys(Keys.ESCAPE)
  }


  void clickAddPerson() {
    try {
      webDriver.findElement(By.id(ADD_PERSON_ID)).click()
    } catch (WebDriverException e) {
      println e
      if (e.message.contains("Element is not clickable at point")) {
        webDriver.findElement(By.id(NEW_FROM_ID)).sendKeys(Keys.ESCAPE)
        webDriver.findElement(By.id(ADD_PERSON_ID)).click()
      } else {
        println e
        throw e
      }
    }
  }

  void addPerson(name, location) {
    enterName(name)
    enterFrom(location)
    clickAddPerson()
  }

  void addPerson(name, location, mode) {
    enterName(name)
    enterFrom(location)
    selectMode(mode)
    clickAddPerson()
  }

  void checkSubmitButton() {
    webDriver.findElement(By.id(SUBMIT_BUTTON_ID))
  }

  void checkAddPersonButtonIs(enabled) {
    def button = webDriver.findElement(By.id(ADD_PERSON_ID))
    checkAButtonIs(button, enabled)

    //Check clicking doesn't add a row
    if (!enabled) {
      def beforeTableLength = personTableLength()
      clickAddPerson()
      def afterTableLength = personTableLength()
      assert beforeTableLength == afterTableLength
    }
  }

  void checkSubmitButtonIs(boolean enabled) {
    def button = webDriver.findElement(By.id(SUBMIT_BUTTON_ID))
    checkAButtonIs(button, enabled)
  }

  private checkAButtonIs(button, enabled) {
    //Check for bootstrap disabled class or disabled attribute
    // (they have the same net affect for different tpyes of element)
    def hasDisabledClass = button.getAttribute("class").contains("disabled")
    def hasDisabledAttribute = button.getAttribute("disabled") != null
    assert hasDisabledClass != enabled || hasDisabledAttribute != enabled

    //Check for correct mouse pointer
    def cursor = button.getCssValue("cursor")

    if (enabled) {
      assert cursor == "pointer"
    } else {
      assert cursor == "not-allowed"
    }
  }

  void checkForPersonRowFor(name, location) {
    assert findPersonInTable(name, location) != null
  }

  void checkPersonRowNotPresentFor(name, location) {
    assert findPersonInTable(name, location) == null
  }

  void removePerson(name, location) {
    def button = findRemoveButtonForPerson(name, location)
    button.click()
  }

  void checkNameFieldHasFocus() {
    assert webDriver.findElement(By.id(NAME_ID))
        .equals(webDriver.switchTo().activeElement())
  }

  void checkFormIsCleared() {
    assert webDriver.findElement(By.id(NAME_ID)).getText() == ""
    assert webDriver.findElement(By.id(NEW_FROM_ID)).getText() == ""
  }

  void checkForHelpIcon() {
    webDriver.findElement(By.id(TOOLTIP_ID))
  }

  void hoverOverHelpIcon() {
    def icon = webDriver.findElement(By.id(TOOLTIP_ID))
    new Actions(webDriver).moveToElement(icon)
        .build().perform()
  }

  void checkForToolTip() {
    assert webDriver.findElement(By.className("tooltip")).isDisplayed()
  }

  void clickSearch() {
    webDriver.findElement(By.id(SUBMIT_BUTTON_ID)).click()
  }

  void checkButtonSaysLoading() {
    assert webDriver.findElement(By.id(SUBMIT_BUTTON_ID)).text.equalsIgnoreCase("loading...")
  }

  void checkForMap() {
    //If there are elements in the map section then a map must be present
    assert webDriver.findElements(By.xpath("//section[@id='map']/child::*")).size() > 0
  }

  void checkForPins(list) {
    list.each {
      //Slightly primitive but works.
      // Any more specific and it would break if Google changed the structure.
      assert webDriver.findElement(By.xpath("//section[@id='map']//div[text() = '${it.charAt(0)}']"))
          .isDisplayed()
    }
  }

  void checkForErrorContainingText(text) {
    assert webDriver
        .findElement(By.xpath("//section[@id='error-section']/span[@class='error-message' and text()[contains(.,'$text')]]"))
        .isDisplayed()
  }

  void checkForMailMessageSent() {
    assert webDriver.findElement(By.id("mail-bar")).text.toLowerCase().contains("sent")
  }

  void checkForSuggestion(suggestion) {
    //presenceOfElementLocated does not check for visibility so is no good here.
    def autocompleteContainer = webDriver.findElement(By.className("pac-container"))

    new WebDriverWait(webDriver, 10).until(visibilityOf(autocompleteContainer))

    suggestion = suggestion.toLowerCase()

    def locations =
        webDriver.findElements(By.className("pac-item")).collect {
          //Sublocalities in seperate spans get merged when we get the text e.g.
          // Reading StationStation Hill, Reading.  or 7lynmouth road, reading
          // Insert a space to over come this And convert to lower case

          it.text.replaceAll(/([A-Z0-9][a-z]*)([A-Z][a-z]+)/, /$1 $2/)
              .toLowerCase()
        }

    assert locations.find { it.contains(suggestion) }
  }


  private findRemoveButtonForPerson(name, location) {
    def personRow = findPersonInTable(name, location)
    personRow.findElements(By.tagName("a"))
        .find {
      //Only currently one anchor in each row but lets future proof
      it.getAttribute("class").contains("removePerson")
    }
  }

  private findPersonInTable(name, location) {
    webDriver.findElements(By.xpath(PERSON_TABLE_ROWS_XPATH)).find {
      row ->
        def cells = row.findElements(By.tagName("td"))
        cells.get(1).getText() == name
        //Auto complete can expand what we enter.  I.e. Bracknell becomes Bracknell Forest
        cells.get(2).getText().contains(location)
    }
  }

  private personTableLength() {
    webDriver.findElements(By.xpath(PERSON_TABLE_ROWS_XPATH)).size()
  }

  private verifyModes(modeSelectBox) {
    assert modeSelectBox
        .findElements(By.tagName("option"))
        .collect { it.text }
        .containsAll("Driving", "Public Transport")
  }
}