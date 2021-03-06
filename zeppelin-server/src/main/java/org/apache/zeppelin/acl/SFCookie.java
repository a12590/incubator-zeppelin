package org.apache.zeppelin.acl;

import static org.apache.zeppelin.acl.Constants.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model class to read cookie and read the SF values from it
 */
public class SFCookie {
  private static final Logger LOG = LoggerFactory.getLogger(SFCookie.class);

  private Map<String, String> cookies = new HashMap<String, String>();

  public SFCookie(String rawCookie) {
    parse(rawCookie);
  }

  public String getSFSessionId() {
    return cookies.get(COOKIE_APEX_AUTH);
  }

  public String getSFInstanceURL() {
    return cookies.get(COOKIE_APEX_INSTANCE_URL);
  }

  public String getSMLSessionId() {
    return cookies.get(COOKIE_APEX_SML_SESSION_ID);
  }

  private void parse(String rawCookie) {
    LOG.debug("Cookies : " + rawCookie);
    if (rawCookie != null) {
      String[] rawCookieParams = rawCookie.split(STR_SEMI_COLON);

      for (int i = 0; i < rawCookieParams.length; i++) {
        String[] rawCookieNameAndValue = rawCookieParams[i].split(STR_EQUAL);
        if (rawCookieNameAndValue.length != 2) {
          throw new RuntimeException("Invalid cookie: missing name and value.");
        }

        String cookieName = rawCookieNameAndValue[0].trim();
        String cookieValue = rawCookieNameAndValue[1].trim();
        cookies.put(cookieName, cookieValue);
      }
    }
  }
}
