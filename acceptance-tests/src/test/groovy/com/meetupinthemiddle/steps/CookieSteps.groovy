package com.meetupinthemiddle.steps
import com.meetupinthemiddle.pages.MeetUpInTheMiddleBasePage

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

MeetUpInTheMiddleBasePage basePage = new MeetUpInTheMiddleBasePage()

Given(~/^I delete my cookie$/) {
  ->
  basePage.deleteCookie()
}

Then(~/^I can see the cookie banner$/) {
  ->
  basePage.checkForCookieBanner()
}

When(~/^I close the cookie banner$/) {
  ->
  basePage.closeCookieBanner()
}

Then(~/^A persistent cookie has been added$/) {
  ->
  basePage.checkForConsentCookie()
}

Then(~/^I do not see the cookie banner$/) {
  ->
  basePage.checkCookieBannerIsNotPresent()
}