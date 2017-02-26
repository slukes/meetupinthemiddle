package com.meetupinthemiddle.steps
import com.meetupinthemiddle.pages.MeetUpInTheMiddleBasePage

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

MeetUpInTheMiddleBasePage basePage = new MeetUpInTheMiddleBasePage()

Given(~/^I refresh the page$/) {
  ->
  basePage.refresh()
}

When(~/^I click on the (terms|contact) link$/) {
  page ->
    basePage.clickLink(page)
}

When(~/^I click on the logo$/) {
  ->
  basePage.clickLogo()
}

Then(~/^I am on the (terms|contact|home) page$/) {
  page ->
    basePage.checkPage(page)
}