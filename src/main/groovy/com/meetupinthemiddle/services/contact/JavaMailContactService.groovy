package com.meetupinthemiddle.services.contact
import com.meetupinthemiddle.model.ContactFormBean
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

import javax.mail.internet.InternetAddress

@Service
class JavaMailContactService implements ContactService{
  private static final Logger LOGGER = LoggerFactory.getLogger(JavaMailContactService)
  @Autowired
  private JavaMailSender javaMailSender

  @Value('${contact.email.to}')
  private String toEmailAddress

  @Value('${contact.email.noreply}')
  private String noreplyEmail

  //TODO - thymeleaf email template?
  boolean sendMessage(ContactFormBean formBean) {
    try {
      def messages = []

      def emailMessage = new SimpleMailMessage()
      emailMessage.with {
        to = toEmailAddress
        from = noreplyEmail
        subject = formBean.subject
        text = "Message from $formBean.name <$formBean.email>: \n $formBean.message"
      }

      messages << emailMessage

      if (formBean.sendCopy) {
        def copyMessage = new SimpleMailMessage(emailMessage)

        copyMessage.with {
          to = formBean.email
          from = new InternetAddress(noreplyEmail, "MeetUpInTheMiddle")
          replyTo = noreplyEmail
          text = "Copy of your message sent to MeetUpInTheMiddle.com: \n\n$formBean.message"
        }

        messages << copyMessage
      }

      messages.each {
        javaMailSender.send(it)
      }

      return true
    } catch (MailException e) {
      LOGGER.error("Problem sending email", e)
      return false
    }
  }
}