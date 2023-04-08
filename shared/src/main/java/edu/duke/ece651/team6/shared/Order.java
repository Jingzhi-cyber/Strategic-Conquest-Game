package edu.duke.ece651.team6.shared;

public class Order implements java.io.Serializable {
  private String orderName;

  public Order(String name) {
    this.orderName = name;
  }

  public String getName() {
    return orderName;
  }
}
