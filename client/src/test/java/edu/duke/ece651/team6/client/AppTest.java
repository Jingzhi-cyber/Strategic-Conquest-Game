package edu.duke.ece651.team6.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
  @Test
  void test_GetMessage() {
    App a = new App(null, null);
    assertEquals("Hello from the client for team6", a.getMessage());
  }
}
