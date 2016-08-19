package com.meetupinthemiddle.services.contact;

import com.meetupinthemiddle.model.ContactFormBean;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static com.meetupinthemiddle.Stubs.randomFormBean;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class JavaMailcontactServiceTest {

  @InjectMocks
  JavaMailContactService contactService;

  @Mock
  JavaMailSender javaMailSender;
  private String toEmail;

  @Before
  public void init() {
    toEmail = RandomStringUtils.random(20);
    setField(contactService, "toEmailAddress", toEmail);
  }

  @Test
  public void testEmailIsSent() {
    //Given
    ContactFormBean formBean = randomFormBean();
    formBean.setSendCopy(false);

    //When
    boolean result = contactService.sendMessage(formBean);

    //Then
    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(javaMailSender, times(1)).send(captor.capture());

    SimpleMailMessage messageSent = captor.getValue();

    assertThat(messageSent.getTo(), hasItemInArray(toEmail));
    assertThat(messageSent.getSubject(), is(formBean.getSubject()));
    assertThat(messageSent.getText(), containsString(formBean.getMessage()));
  }

  @Test
  public void secondEmailIsSentIfSendCopyIsTrue() {
    //Given
    ContactFormBean formBean = randomFormBean();
    formBean.setSendCopy(true);

    //When
    boolean result = contactService.sendMessage(formBean);

    //Then
    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(javaMailSender, times(2)).send(captor.capture());

    SimpleMailMessage messageSent = captor.getAllValues().get(1);

    assertThat(messageSent.getTo(), hasItemInArray(formBean.getEmail()));
    assertThat(messageSent.getSubject(), is(formBean.getSubject()));
    assertThat(messageSent.getText(), containsString(formBean.getMessage()));
  }

  @Test
  public void returnsFalseIfException() {
    //Given
    doThrow(new MailSendException("test")).when(javaMailSender).send(any(SimpleMailMessage.class));

    //When
    boolean result = contactService.sendMessage(randomFormBean());

    //Then
    verify(javaMailSender).send(any(SimpleMailMessage.class));
    assertThat(result, is(false));
  }
}
