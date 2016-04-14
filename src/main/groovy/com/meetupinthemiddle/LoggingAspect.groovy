package com.meetupinthemiddle

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@Component
@Aspect
class LoggingAspect {
  /**
   * When trace logging is enabled this around advice will log
   * entry, arguments and exit from every method for debugging purposes
   *
   * @param joinPoint method call intercepted
   * @return the result from the jointpoint's underlying method
   * @throws Throwable if thrown by the underlying method
   */
  @Around("execution(* com.meetupinthemiddle..*.*(..))")
  Object log(ProceedingJoinPoint joinPoint) {
    def logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType())
    if (logger.isDebugEnabled()) {
      String args = argsAsString(joinPoint.getArgs())
      logger.debug("Entering method ${joinPoint.getSignature().getName()} With args (  $args  )")
    }

    final def result = joinPoint.proceed()

    if (logger.isDebugEnabled()) {
      logger.debug("Exiting  ${joinPoint.getSignature().getName()}")
    }

    return result
  }

  private String argsAsString(Object[] args) {
    return Arrays.stream(args)
        .map({ it.toString() })
        .collect(Collectors.joining(", "))
  }
}
