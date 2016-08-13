package com.meetupinthemiddle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.builder.Builder
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.Length

import javax.validation.constraints.NotNull

@Builder
@EqualsAndHashCode
@ToString
class ContactFormBean {
  @NotNull(message = "Please enter your name")
  @Length(min = 1, message = "Please enter your name")
  String name

  @NotNull(message = "Please enter your email address")
  @Length(min = 1, message = "Please enter your email address")
  @Email(message = "The email address you have entered is invalid")
  String email

  @Length(min = 1, message = "Please enter a subject")
  @NotNull(message = "Please enter a subject")
  String subject

  @Length(min = 1, message = "Please enter a message")
  @NotNull(message = "Please enter a message")
  String message
  boolean sendCopy
}