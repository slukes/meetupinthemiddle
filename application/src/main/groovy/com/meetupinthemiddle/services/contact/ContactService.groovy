package com.meetupinthemiddle.services.contact

import com.meetupinthemiddle.model.ContactFormBean

interface ContactService {
  boolean sendMessage(ContactFormBean formBean)
}