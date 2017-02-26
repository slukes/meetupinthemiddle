package com.meetupinthemiddle.steps
import com.meetupinthemiddle.model.EmailFinderModel
import com.meetupinthemiddle.pages.ContactPage
import com.meetupinthemiddle.pages.HomePage
import org.apache.commons.lang3.RandomStringUtils

import static org.apache.commons.lang3.RandomStringUtils.random
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

ContactPage contactPage = new ContactPage()
HomePage homePage = new HomePage()
EmailFinderModel emailFinder = new EmailFinderModel()

def emailSubject

When(~/^I go to the contact page$/) {
  ->
  contactPage.open()
}

Then(~/^I can see an email form$/) {
  ->
  contactPage.checkForm()
}

Then(~/^the email doesn't send$/) {
  ->
  //If we are still on the contact page, it has not sent
  contactPage.checkPage("contact")
}

When(~/^I try to send an email with out entering a name$/) {
  ->
  contactPage.fillForm(null, randomEmail(), random(50), random(1000), false)
  contactPage.clickSend()
}

When(~/^I try to send an email with out entering an email address$/) {
  ->
  contactPage.fillForm(random(10), null, random(50), random(1000), false)
  contactPage.clickSend()
}

When(~/^I try to send an email with out entering a message$/) {
  ->
  contactPage.fillForm(random(10), randomEmail(), random(50), null, false)
  contactPage.clickSend()
}

When(~/^I try to send an email with an invalid email address$/) {
  ->
  contactPage.fillForm(random(10), random(20), random(50), random(1000), false)
  contactPage.clickSend()
}

When(~/^I send a random message$/) {
  ->

  def subject = random(50)
  contactPage.fillForm(random(10), randomEmail(), subject, random(1000), false)
  contactPage.clickSend()
  emailSubject = subject
}

When(~/^I send a random message with send copy selected$/) {
  ->
  def subject = RandomStringUtils.randomAlphabetic(10)
  contactPage.fillForm(random(10), "sdlukes@gmail.com", subject, random(1000), true)
  contactPage.clickSend()
  emailSubject = subject
}

Then(~/^I can see a successfully sent message$/) {
  ->
  homePage.checkForMailMessageSent()
}

Then(~/^I see an error message for (.+)$/) {
  field ->
    contactPage.checkForErrorMessageForField(field)
}

Given(~/^I disable form validation$/) {
  ->
  contactPage.disableFormValidation()
}

Then(~/^I have received a copy of the email$/) {
  ->
  assert emailFinder.canFindEmail(emailSubject)
}

private String randomEmail() {
  "${randomAlphabetic(10)}@${randomAlphabetic(10)}.com"
}