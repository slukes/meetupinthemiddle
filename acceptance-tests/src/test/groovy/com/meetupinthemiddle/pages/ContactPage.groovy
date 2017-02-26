package com.meetupinthemiddle.pages
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor

class ContactPage extends MeetUpInTheMiddleBasePage {

   ContactPage() {
    super("http://localhost:8085/contact")
  }

  void checkForm() {
    assert webDriver.findElement(By.id("name")) != null
    assert webDriver.findElement(By.id("email")) != null
    assert webDriver.findElement(By.id("subject")) != null
    assert webDriver.findElement(By.id("message")) != null
    assert webDriver.findElement(By.id("sendCopy")) != null
    webDriver.findElement(By.tagName("button")) != null
  }

  void fillForm(String name, String email, String subject, String message, boolean sendCopy) {
    if (name) {
      webDriver.findElement(By.id("name")).sendKeys(name)
    }

    if (email) {
      webDriver.findElement(By.id("email")).sendKeys(email)
    }

    if (subject) {
      webDriver.findElement(By.id("subject")).sendKeys(subject)
    }

    if (message) {
      webDriver.findElement(By.id("message")).sendKeys(message)
    }

    if (sendCopy) {
      webDriver.findElement(By.id("sendCopy")).click()
    }
  }

  void clickSend() {
    webDriver.findElement(By.tagName("button")).click()
  }

  void checkForErrorMessageForField(String field) {
    assert webDriver.findElements(By.className("error-message")).find{
      it.text.toLowerCase().contains(field)
    } != null
  }

  void disableFormValidation() {
    //Ref: http://novalidate.com/
    ((JavascriptExecutor) webDriver)
        .executeScript("""for(var f=document.forms,i=f.length;i--;)f[i].setAttribute("novalidate",i)""")
  }
}
