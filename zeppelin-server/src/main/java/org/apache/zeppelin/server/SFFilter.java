package org.apache.zeppelin.server;

import static org.apache.zeppelin.acl.Constants.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Decoder;

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
      addCookie(httpResponse, Constants.COOKIE_APEX_AUTH, getDecodedStr(sessionId));
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

    String smlSessionId = httpRequest.getParameter(Constants.URL_PARAM_SML_SESSION_ID);
    logger.info("smlSessionId " + smlSessionId);
    if (smlSessionId != null) {
      addCookie(httpResponse, Constants.COOKIE_APEX_SML_SESSION_ID, smlSessionId);
    } else {
      logger.debug("SML Session Id not passed as URL Parameter");
    }

    String psUserURL = getPredServiceUserURL();
    logger.info("Predictive Service User URL " + psUserURL);
    if (psUserURL != null) {
      addCookie(httpResponse, Constants.COOKIE_PS_USER_URL, psUserURL);
    }

    chain.doFilter(request, response);
  }

  private String getPredServiceUserURL() {
    String predSerProtocol = System.getProperty(KEY_PREDICTIVE_SERVICE_PROTOCOL);
    String predSerHost = System.getProperty(KEY_PREDICTIVE_SERVICE_HOST);
    String predSerPort = System.getProperty(KEY_PREDICTIVE_SERVICE_PORT);

    StringBuilder predServiceUserURL = new StringBuilder();
    predServiceUserURL.append(predSerProtocol);
    predServiceUserURL.append(STR_COLON_SLASH_SLASH);
    predServiceUserURL.append(predSerHost);
    predServiceUserURL.append(STR_COLON);
    predServiceUserURL.append(predSerPort);

    predServiceUserURL.append(STR_SLASH);
    predServiceUserURL.append(PREDICTIVE_SERVICE_USER_PATH);

    return predServiceUserURL.toString();
  }

  private String getDecodedStr(String encodedSessionId) {
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
