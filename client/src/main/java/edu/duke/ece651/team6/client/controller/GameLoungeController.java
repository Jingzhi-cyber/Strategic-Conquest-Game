package edu.duke.ece651.team6.client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.UIGame;
import edu.duke.ece651.team6.client.model.GameLounge;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameLoungeController extends Controller implements Initializable {

  GameLounge gameLounge;

  @FXML
  private GridPane gameLoungeGridPane;

  int id;

  String username;

  @FXML
  ListView<Integer> numPlayer;

  @FXML
  ListView<String> gameLoungeList;

  private Integer gameId = null;
  private Integer playerNumber = null;

  public GameLoungeController(Client client, GameLounge gameLounge) {
    super(client);
    this.gameLounge = gameLounge;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @FXML
  public void onSwitchPageButton(ActionEvent ae) {
    if (this.gameId == null) {
      showError("Please select a game id to continue");
      return;
    }

    // TODO client.joinStartedGame(this.gameId);

    try {
      switchToGameMainPage(this.gameId);
    } catch (Exception e) {
      e.printStackTrace();
      showError(e.getMessage());
    }
  }

  @FXML
  public void onNewGameButton(ActionEvent ae) throws ClassNotFoundException, IOException, Exception {
    // Create a new game

    // create a new game

    // push it to gameLounge
    // this.gameLounge.pushGame(gameId);

    if (this.playerNumber == null) {
      showError("Please select number of players to start");
      return;
    }

    try {
      Integer newGameId = client.createNewGame(this.playerNumber);

      this.gameLoungeList.getItems().add(String.valueOf(newGameId));
      gameLoungeList.refresh();

      System.out.println("Switching to Game Main Page");
      switchToGameMainPage(newGameId);

      // TODO
      // client.getUIGameById(newGameId).placeUnit();

    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
      showError(e.getMessage());
    }
  }

  @FXML
  private void onLogoutButton(ActionEvent event) throws Exception {
    logOut();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // @SuppressWarnings("unchecked")
    // ListView<String> tmp = (ListView<String>) scene.lookup("#gameLoungeList");
    // tmp.setItems(gameLounge.getList());

    // this.operands = tmp;
    // gameLoungeList.getItems().add("1");
    // gameLoungeList.getItems().add("2");
    // gameLoungeList.getItems().add("3");
    this.id = 0;

    numPlayer.getItems().add(2);
    numPlayer.getItems().add(3);
    numPlayer.getItems().add(4);

    numPlayer.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
        Integer selectedItem = numPlayer.getSelectionModel().getSelectedItem();
        System.out.println("Single-clicked on item: " + selectedItem);
        this.playerNumber = selectedItem;
      }
    });

    gameLoungeList.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
        String selectedItem = gameLoungeList.getSelectionModel().getSelectedItem();
        // System.out.println("Single-clicked on item: " + selectedItem);
        this.gameId = Integer.valueOf(selectedItem);
      }
    });

  }

  private void switchToGameMainPage(int gameId) throws Exception {

    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    UIGame game = client.getUIGameById(gameId);

    MainPageController mainPageController = game.getMainPageController();
    mainPageController.setGameLounge(gameLounge);
    //// mainPageController.setUiGame(game);
    //// game.setMainPageController(mainPageController);

    controllers.put(MainPageController.class, mainPageController);

    // setting the UIGame instance in the UnitPlacementController
    // UnitPlacementController unitPlacementController =
    // game.getUnitPlacementController();

    // controllers.put(UnitPlacementController.class, unitPlacementController);
    switchToPage("/ui/mainPage.xml", "/ui/buttonstyle.css", controllers, "Main Page", client.getStage());
  }
}
