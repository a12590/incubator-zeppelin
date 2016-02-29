package org.apache.zeppelin.acl;

import java.util.List;

/**
 * Notebook content provider
 */
public class Note {
  private String id;
  private String name;
  private String description;
  private String url;
  private List<Para> paragraphs;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * Title of notebook
   * @return
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<Para> getParagraphs() {
    return paragraphs;
  }

  public void setParagraphs(List<Para> paragraphs) {
    this.paragraphs = paragraphs;
  }

  @Override
  public String toString() {
    return "Note [id=" + id + ", name=" + name + ", description=" + description
            + ", url=" + url + ", paragraphs="
            + paragraphs + "]";
  }

}
