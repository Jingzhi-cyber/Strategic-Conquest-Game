package edu.duke.ece651.team6.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameLoungeTest {
  private GameLounge gameLounge;

  @BeforeEach
  void setUp() {
    gameLounge = new GameLounge();
  }

  @Test
  void testPushGame() {
    gameLounge.pushGame("Game1");
    assertEquals(1, gameLounge.size());
    gameLounge.pushGame("Game2");
    assertEquals(2, gameLounge.size());
  }

  @Test
  void testRemoveGameByIndex() {
    gameLounge.pushGame("Game1");
    gameLounge.pushGame("Game2");
    assertEquals("Game1", gameLounge.removeGame(0));
    assertEquals(1, gameLounge.size());
  }

  @Test
  void testRemoveGameByName() {
    gameLounge.pushGame("Game1");
    gameLounge.pushGame("Game2");
    assertTrue(gameLounge.removeGame("Game1"));
    assertFalse(gameLounge.removeGame("Game3"));
    assertEquals(1, gameLounge.size());
  }

  @Test
  void testGetList() {
    ObservableList<String> list = gameLounge.getList();
    gameLounge.pushGame("Game1");
    assertEquals(list, gameLounge.getList());
  }
}
