package org.apache.zeppelin.acl;

import static org.apache.zeppelin.acl.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.server.ZeppelinServer;
import org.apache.zeppelin.socket.NotebookSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Utility class to get the notebooks from Predictive Service REST API
 */
public class NotebookACLUtils {
  private static final Logger LOG = LoggerFactory.getLogger(NotebookACLUtils.class);

  public static Map<String, org.apache.zeppelin.acl.Note> getNotebooks(NotebookSocket socket) {
    List<org.apache.zeppelin.acl.Note> noteList = null;

    SFCookie sfCookie = new SFCookie(socket.getCookie());
    LOG.info("AuthHeader : " + sfCookie.getSFSessionId());
    LOG.info("InstanceURL : " + sfCookie.getSFInstanceURL());

    // Easter Egg - To test notebooks directly created in zeppelin
    // Has to be removed
//    if (sfCookie.getSFSessionId() == null || sfCookie.getSFSessionId().isEmpty()
//            || sfCookie.getSFSessionId().equals("admin123$")) {
//      LOG.info("Bypassing security...");
//      return getAllKeys();
//    }

    try {
      String jsonResponse = new HTTPHelper().get(getPredictiveServiceURL(),
              sfCookie.getSFSessionId(),
              sfCookie.getSFInstanceURL());
      Notebooks notebooks = new Gson().fromJson(jsonResponse, Notebooks.class);
      noteList = notebooks.getNotebooks();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Map<String, org.apache.zeppelin.acl.Note> notebookMap = new HashMap<>();
    if (noteList != null && !noteList.isEmpty()) {
      for (org.apache.zeppelin.acl.Note note : noteList) {
        notebookMap.put(note.getId(), note);
      }
    }

    LOG.info("Allowed notebooks " + notebookMap.keySet());
    return notebookMap;
  }

  private static Map<String, org.apache.zeppelin.acl.Note> getAllKeys() {
    Notebook notebook = ZeppelinServer.notebook;
    List<Note> notes = notebook.getAllNotes();
    Map<String, org.apache.zeppelin.acl.Note> notebookList = new HashMap<>();
    if (notes != null) {
      for (Note note : notes) {
        org.apache.zeppelin.acl.Note aclNote = new org.apache.zeppelin.acl.Note();
        aclNote.setId(note.getId());
        aclNote.setHideCode(true);
        notebookList.put(note.getId(), aclNote);
      }
    }

    return notebookList;
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
