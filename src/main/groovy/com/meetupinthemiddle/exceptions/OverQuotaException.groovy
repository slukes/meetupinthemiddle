package com.meetupinthemiddle.exceptions

import groovy.transform.TupleConstructor

@TupleConstructor
class OverQuotaException extends RuntimeException{
  //Nothing to see here, this is used as a provider neutral way of handling this case.
  //I could instead catch the google exception in the controller, but this would mean tighter coupling between
  //the two which I'm trying to avoid.
}
