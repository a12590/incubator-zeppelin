package org.apache.zeppelin.acl;

/**
 * An utility class to hold constants
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

  public static final String URL_PARAM_SF_SESSION_ID = "sf_sessionId";
  public static final String URL_PARAM_SF_INSTANCE_URL = "sf_InstanceURL";
  public static final String URL_PARAM_SML_SESSION_ID = "sml_session_id";

  public static final String COOKIE_APEX_AUTH = "apex__Authorization";
  public static final String COOKIE_APEX_INSTANCE_URL = "apex__InstanceURL";
  public static final String COOKIE_APEX_SML_SESSION_ID = "apex__SMLSessionId";
  public static final String COOKIE_PS_USER_URL = "ps__userURL";
  public static final String COOKIE_SID = "sid";

  public static final String STR_COLON_SLASH_SLASH = "://";
  public static final String STR_SLASH = "/";
  public static final String STR_COLON = ":";
  public static final String STR_SEMI_COLON = ";";
  public static final String STR_EQUAL = "=";
  public static final String STR_UTF_8 = "UTF-8";
  public static final String STR_HTTPS_URL_PREFIX = "https://";

  public static final String PREDICTIVE_SERVICE_APP_PATH = "zeppelin/notebook";
  public static final String PREDICTIVE_SERVICE_USER_PATH = "user";

  public static final int CONNECTION_TIMEOUT = 60000;
}
