package edu.duke.ece651.team6.shared;

import java.io.Serializable;

public class TestMap implements Serializable {

  private String name;

  public TestMap(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
