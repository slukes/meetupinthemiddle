package com.meetupinthemiddle.model

import com.sun.mail.imap.IMAPFolder

import javax.mail.Folder
import javax.mail.internet.InternetAddress
import javax.mail.search.*
import java.util.concurrent.TimeUnit

import static javax.mail.Session.getDefaultInstance

class EmailFinderModel {
  IMAPFolder folder

  EmailFinderModel() {
    Properties props = new Properties()
    props.setProperty("mail.store.protocol", "imaps");

    def session = getDefaultInstance(props);

    def store = session.getStore("imaps");
    store.connect("imap.googlemail.com", "sdlukes@gmail.com", "Cheese123");

    folder = (IMAPFolder) store.getFolder("inbox");
  }

  boolean canFindEmail(emailSubject) {
    def found = false

    //Should be there immediately but keep looking for 2 minutes
    for (def i = 0; i < 12; i++) {
      folder.open(Folder.READ_ONLY)

      def dateTerm = new ReceivedDateTerm(ComparisonTerm.GT, oneMinuteAgo())
      def fromTerm = new FromTerm(new InternetAddress("sdlukes@gmail.com"))
      def subjectTerm = new SubjectTerm(emailSubject)

      def messages = folder.search(new AndTerm([dateTerm, fromTerm, subjectTerm] as SearchTerm[]))

      if (messages.size() > 0) {
        found = true
        break
      }

      folder.close(false)
      TimeUnit.SECONDS.sleep(10)
    }
    found
  }

  private Date oneMinuteAgo() {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(new Date())
    calendar.add(Calendar.MINUTE, -1)
    calendar.getTime()
  }
}