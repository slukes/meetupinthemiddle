package com.meetupinthemiddle.model

class ErrorResponse {
  enum ErrorReason {
    NOT_ENOUGH_PEOPLE,
    MISSING_NAME,
    MISSING_FROM,
    MISSING_OR_INVALID_POI_TYPE,
    MISSING_OR_INVALID_TRANSPORT_MODE,
    UNKNOWN
  }
  List<ErrorReason> errorReasons = []

  void addReason(ErrorReason reason) {
    errorReasons.add(reason)
  }
}