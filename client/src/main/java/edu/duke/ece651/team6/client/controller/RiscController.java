package edu.duke.ece651.team6.client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

public class RiscController implements Initializable {

  @FXML
  ChoiceBox<String> orderMenu;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ObservableList<String> items = FXCollections.observableArrayList("Move", "Attack", "Research", "Upgrade");
    orderMenu.setItems(items);
    orderMenu.setValue("Move");
  }
}
