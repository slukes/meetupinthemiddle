package com.meetupinthemiddle;

import com.meetupinthemiddle.Controllers.MainController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoggingAspectTest {
  @Test
  public void testPointCut() throws Throwable {
    //Given
    MainController target = new MainController();
    AspectJProxyFactory factory = new AspectJProxyFactory(target);
    LoggingAspect aspect = Mockito.mock(LoggingAspect.class);
    factory.addAspect(aspect);
    MainController proxy = factory.getProxy();
    when(aspect.log(any(ProceedingJoinPoint.class))).thenReturn(null);

    //When
    proxy.index();

    //Then
    verify(aspect).log(any(ProceedingJoinPoint.class));
  }
}