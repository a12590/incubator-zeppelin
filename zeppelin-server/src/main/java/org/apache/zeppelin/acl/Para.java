package org.apache.zeppelin.acl;

/**
 * Paragraph details from REST gateway
 */
public class Para {
  private String id;
  private String title;
  private boolean hideCode;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isHideCode() {
    return hideCode;
  }

  public void setHideCode(boolean hideCode) {
    this.hideCode = hideCode;
  }

}
