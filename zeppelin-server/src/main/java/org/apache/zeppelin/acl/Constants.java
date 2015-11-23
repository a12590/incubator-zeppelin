package org.apache.zeppelin.acl;

/**
 * An utility classs to hold constants
 */
public class Constants {
  public static final String KEY_PREDICTIVE_SERVICE_PROTOCOL = "PREDICTIVE_SERVICE_PROTOCOL";
  public static final String KEY_PREDICTIVE_SERVICE_HOST = "PREDICTIVE_SERVICE_HOST";
  public static final String KEY_PREDICTIVE_SERVICE_PORT = "PREDICTIVE_SERVICE_PORT";

  public static final String HEADER_APPLICATION_JSON = "application/json";
  public static final String HEADER_ACCEPT = "Accept";
  public static final String HEADER_AUTH = "Authorization";
  public static final String HEADER_AUTH_TOKEN = "token ";
  public static final String HEADER_INSTANCE_URL = "InstanceURL";

  public static final String COOKIE_SF_SESSION_ID = "sf_session_id";
  public static final String COOKIE_SF_INSTANCE_URL = "sf_instance_url";
  public static final String COOKIE_SID = "sid";

  public static final String STR_COLON_SLASH_SLASH = "://";
  public static final String STR_COLON = ":";
  public static final String STR_SEMI_COLON = ";";
  public static final String STR_EQUAL = "=";
  public static final String STR_UTF_8 = "UTF-8";

  public static final String PREDICTIVE_SERVICE_APP_PATH = "zeppelin/notebook";

  public static final int CONNECTION_TIMEOUT = 60000;
}
