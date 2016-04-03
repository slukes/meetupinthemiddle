package com.meetupinthemiddle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.ui.ExtendedModelMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoggingAspectTest {
  @Test
  public void testPointCut() throws Throwable {
    //Given
    TestController target = new TestController();
    AspectJProxyFactory factory = new AspectJProxyFactory(target);
    LoggingAspect aspect = Mockito.mock(LoggingAspect.class);
    factory.addAspect(aspect);
    TestController proxy = factory.getProxy();
    when(aspect.log(any(ProceedingJoinPoint.class))).thenReturn(null);

    //When
    proxy.index(new ExtendedModelMap());

    //Then
    verify(aspect).log(any(ProceedingJoinPoint.class));
  }
}