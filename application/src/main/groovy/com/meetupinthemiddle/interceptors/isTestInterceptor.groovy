package com.meetupinthemiddle.interceptors

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class isTestInterceptor extends HandlerInterceptorAdapter {
  @Override
  boolean preHandle(
      final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
    def view = request.getParameter("view");
    //Ability to call with url?view=test to enable a param to be passed to GoogleMaps
    //Which allows ensures the map pins etc. are displayed in the DOM which is required
    //By auto tests.  This solution is not quite ideal, however appears to be the most viable.
    request.setAttribute("isTest", view?.equals("test"))
    true
  }
}