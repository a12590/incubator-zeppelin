package org.apache.zeppelin.acl;

import java.util.List;

/**
 * Bean class for JSON object returned from predictive service REST API
 */
public class Notebooks {
  private List<Note> notebooks;

  public Notebooks() {
  }

  public Notebooks(List<Note> notebooks) {
    this.setNotebooks(notebooks);
  }

  public List<Note> getNotebooks() {
    return notebooks;
  }

  public void setNotebooks(List<Note> notebooks) {
    this.notebooks = notebooks;
  }

  @Override
  public String toString() {
    return "Notebooks [notebooks=" + notebooks + "]";
  }

}
