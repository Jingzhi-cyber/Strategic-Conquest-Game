package edu.duke.ece651.team6.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.GlobalMapInfo;

class UIGameViewTest {
  private GlobalMapInfo map;
  private UIGameView uiGameView;

  @BeforeEach
  void setUp() {
    map = new GlobalMapInfo();
    uiGameView = new UIGameView(map);
  }

  @Test
  void test_display() {
    uiGameView.display();
  }

}
