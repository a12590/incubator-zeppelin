/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.rest.message.NewParagraphRequest;
import org.apache.zeppelin.server.JsonResponse;
import org.apache.zeppelin.socket.NotebookServer;

import com.google.gson.Gson;

/**
 * REST API related to paragraphs
 */
@Path("/notebook/{notebookId}/paragraph")
@Produces("application/json")
public class ParagraphRestApi {
  private Gson gson = new Gson();
  private Notebook notebook;
  private NotebookServer notebookServer;

  public ParagraphRestApi() {
  }

  public ParagraphRestApi(Notebook notebook, NotebookServer notebookServer) {
    this.notebook = notebook;
    this.notebookServer = notebookServer;
  }

  @GET
  @Path("/")
  public Response getParagraphList(@PathParam("notebookId") String notebookId) {
    Note note = notebook.getNote(notebookId);
    return new JsonResponse(Status.OK, "", note.getParagraphs()).build();
  }

  @POST
  @Path("/")
  public Response addParagraph(@PathParam("notebookId")
      String notebookId, String jsonMessage) throws IOException {
    Note note = notebook.getNote(notebookId);

    NewParagraphRequest paraRequest = gson.fromJson(jsonMessage, NewParagraphRequest.class);
    Paragraph newParagraph = note.addParagraph();
    persistParagraph(note, paraRequest, newParagraph);
    return new JsonResponse(Status.CREATED, "", newParagraph.getId()).build();
  }

  @POST
  @Path("/{paraId}")
  public Response updateParagraph(@PathParam("notebookId") String notebookId,
      @PathParam("paraId") String paraId,
      String jsonMessage) throws IOException {
    Note note = notebook.getNote(notebookId);

    NewParagraphRequest paraRequest = gson.fromJson(jsonMessage, NewParagraphRequest.class);
    Paragraph paragraph = note.getParagraph(paraId);
    persistParagraph(note, paraRequest, paragraph);
    return new JsonResponse(Status.ACCEPTED, "", paragraph.getId()).build();
  }

  private void persistParagraph(Note note, NewParagraphRequest paraRequest,
      Paragraph paragraph) throws IOException {
    paragraph.setTitle(paraRequest.getTitle());
    paragraph.setText(paraRequest.getText());

    note.persist();
    notebookServer.broadcastNote(note);
    notebookServer.broadcastNoteList();
  }
}
