package edu.duke.ece651.team6.client.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.SocketHandler;
import edu.duke.ece651.team6.client.model.GameLounge;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

public class GameLoungeController extends Controller implements Initializable {

  GameLounge gameLounge;

  @FXML
  private GridPane gameLoungeGridPane;

  int id;

  @FXML
  ListView<String> gameLoungeList;

  public GameLoungeController(SocketHandler client, GameLounge gameLounge) {
    super(client);
    this.gameLounge = gameLounge;
  }

  @FXML
  public void onSwitchPageButton(ActionEvent ae) {
  }

  @FXML
  public void onNewGameButton(ActionEvent ae) {
    // TODO Create a new game

    // create a new game
    // String gameId = uiPlayer.createNewGame();

    // push it to gameLounge
    // this.gameLounge.pushGame(gameId);
    this.gameLounge.pushGame(String.valueOf(id++));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // @SuppressWarnings("unchecked")
    // ListView<String> tmp = (ListView<String>) scene.lookup("#gameLoungeList");
    // tmp.setItems(gameLounge.getList());

    // this.operands = tmp;
    gameLoungeList.getItems().add("Item 1");
    gameLoungeList.getItems().add("Item 2");
    gameLoungeList.getItems().add("Item 3");
    this.id = 0;

    gameLoungeList.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
        String selectedItem = gameLoungeList.getSelectionModel().getSelectedItem();
        System.out.println("Single-clicked on item: " + selectedItem);
      }
    });

    // Button button = new Button("Read Selected Value");

    // button.setOnAction(event -> {
    // ObservableList<Integer> selectedIndices =
    // operands.getSelectionModel().getSelectedIndices();

    // for (Object o : selectedIndices) {
    // showSuccess("o = " + o + " (" + o.getClass() + ")");
    // System.out.println("o = " + o + " (" + o.getClass() + ")");

    // try {
    // switchToGameMainPage((int) o);
    // } catch (Exception e) {
    // e.printStackTrace();
    // showError("Failed to load main page");
    // }
    // }
    // });
  }

  private void switchToGameMainPage(int gameId) throws Exception {
    // Load the main game window
    // ...

    // Get the game to switch to

    // switch to that game controller?

    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    controllers.put(MainPageController.class, new MainPageController(client));
    switchToPage("/ui/mainPage.xml", "/ui/buttonstyle.css", controllers, "Main Page", gameLoungeGridPane);
  }

}
