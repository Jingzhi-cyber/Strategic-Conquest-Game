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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainPageController extends Controller implements Initializable {
  // @FXML
  // Label username;

  // private String user;

  // public void setUser(String user) {
  // this.user = user;
  // }
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

  @FXML
  public void resetOrders() {
    uiGame.initiateCommit();
  }

  public void setUiGame(UIGame uiGame) {
    this.uiGame = uiGame;
  }

  public Scene getScene() {
    return this.mainPageScene;
  }

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
      default:
        // Handle null / unexpected input - impossible
    }
  }

  @FXML
  public void submitOrders() {
    try {
      uiGame.submitCommit();
    } catch (IOException | ClassNotFoundException e) {
      showError(e.getMessage());
    }
  }

  public void setGameLounge(GameLounge gameLounge) {
    this.gameLounge = gameLounge;
  }

  public void setOrderMenuDisabled(boolean disable) {
    orderMenu.setDisable(disable);
  }

  public void setSubmitOrderDisabled(boolean disable) {
    submitOrdersButton.setDisable(disable);
  }

  public void setPlayTurnsButtonsDisabled(boolean disable) {
    this.resetCommitButton.setDisable(disable);
    this.placeOrderButton.setDisable(disable);
    this.submitOrdersButton.setDisable(disable);
    this.orderMenu.setDisable(disable);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // this.mainPageScene = resetCommitButton.getScene();

    ObservableList<String> items = FXCollections.observableArrayList("Move", "Attack", "Research", "Upgrade");
    orderMenu.setItems(items);
    orderMenu.setValue("Move");

    if (uiGame.getStatus() != GAME_STATUS.ISSUE_ORDER) {
      // orderMenu.setDisable(true);
    }

    techResourcesLabel.setText("Tech resources: 0 (units)");
    foodResourcesLabel.setText("Food resources: 0 (units)");
    techLevelLabel.setText("Maximum tech level: 0");
  }

  public MainPageController(Client client) {
    super(client);
    // this.username.setText(userName);
  }

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

  @FXML
  public void logOut(ActionEvent ae) {
    try {
      logOut();
    } catch (Exception e) {
      showError(e.getMessage());
    }
  }

  @FXML
  public void exit(ActionEvent ae) {
    try {
      uiGame.exit();
      // ...
      HashMap<Class<?>, Object> controllers = new HashMap<Class<?>, Object>();
      // System.out.print(this.usernameField.getText());
      controllers.put(GameLoungeController.class, client.getGameLoungeController());
      switchToPage("/ui/game-lounge-page.xml", "/ui/buttonstyle.css", controllers, "Game Lounge", client.getStage());

    } catch (Exception e) {
      showError(e.getMessage());
    }
  }

  public void updateTechResources(int techResources) {
    this.techResourcesLabel.setText("Tech resources: " + techResources + " (units)");
  }

  public void updateFoodResources(int foodResources) {
    this.foodResourcesLabel.setText("Food resources: " + foodResources + " (units)");
  }

  public void updateTechLevel(int techLevel) {
    this.techLevelLabel.setText("Maximum tech level: " + techLevel);
  }

  public void updateGameStatus(GAME_STATUS gameStatus) {
    this.gameStatusLabel.setText("Game status: " + gameStatus);
  }

  public void updateGameMap(GameMap gameMap) {
    // TODO: Update the UI to display the gameMap
  }

  public void setUsername(String username) {
    this.usernameLabel.setText(username);
  }
}
