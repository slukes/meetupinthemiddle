package com.meetupinthemiddle

import com.meetupinthemiddle.controllers.MainController
import org.aspectj.lang.ProceedingJoinPoint
import org.junit.Ignore
import org.junit.Test
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory

import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

class LoggingAspectTest {
  @Test
  @Ignore
  void testPointCut() {
    //Given
    def target = new MainController();
    def factory = new AspectJProxyFactory(target);
    def aspect = mock(LoggingAspect.class);
    factory.addAspect(aspect);
    def proxy = factory.getProxy();
    when(aspect.log(any(ProceedingJoinPoint))).thenReturn(null);

    //When
    proxy.index();

    //Then
    verify(aspect).log(any(ProceedingJoinPoint));
  }
}