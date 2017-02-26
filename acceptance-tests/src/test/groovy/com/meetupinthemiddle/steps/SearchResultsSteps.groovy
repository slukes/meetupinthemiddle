package com.meetupinthemiddle.steps
import com.meetupinthemiddle.pages.SearchResultsPage

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

SearchResultsPage serp = new SearchResultsPage()
def midpoints = []

When(~/^I wait for the results to load$/) {
  ->
  serp.waitForLoad()
}

When(~/^I make a note of the midpoint$/) { ->
  midpoints << serp.getMidpoint()
}

Then(~/^the midpoints are different$/) {
  ->
  midpoints.each {
    assert it == midpoints[0]
  }
}

Then(~/^I can see the postcode and town on the page$/) {
  ->
  serp.checkForPostcodeAndTown()
}

Then(~/^I can see a table with the name of each POI$/) {
  ->
  serp.checkForPoiTable()
}

When(~/^I click on POI (\d+) in the table$/) {
  int poi ->
    serp.clickPoi(poi)
}

When (~/^I click on the map pin labeled (.)$/){
  label ->
    serp.clickMarkerForPoi(label)
}

Then(~/^I see an info box$/) { ->
  serp.checkInfoBox()
}