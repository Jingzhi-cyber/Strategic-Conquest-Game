package edu.duke.ece651.team6.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameLounge {
  ObservableList<String> myGameLounge;

  public GameLounge() {
    myGameLounge = FXCollections.observableArrayList();
  }

  public int size() {
    return myGameLounge.size();
  }

  public ObservableList<String> getList() {
    return myGameLounge;
  }

  public void pushGame(String game) {
    myGameLounge.add(game);
  }

  public String removeGame(int index) {
    return myGameLounge.remove(index);
  }

  public boolean removeGame(String game) {
    return myGameLounge.remove(game);
  }
}
