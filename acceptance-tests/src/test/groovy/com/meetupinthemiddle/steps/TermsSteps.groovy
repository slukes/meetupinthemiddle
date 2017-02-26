package com.meetupinthemiddle.steps
import com.meetupinthemiddle.pages.TermsPage

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

TermsPage termsPage = new TermsPage()

When(~/^I go to the terms page$/) {
  ->
  termsPage.open()
}
