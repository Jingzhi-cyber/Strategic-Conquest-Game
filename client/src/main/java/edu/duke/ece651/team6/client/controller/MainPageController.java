/**
The MainPageController class manages the user interface for the main game
page. It initializes the main page GUI, provides event handlers for the
page's buttons and choice box, and updates the display with relevant game
information. It also allows the user to reset orders that they have committed
to, place orders for their units, and submit the orders they have placed.
Lastly, it enables the user to return to the game lounge, log out of the
application, or exit the current game.
*/
package edu.duke.ece651.team6.client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import edu.duke.ece651.team6.client.Client;
import edu.duke.ece651.team6.client.UIGame;
import edu.duke.ece651.team6.client.model.GameLounge;
import edu.duke.ece651.team6.shared.Constants.GAME_STATUS;
import edu.duke.ece651.team6.shared.GameMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * 
 * The MainPageController class manages the user interface for the main game
 * page.
 */
public class MainPageController extends Controller implements Initializable {

  @FXML
  Button returnToGameLounge;

  @FXML
  Button logout;

  @FXML
  Label usernameLabel;

  @FXML
  Label techResourcesLabel;

  @FXML
  Label foodResourcesLabel;

  @FXML
  Label techLevelLabel;

  @FXML
  Label gameStatusLabel;

  @FXML
  AnchorPane mapPane;

  @FXML
  HBox territoryInfoPane;

  @FXML
  Text territoryInfoText1;

  @FXML
  Text territoryInfoText2;

  @FXML
  Text territoryInfoText3;

  private GameLounge gameLounge;

  private UIGame uiGame;

  private Scene mainPageScene;

  @FXML
  Button resetCommitButton;

  @FXML
  Button placeOrderButton;

  @FXML
  Button submitOrdersButton;

  @FXML
  ChoiceBox<String> orderMenu;

  /**
   * 
   * Resets the orders that the user has committed to.
   */
  @FXML
  public void resetOrders() {
    uiGame.initiateCommit();
  }

  /**
   * 
   * Sets the UIGame object for the MainPageController.
   * 
   * @param uiGame - the UIGame object for the MainPageController
   */
  public void setUiGame(UIGame uiGame) {
    this.uiGame = uiGame;
  }

  /**
   * 
   * Returns the main page Scene.
   * 
   * @return the main page Scene
   */
  public Scene getScene() {
    return this.mainPageScene;
  }

  /**
   * 
   * Returns the AnchorPane for the map.
   * 
   * @return the AnchorPane for the map
   */
  @FXML
  public AnchorPane getMapPane() {
    return this.mapPane;
  }

  /**
   * 
   * Returns the Text object for the first line of territory information.
   * 
   * @return the Text object for the first line of territory information
   */
  @FXML
  public Text getTerritoryInfoText1() {
    return this.territoryInfoText1;
  }

  /**
   * 
   * Returns the Text object for the second line of territory information.
   * 
   * @return the Text object for the second line of territory information
   */
  @FXML
  public Text getTerritoryInfoText2() {
    return this.territoryInfoText2;
  }

  /**
   * 
   * Returns the Text object for the second line of territory information.
   * 
   * @return the Text object for the second line of territory information
   */
  @FXML
  public Text getTerritoryInfoText3() {
    return this.territoryInfoText3;
  }

  /**
   * 
   * Places an order based on the currently selected order from the orderMenu.
   * Calls the corresponding method in the uiGame object to construct the order.
   * Handles exceptions thrown by the order construction methods.
   * 
   * @throws IOException          if there is an IO error during order
   *                              construction
   * @throws InterruptedException if the order construction is interrupted
   * @throws ExecutionException   if the order construction encounters an
   *                              execution error
   */

