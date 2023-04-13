/**

This class is responsible for controlling the behavior of the game lounge UI. It handles the creation of new games, joining existing games, and transitioning to the main page of a game.
*/
package edu.duke.ece651.team6.client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.UIGame;
import edu.duke.ece651.team6.client.model.GameLounge;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

public class GameLoungeController extends Controller implements Initializable {

  /**
   * 
   * The GameLounge object that this controller is associated with.
   */
  GameLounge gameLounge;

  /**
   * 
   * The GridPane object that represents the UI for the game lounge.
   */
  @FXML
  private GridPane gameLoungeGridPane;

  /**
   * 
   * The ID of the currently selected game.
   */
  int id;

  /**
   * 
   * The username of the current user.
   */
  String username;

  /**
   * 
   * The ListView object that displays the number of players in each game.
   */
  @FXML
  ListView<Integer> numPlayer;

  /**
   * 
   * The ListView object that displays the list of available games.
   */
  @FXML
  ListView<String> gameLoungeList;

  /**
   * 
   * The ID of the game that the user has selected to join.
   */
  private Integer gameId = null;
  /**
   * 
   * The number of players in the game that the user wants to create.
   */
  private Integer playerNumber = null;

  /**
   * 
   * Constructs a new GameLoungeController object with the given Client and
   * GameLounge objects.
   * 
   * @param client     The Client object to associate with this controller.
   * @param gameLounge The GameLounge object to associate with this controller.
   */
  public GameLoungeController(Client client, GameLounge gameLounge) {
    super(client);
    this.gameLounge = gameLounge;
  }

  /**
   * 
   * Sets the username of the current user.
   * 
   * @param username The username of the current user.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * 
   * Handles the behavior when the user clicks the "Switch Page" button. If no
   * game is selected, displays an error message. If a game is selected, switches
   * to the main page of that game.
   * 
   * @param ae The ActionEvent object that triggered this method call.
   */
  @FXML
  public void onSwitchPageButton(ActionEvent ae) {
    if (this.gameId == null) {
      showError("Please select a game id to continue");
      return;
    }

    try {
      switchToGameMainPage(this.gameId, true);
    } catch (Exception e) {
      e.printStackTrace();
      showError(e.getMessage());
    }
  }

  /**
   * 
   * Updates the game list in the UI to match the current GameLounge object.
   * 
   * @param gameLounge The updated GameLounge object.
   */
  public void updateGameList(GameLounge gameLounge) {
    this.gameLounge = gameLounge;
    gameLoungeList.setItems(gameLounge.getList());
  }

  /**
   * 
   * Handles the behavior when the user clicks the "New Game" button. If no player
   * number is selected, displays an error message. If a player number is
   * selected, creates a new game and switches to the main page of that game.
   * 
   * @param ae The ActionEvent object that triggered this method call.
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws Exception
   */
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

      // System.out.println("In Game Lounge Controller: Game Lounge updated, size is "
      // + gameLounge.size());

      System.out
          .println("In Game Lounge Controller: Game Lounge List updated, size is  " + gameLoungeList.getItems().size());

      // System.out.println("Game Lounge updated: " +
      // gameLoungeList.getAccessibleText() + " size: " + gameLounge.size());

      System.out.println("Switching to Game Main Page");
      switchToGameMainPage(newGameId, false);

      // TODO
      // client.getUIGameById(newGameId).placeUnit();

    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
      showError(e.getMessage());
    }
  }

  /**
   * 
   * This method logs out the current user and goes back to the login page.
   * 
   * @param event The button click event that triggers this method.
   * @throws Exception if there is any issue with logging out.
   */
  @FXML
  private void onLogoutButton(ActionEvent event) throws Exception {
    logOut();
  }

  /**
   * 
   * This method allows the user to go back to a previously started game.
   * 
   * @throws Exception if there is any issue with returning to the game.
   */
  @FXML
  private void onBackToGameButton() throws Exception {
    int gameId = -1;
    try {
      gameId = client.backToGame();
    } catch (IOException | ClassNotFoundException e) {
      Platform.runLater(() -> {
        showError("Cannot back to a game, error message: " + e.getMessage());
      });
    }
    if (gameId != -1) {
      this.gameLoungeList.getItems().add(String.valueOf(gameId));
      gameLoungeList.refresh();
      switchToGameMainPage(gameId, false);
    }
  }

  /**
   * Initializes the game lounge GUI by setting the number of players list, the
   * game lounge list, and their respective click event handlers.
   */
  @Override
  // @SuppressWarnings("unchecked")
  public void initialize(URL location, ResourceBundle resources) {

    gameLoungeList.setItems(client.getGameLounge().getList());

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

  /**
   * 
   * Switches the GUI to the main page of the specified game. If returnToGame is
   * true, it switches back to the previous game played.
   * 
   * @param gameId       the ID of the game to switch to
   * @param returnToGame true if switching back to the previous game played, false
   *                     otherwise
   * @throws Exception if there is an error switching to the main page
   */
  private void switchToGameMainPage(int gameId, boolean returnToGame) throws Exception {

    if (returnToGame) {
      Scene scene = client.getUIGameById(gameId).getScene();
      client.getStage().setScene(scene);
      client.getStage().setTitle("Main Page");
      client.getStage().show();
      return;
    }

    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    UIGame game = client.getUIGameById(gameId);

    MainPageController mainPageController = game.getMainPageController();

    mainPageController.setGameLounge(gameLounge);
    mainPageController.setUiGame(game);
    //// game.setMainPageController(mainPageController);

    controllers.put(MainPageController.class, mainPageController);

    // setting the UIGame instance in the UnitPlacementController
    // UnitPlacementController unitPlacementController =
    // game.getUnitPlacementController();

    // controllers.put(UnitPlacementController.class, unitPlacementController);

    switchToPage("/ui/mainPage.xml", "/ui/buttonstyle.css", controllers, "Main Page", client.getStage());

    // FXMLLoader loader = new
    // FXMLLoader(getClass().getResource("ui/mainPage.xml"));

    // Parent root = loader.load();
    // MainPageController controller = loader.getController();
  }

}
