package edu.duke.ece651.team6.client.model;

import javafx.scene.control.ChoiceBox;

public class OrderMenuList {
  // ObservableList<Order> orderMenuList;

  ChoiceBox<String> choiceBox;

  public OrderMenuList() {
    this.choiceBox = new ChoiceBox<String>();
    this.choiceBox.getItems().add("Move");
    this.choiceBox.getItems().add("Attack");
    this.choiceBox.getItems().add("Research");
    this.choiceBox.getItems().add("Upgrade");
  }

}
