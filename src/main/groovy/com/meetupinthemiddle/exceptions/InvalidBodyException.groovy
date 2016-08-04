package com.meetupinthemiddle.exceptions

import groovy.transform.TupleConstructor
import org.springframework.validation.FieldError

@TupleConstructor
class InvalidBodyException extends RuntimeException{
  List<FieldError> fieldError
}