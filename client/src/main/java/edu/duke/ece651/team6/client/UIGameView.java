package edu.duke.ece651.team6.client;

import edu.duke.ece651.team6.shared.GlobalMapInfo;
import javafx.scene.Scene;

/**
 * A class deal with Scene switching and displaying
 */
public class UIGameView extends MapView {

  Scene scene = null;

  public UIGameView(GlobalMapInfo map) {
    super(map);
  }

  @Override
  public String display() {
    // TODO Auto-generated method stub
    return null;
  }

  public void updateScene(Scene scene) {
    this.scene = scene;
  }
}
