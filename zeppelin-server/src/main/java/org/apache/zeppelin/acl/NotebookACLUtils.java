package org.apache.zeppelin.acl;

import static org.apache.zeppelin.acl.Constants.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.zeppelin.socket.NotebookSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Utility class to get the notebooks from Predictive Service REST API
 */
public class NotebookACLUtils {
  private static final Logger LOG = LoggerFactory.getLogger(NotebookACLUtils.class);

  public static List<String> getNotebooks(NotebookSocket socket) {
    List<Note> noteList = null;

    SFCookie sfCookie = new SFCookie(socket.getCookie());
    LOG.info("AuthHeader : " + sfCookie.getSFSessionId());
    LOG.info("InstanceURL : " + sfCookie.getSFInstanceURL());

    try {
      String jsonResponse = new HTTPHelper().get(getPredictiveServiceURL(),
              sfCookie.getSFSessionId(),
              sfCookie.getSFInstanceURL());
      Notebooks notebooks = new Gson().fromJson(jsonResponse, Notebooks.class);
      noteList = notebooks.getNotebooks();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    List<String> notebookKeys = new ArrayList<>();
    if (noteList != null && !noteList.isEmpty()) {
      for (Note note : noteList) {
        notebookKeys.add(note.getId());
      }
    }

    LOG.info("Allowed notebooks " + notebookKeys);
    return notebookKeys;
  }

  private static String getPredictiveServiceURL() {
    String predSerProtocol = System.getProperty(KEY_PREDICTIVE_SERVICE_PROTOCOL);
    String predSerHost = System.getProperty(KEY_PREDICTIVE_SERVICE_HOST);
    String predSerPort = System.getProperty(KEY_PREDICTIVE_SERVICE_PORT);

    StringBuilder predServiceURL = new StringBuilder();
    predServiceURL.append(predSerProtocol);
    predServiceURL.append(STR_COLON_SLASH_SLASH);
    predServiceURL.append(predSerHost);
    predServiceURL.append(STR_COLON);
    predServiceURL.append(predSerPort);

    predServiceURL.append(STR_SLASH);
    predServiceURL.append(PREDICTIVE_SERVICE_APP_PATH);

    LOG.info("predServiceURL : " + predServiceURL);
    return predServiceURL.toString();
  }

}
