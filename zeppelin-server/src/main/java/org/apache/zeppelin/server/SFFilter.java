package org.apache.zeppelin.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Decoder;
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

    String sessionId = httpRequest.getParameter(Constants.URL_PARAM_SF_SESSION_ID);
    logger.info("Salesforce Session Id" + sessionId);
    if (sessionId != null) {
      addCookie(httpResponse, Constants.COOKIE_APEX_AUTH, getSessionId(sessionId));
    } else {
      logger.warn("Session Id not passed as URL Parameter");
    }

    String instanceURL = httpRequest.getParameter(Constants.URL_PARAM_SF_INSTANCE_URL);
    logger.info("InstanceURL " + instanceURL);
    if (instanceURL != null) {
      addCookie(httpResponse, Constants.COOKIE_APEX_INSTANCE_URL, instanceURL);
    } else {
      logger.warn("InstanceURL not passed as URL Parameter");
    }

    chain.doFilter(request, response);
  }

  private String getSessionId(String encodedSessionId) {
    Decoder decoder = Base64.getDecoder();
    byte[] decodedValue = decoder.decode(encodedSessionId);
    return new String(decodedValue, Charset.forName("UTF-8"));
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
