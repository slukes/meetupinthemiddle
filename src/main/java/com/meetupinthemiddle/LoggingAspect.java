package com.meetupinthemiddle;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Aspect
public class LoggingAspect {
  /**
   * When trace logging is enabled this around advice will log
   * entry, arguments and exit from every method for debugging purposes
   *
   * @param joinPoint method call intercepted
   * @return the result from the jointpoint's underlying method
   * @throws Throwable if thrown by the underlying method
   */
  @Around("execution(* com.meetupinthemiddle..*.*(..))")
  public Object log(final ProceedingJoinPoint joinPoint) throws Throwable {
    final Logger logger = Logger.getLogger(joinPoint.getSignature().getDeclaringType());
    if (logger.isDebugEnabled()) {
      String args = argsAsString(joinPoint.getArgs());
      logger.debug("Entering method" + joinPoint.getSignature().getName()
          + " With args (" + args + ")");
    }

    final Object result = joinPoint.proceed();

    if (logger.isDebugEnabled()) {
      logger.debug("Exiting " + joinPoint.getSignature().getName());
    }

    return result;
  }

  private String argsAsString(final Object[] args) {
    return Arrays.stream(args)
        .map(Object::toString)
        .collect(Collectors.joining(", "));
  }
}
