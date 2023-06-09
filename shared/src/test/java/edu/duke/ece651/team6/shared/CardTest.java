package edu.duke.ece651.team6.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
  @Test
  void basicTest() {
    Card card = new Card("Paratroopers", 0, "cool");
    assertEquals("Paratroopers", card.getName());
    assertEquals(0, card.getCode());
    assertEquals("cool", card.getDescription());
    for (int i = 0; i < 1000; i++) {
      Card card1 = Card.chouOneCard();
      assertTrue(card1.getCode() >= -1 && card1.getCode() < 6);
    }
  }

}
