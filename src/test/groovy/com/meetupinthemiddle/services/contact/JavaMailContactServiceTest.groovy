package com.meetupinthemiddle.services.contact
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.mail.MailSendException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

import static com.meetupinthemiddle.Stubs.randomFormBean
import static org.junit.Assert.assertThat
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*
import static org.springframework.test.util.ReflectionTestUtils.setField

@RunWith(MockitoJUnitRunner)
class JavaMailContactServiceTest {

  @InjectMocks
  JavaMailContactService contactService

  @Mock
  JavaMailSender javaMailSender

  @Before
  void init(){
    setField(contactService, "toEmailAddress", "foo@bar.com")
  }

  @Test
  void testEmailIsSent(){
    //When
    def formBean = randomFormBean()
    def result = contactService.sendMessage(formBean)

    //Then
    verify(javaMailSender).send(any(SimpleMailMessage));

  }

  @Test
  void secondEmailIsSentIfSendCopyIsTrue(){
    //When
    def formBean = randomFormBean()
    formBean.sendCopy = true
    def result = contactService.sendMessage(formBean)

    //Then
    verify(javaMailSender, times(2)).send(any(SimpleMailMessage));
  }

  @Test
  void returnsFalseIfException(){
    //Given
    doThrow(new MailSendException("test")).when(javaMailSender).send(any(SimpleMailMessage))

    //When
    def result = contactService.sendMessage(randomFormBean())

    //Then
//    verify(javaMailSender).send(any(SimpleMailMessage))
    assertThat(result, Matchers.is(false))
  }

}
