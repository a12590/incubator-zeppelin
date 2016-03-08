package org.apache.zeppelin.acl;

import static org.apache.zeppelin.acl.Constants.*;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

/**
 * Helper class for HTTP requests
 */
public class HTTPHelper {
  private static final Logger LOG = Logger.getLogger(HTTPHelper.class);

  public String get(String url, SFCookie sfCookie) throws Exception {
    LOG.info("Executing GET request on " + url);
    HttpGet httpGet = new HttpGet(url);
    httpGet.setConfig(getRequestConfig());

    if (!StringUtils.isEmpty(sfCookie.getSMLSessionId())) {
      httpGet.addHeader(HEADER_AUTH, sfCookie.getSMLSessionId());
    } else {
      httpGet.addHeader(HEADER_AUTH, HEADER_AUTH_TOKEN + sfCookie.getSFSessionId());
      httpGet.addHeader(HEADER_INSTANCE_URL, STR_HTTPS_URL_PREFIX + sfCookie.getSFInstanceURL());
    }
    httpGet.addHeader(HEADER_ACCEPT, HEADER_APPLICATION_JSON);

    return execute(url, httpGet);
  }

  private String execute(String url, HttpUriRequest httpReq) throws Exception {
    CloseableHttpClient httpClient = HttpClients.createDefault();

    InputStream eis = null;
    try {
      CloseableHttpResponse response = httpClient.execute(httpReq);

      int statusCode = response.getStatusLine().getStatusCode();
      if (!(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED)) {
        String reasonPhrase = response.getStatusLine().getReasonPhrase();
        String errResponse = IOUtils.toString(response.getEntity().getContent(), STR_UTF_8);
        throw new Exception(
                String.format("Accessing %s failed. Status %d. Reason %s \n Error from server %s",
                url, statusCode, reasonPhrase, errResponse));
      }

      HttpEntity responseEntity = response.getEntity();
      eis = responseEntity.getContent();
      return IOUtils.toString(eis, STR_UTF_8);
    } finally {
      try {
        if (httpClient != null) {
          httpClient.close();
        }
      } catch (Exception e) {
        LOG.debug("Error while closing HTTP Client", e);
      }

      try {
        if (eis != null) {
          eis.close();
        }
      } catch (Exception e) {
        LOG.debug("Error while closing InputStream", e);
      }
    }
  }

  private static RequestConfig getRequestConfig() {
    RequestConfig requestConfig = RequestConfig.custom()
           .setSocketTimeout(CONNECTION_TIMEOUT)
           .setConnectTimeout(CONNECTION_TIMEOUT)
           .setConnectionRequestTimeout(CONNECTION_TIMEOUT).build();

    return requestConfig;
  }
}
