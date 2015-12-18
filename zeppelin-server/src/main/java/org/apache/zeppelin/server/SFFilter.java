package org.apache.zeppelin.server;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.zeppelin.acl.Constants;
import org.apache.zeppelin.rest.NotebookRestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get the salesforce sessionId and instance URL and set it as cookie
 */
public class SFFilter implements Filter {
  private static Logger logger = LoggerFactory.getLogger(NotebookRestApi.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    if (headerNames != null) {
      logger.info("Printing headers...");
      logger.info("############################################################################");
      while (headerNames.hasMoreElements()) {
        String name = headerNames.nextElement();
        logger.info("Header Name : " + name);
        String value = httpRequest.getHeader(headerNames.nextElement());
        logger.info("Header Value : " + value);
      }
      logger.info("############################################################################");
    } else {
      logger.info("No header has been passed");
    }

    String authHeader = httpRequest.getHeader(Constants.HEADER_AUTH);
    logger.info("Auth header " + authHeader);
    if (authHeader != null) {
      addCookie(httpResponse, Constants.COOKIE_APEX_AUTH, getSessionId(authHeader));
    } else {
      logger.warn("Session Id not passed in header");
    }

    String instanceURL = httpRequest.getHeader(Constants.HEADER_INSTANCE_URL);
    logger.info("InstanceURL from header " + instanceURL);
    if (instanceURL != null) {
      addCookie(httpResponse, Constants.COOKIE_APEX_INSTANCE_URL, instanceURL);
    } else {
      logger.warn("InstanceURL not passed in header");
    }

    chain.doFilter(request, response);
  }

  private String getSessionId(String authHeader) {
    String sessionId = "";

    String[] authHeaderStrArr = authHeader.split(" ");
    if (authHeaderStrArr.length == 2) {
      sessionId = authHeaderStrArr[1];
    }

    return sessionId;
  }

  private void addCookie(HttpServletResponse httpResponse, String name, String value) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    httpResponse.addCookie(cookie);
  }

  @Override
  public void destroy() {
  }

}
