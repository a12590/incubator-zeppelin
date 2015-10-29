package org.apache.zeppelin.rest.message;

/**
 * Request message for creating new paragraph
 */
public class NewParagraphRequest {
  private String title;
  private String content;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
