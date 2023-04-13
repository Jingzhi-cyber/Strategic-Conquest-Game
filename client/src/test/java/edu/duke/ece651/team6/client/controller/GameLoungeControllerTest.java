package edu.duke.ece651.team6.client.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.model.GameLounge;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class GameLoungeControllerTest {

  private GameLoungeController gameLoungeController;

  // @Mock
  // private Client client;

  // @Mock
  // private GameLounge gameLounge;

  // @Start
  // public void start(Stage stage) throws Exception {
  // FXMLLoader loader = new
  // FXMLLoader(getClass().getResource("/ui/game-lounge-page.xml"));
  // Parent root = loader.load();
  // gameLoungeController = new GameLoungeController(client, gameLounge);
  // loader.setController(gameLoungeController);
  // stage.setScene(new Scene(root));
  // stage.show();
  // }

  // @BeforeEach
  // public void setUp() {
  // gameLoungeController.setUsername("testuser");
  // when(client.getGameLounge()).thenReturn(gameLounge);
  // }

  // @Disabled
  // @Test
  // public void testInitialize() {
  // verify(client).getGameLounge();

  // ListView<Integer> numPlayerList = gameLoungeController.numPlayer;
  // verifyThat(numPlayerList, hasItems(3));
  // }

  // Add more test methods here for other methods in GameLoungeController

}
