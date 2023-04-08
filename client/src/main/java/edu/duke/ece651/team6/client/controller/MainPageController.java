package edu.duke.ece651.team6.client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import edu.duke.ece651.team6.client.SocketHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;

public class MainPageController extends Controller implements Initializable {
  // @FXML
  // Label username;

  // private String user;

  // public void setUser(String user) {
  // this.user = user;
  // }

  SocketHandler socketHandler;

  @FXML
  ChoiceBox<String> orderMenu;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ObservableList<String> items = FXCollections.observableArrayList("Move", "Attack", "Research", "Upgrade");
    orderMenu.setItems(items);
    orderMenu.setValue("Move");
  }

  public MainPageController(SocketHandler client) {
    super(client);
    // this.username.setText(userName);
  }
}
