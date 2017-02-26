package com.meetupinthemiddle.steps
import com.meetupinthemiddle.pages.HomePage

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)


HomePage homePage = new HomePage()

Given(~/^I go to the home page$/) {
  ->
  homePage.open()
}

Then(~/^I can see the add person form elements$/) {
  ->
  homePage.checkAddPersonFormElements()
}

Then(~/^I can see the POI selector form element$/) {
  ->
  homePage.checkPoiSelector()
}

Then(~/^I can see the search button$/) {
  ->
  homePage.checkSubmitButton()
}

When(~/^I click on search$/) {
  ->
  homePage.clickSearch()
}

Then(~/^the search button says loading$/) {
  ->
  homePage.checkButtonSaysLoading()
}

When(~/^I enter a location of (.+)$/) {
  location ->
    homePage.enterFrom(location)
}

When(~/^I enter a name of (.+)$/) {
  name ->
    homePage.enterName(name)
}

When(~/^I add (.+) from (.+)$/) {
  name, location ->
    homePage.addPerson(name, location)
}

Then(~/^the add person button is (enabled|disabled)$/) {
  state ->
    def enabled = "enabled".equals(state)
    homePage.checkAddPersonButtonIs(enabled)
}

When(~/^I click on the add person button$/) {
  ->
  homePage.clickAddPerson()
}

Then(~/^a new row is added for (.+) from (.+)$/) {
  name, location ->
    homePage.checkForPersonRowFor(name, location)
}

When(~/^I click on the remove person button for (.+) from (.+)/) {
  name, location ->
    homePage.removePerson(name, location)
}

Then(~/^I can see (.+) from (.+) is not in the table$/) {
  name, location ->
    homePage.checkPersonRowNotPresentFor(name, location)
}

Then(~/^the search button is (enabled|disabled)$/) {
  state ->
    def enabled = "enabled".equals(state)
    homePage.checkSubmitButtonIs(enabled)
}

Then(~/^the cursor is focused on the name field$/) {
  ->
  homePage.checkNameFieldHasFocus()
}

Then(~/the form has been cleared/) {
  ->
  homePage.checkFormIsCleared()
}

Then(~/^I can see a \?$/) { ->
  homePage.checkForHelpIcon()
}

When(~/^I hover over the \?$/) { ->
  homePage.hoverOverHelpIcon()
}

Then(~/^I can see a tooltip$/) { ->
  homePage.checkForToolTip()
}

Given(~/^I perform a search for$/) {
  people ->
    homePage.open()
    people.gherkinRows.each {
      homePage.addPerson(it.cells[0], it.cells[1], it.cells[2])
    }
    homePage.clickSearch()
}

Then(~/^the location is rejected because it is outside of the UK$/) {
  ->
  homePage.checkForErrorContainingText("Only locations in the UK are supported")
}

Then(~/^I can see a map$/) {
  ->
  homePage.checkForMap()
}

Then(~/^I can see pins on the map for (.+)$/) {
  String people ->
    //TODO - can cucumber give this as a list / array?
    homePage.checkForPins(people.split(/,\s?/))
}

Then(~/^I see a suggestion of (.+)$/) {
  suggestion ->
    homePage.checkForSuggestion(suggestion)
}