package com.meetupinthemiddle.steps
import com.meetupinthemiddle.pages.MeetUpInTheMiddleBasePage

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

MeetUpInTheMiddleBasePage basePage = new MeetUpInTheMiddleBasePage()

Given(~/^I disable JavaScript$/){
  ->
  basePage.toogleJs()
}

Given(~/^I reenable JavaScript$/){
  ->
  basePage.toogleJs()
}

Given(~/^I can see the no JavaScript banner$/){
  ->
  basePage.checkForNoJSBanner()
}

Then(~/^I can see social media buttons$/){
  ->
  basePage.checkForSocialMediaButtons()
}

When(~/^I click on the facebook button$/){
  ->
  basePage.clickFacebookButton()
}

When(~/^I click on the twitter button$/){
  ->
  basePage.clickTwitterButton()
}

Then(~/^I can see a facebook share window$/){
  ->
  basePage.checkForFacebookWindow()
}

Then(~/^I can see a twitter share window$/){
  ->
  basePage.checkForFacebookWindow()
}