  @FXML
  public void placeAnOrder() throws IOException, InterruptedException, ExecutionException {
    String selectedOrder = orderMenu.getValue();
    switch (selectedOrder) {
      case "Move":
        try {
          uiGame.constructMoveOrder();
        } catch (IOException e) {
          e.printStackTrace();
        }

        break;
      case "Attack":
        try {
          uiGame.constructAttackOrder();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      case "Research":
        uiGame.constructResearchOrder();
        break;
      case "Upgrade":
        try {
          uiGame.constructUpgradeOrder();
        } catch (IOException | InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        break;
      case "Cloak":
        uiGame.constructCloakTerritoryOrder();
        break;
      case "Generate Spies":
        try {
          uiGame.constructGenerateSpyOrder();
        } catch (IOException | InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        break;
      case "Move Spies":
        try {
          uiGame.constructMoveSpyOrder();
        } catch (IOException | InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        break;
      case "San Bing":
        try {
          uiGame.constructSanBingOrder();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        break;
      case "Super Shield":
        try {
          uiGame.constructSuperShieldOrder();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        break;
      default:
        // Handle null / unexpected input - impossible
    }
  }

  /**
   * 
   * The submitOrders method submits the orders that the user has committed to in
   * the current turn. It calls the uiGame.submitCommit() method to submit the
   * orders.
   */
  @FXML
  public void submitOrders() {
    new Thread(new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        uiGame.submitCommit();
        return null;
      }
    }).start();
  }

  /**
   * 
   * The setGameLounge method sets the GameLounge for the MainPageController.
   * 
   * @param gameLounge the GameLounge to set
   */
  public void setGameLounge(GameLounge gameLounge) {
    this.gameLounge = gameLounge;
  }

  /**
   * 
   * The setOrderMenuDisabled method sets the disable property of the orderMenu
   * ChoiceBox.
   * 
   * @param disable a boolean indicating whether to disable the orderMenu or not
   */
  public void setOrderMenuDisabled(boolean disable) {
    orderMenu.setDisable(disable);
  }

  /**
   * 
   * The setSubmitOrderDisabled method sets the disable property of the
   * submitOrdersButton.
   * 
   * @param disable a boolean indicating whether to disable the submitOrdersButton
   *                or not
   */
  public void setSubmitOrderDisabled(boolean disable) {
    submitOrdersButton.setDisable(disable);
  }

  /**
   * 
   * Sets the state of the play turn buttons (reset commit, place order, submit
   * orders, order menu) to be enabled or disabled based on the given boolean
   * value.
   * 
   * @param disable a boolean value indicating whether to disable the play turn
   *                buttons or not
   */
  public void setPlayTurnsButtonsDisabled(boolean disable) {
    this.resetCommitButton.setDisable(disable);
    this.placeOrderButton.setDisable(disable);
    this.submitOrdersButton.setDisable(disable);
    this.orderMenu.setDisable(disable);
  }

  /**
   * 
   * Initializes the MainPageController GUI by setting up the order menu and
   * disabling play turns buttons if the game status is not ISSUE_ORDER. It also
   * sets the initial values of the tech resources, food resources, and maximum
   * tech level labels to zero.
   * 
   * @param location  The location of the FXML file.
   * @param resources The resources for the controller.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // this.mainPageScene = resetCommitButton.getScene();

    ObservableList<String> items = FXCollections.observableArrayList("Move", "Attack", "Research", "Upgrade", "Cloak",
        "Generate Spies", "Move Spies", "San Bing", "Super Shield");
    orderMenu.setItems(items);
    orderMenu.setValue("Move");

    if (uiGame.getStatus() != GAME_STATUS.ISSUE_ORDER) {
      setPlayTurnsButtonsDisabled(true);
    }

    techResourcesLabel.setText("Tech resources: 0 (units)");
    foodResourcesLabel.setText("Food resources: 0 (units)");
    techLevelLabel.setText("Maximum tech level: 0");
  }

  /**
   * This is a constructor for the MainPageController class that takes in a Client
   * object and calls the constructor of its superclass (Controller) with that
   * Client object as an argument using the super() keyword.
   *
   * @param client
   */
  public MainPageController(Client client) {
    super(client);
  }

  /**
   * 
   * Returns the user to the game lounge page when the "Return to Game Lounge"
   * button is clicked. Also refreshes the game lounge list.
   * 
   * @param ae The ActionEvent that triggers this method.
   * @throws Exception if an error occurs during the switch to the game lounge
   *                   page.
   */
  @FXML
  public void returnToGameLounge(ActionEvent ae) throws Exception {

    HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
    // System.out.print(this.usernameField.getText());
    // client.getGameLoungeController().gameLoungeList.refresh();
    // System.out.println("In mainPageController, Game Lounge List refreshed, size
    // is "
    // + client.getGameLoungeController().gameLoungeList.getItems().size());
    this.mainPageScene = resetCommitButton.getScene();
    uiGame.setScene(mainPageScene);

    controllers.put(GameLoungeController.class, client.getGameLoungeController());
    switchToPage("/ui/game-lounge-page.xml", "/ui/buttonstyle.css", controllers, "Game Lounge", client.getStage());
    // Update the gameLoungeList display

    // System.out.println("In mainPageController, gameLounge size " +
    // client.getGameLoungeController().gameLounge.size());

    System.out.println("In mainPageController, Game Lounge List , size is  "
        + client.getGameLoungeController().gameLoungeList.getItems().size());

    client.getGameLoungeController().gameLoungeList.refresh();
  }

  /**
   *
   * The logOut() method is called when the user clicks the "Log Out" button. It
   * attempts to log the user out and catch any exceptions that may occur. If an
   * exception is caught, it is displayed to the user as an error message using
   * the showError() method.
   * 
   * @param ae
   */
  @FXML
  public void logOut(ActionEvent ae) {
    try {
      logOut();
    } catch (Exception e) {
      showError(e.getMessage());
    }
  }

  /**
   * The exit() method is called when the user clicks the "Exit" button. It gets
   * the current game ID, calls the exit() method of the uiGame object, removes
   * the game from the gameLounge, creates a HashMap of controllers, and switches
   * to the game lounge page using the switchToPage() method. If an exception is
   * caught, it is displayed to the user as an error message using the showError()
   * method.
   * 
   * @param ae
   */
  @FXML
  public void exit(ActionEvent ae) {
    try {
      int gameId = uiGame.getGameId();
      uiGame.exit();

      // gameLounge.removeGame(gameId);
      GameLoungeController gameLoungeController = client.getGameLoungeController();
      gameLoungeController.gameLounge.removeGame(String.valueOf(gameId));
      // gameLoungeController.updateGameList(gameLounge);
      // ...
      HashMap<Class<?>, Object> controllers = new HashMap<>();
      // System.out.print(this.usernameField.getText());
      controllers.put(GameLoungeController.class, gameLoungeController);
      switchToPage("/ui/game-lounge-page.xml", "/ui/buttonstyle.css", controllers, "Game Lounge", client.getStage());

    } catch (Exception e) {
      showError(e.getMessage());
    }
  }

  /**
   * 
   * Updates the displayed tech resources value on the UI.
   * 
   * @param techResources the new tech resources value to be displayed.
   */
  public void updateTechResources(int techResources) {
    this.techResourcesLabel.setText("Tech resources: " + techResources + " (units)");
  }

  /**
   * 
   * Updates the displayed food resources value on the UI.
   * 
   * @param foodResources the new food resources value to be displayed.
   */
  public void updateFoodResources(int foodResources) {
    this.foodResourcesLabel.setText("Food resources: " + foodResources + " (units)");
  }

  /**
   * 
   * Updates the displayed maximum tech level value on the UI.
   * 
   * @param techLevel the new maximum tech level value to be displayed.
   */
  public void updateTechLevel(int techLevel) {
    this.techLevelLabel.setText("Maximum tech level: " + techLevel);
  }

  /**
   * 
   * Updates the displayed game status value on the UI.
   * 
   * @param gameStatus the new game status value to be displayed.
   */
  public void updateGameStatus(GAME_STATUS gameStatus) {
    this.gameStatusLabel.setText("Game status: " + gameStatus);
  }

  /**
   * 
   * Updates the displayed game map on the UI.
   * 
   * @param gameMap the new game map to be displayed.
   */
  public void updateGameMap(GameMap gameMap) {
    // TODO: Update the UI to display the gameMap
  }

  /**
   * This method sets the text of the usernameLabel to the specified username.
   */
  public void setUsername(String username) {
    this.usernameLabel.setText(username);
  }
}
