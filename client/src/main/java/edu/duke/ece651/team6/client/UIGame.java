package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.duke.ece651.team6.client.controller.MainPageController;
import edu.duke.ece651.team6.client.model.GameLounge;
import edu.duke.ece651.team6.client.model.OrdersHandler;
import edu.duke.ece651.team6.client.view.MapView;
import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.Constants;
import edu.duke.ece651.team6.shared.Constants.GAME_STATUS;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PolygonGetter;
import edu.duke.ece651.team6.shared.Result;
import edu.duke.ece651.team6.shared.Territory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

/**
 * The UIGame class represents one game with a user interface, which is
 * responsible for handling the game's state, user interactions, and
 * communication with the server. It has a SocketHandler object to manage the
 * communication with the server through a socket.
 */
public class UIGame extends Game {

  private MainPageController mainPageController;

  int gameId; // unique for games that are of each player

  Integer playerId = -1;

  String username;

  Scene scene;

  GAME_STATUS gameStatus;

  GameBasicSetting setting;

  Map<String, Integer> resource;

  GameLounge gameLounge;

  PolygonGetter polygonGetter;

  Map<Integer, Color> playerColor;

  ArrayList<Color> colors;

  Map<Territory, Map<String, String>> previouslySeenTerritories;

  /**
   * This method sets the scene of the game. It takes a Scene object as a
   * parameter and assigns it to the scene field.
   *
   * @param scene
   */
  public void setScene(Scene scene) {
    this.scene = scene;
  }

  /**
   * This method returns the current Scene object of the game.
   */
  public Scene getScene() {
    return this.scene;
  }

  /**
   * This method returns the current GAME_STATUS object of the game.
   */
  public GAME_STATUS getStatus() {
    return this.gameStatus;
  }

  /**
   * This method returns the gameId of the game.
   */
  public int getGameId() {
    return this.gameId;
  }

  /**
   * This method sets the GameLounge object for the current game instance. The
   * GameLounge object manages the players in the game.
   *
   * @param gameLounge The GameLounge object to set for the current game instance.
   */
  public void setGameLounge(GameLounge gameLounge) {
    this.gameLounge = gameLounge;
  }

  /**
   * This method returns the MainPageController object that manages the user
   * interface for the game.
   *
   * @return The MainPageController object that manages the user interface for the
   *         game.
   */
  public MainPageController getMainPageController() {
    return this.mainPageController;
  }

  /**
   * This method sets the MainPageController object for the current game instance.
   * The MainPageController object manages the user interface for the game.
   *
   * @param mainPageController The MainPageController object to set for the
   *                           current game instance.
   */
  public void setMainPageController(MainPageController mainPageController) {
    this.mainPageController = mainPageController;
  }

  OrdersHandler ordersHandler;

  /**
   * Constructor: UIGame(int gameId, String username, SocketHandler socketHandler,
   * MainPageController mainPageController) This constructor initializes a new
   * instance of the UIGame class.
   *
   * Parameters
   *
   * @param gameId             gameId: The unique identifier for the game session.
   * @param username           username: The username of the player.
   * @param socketHandler      socketHandler: An instance of the SocketHandler
   *                           class to manage communication with the server.
   * @param mainPageController mainPageController: The controller for the main
   *                           page of the application, which manages the user
   *                           interface for the game.
   */
  public UIGame(int gameId, String username, SocketHandler socketHandler, MainPageController mainPageController) {
    super(socketHandler);
    this.gameId = gameId;
    this.username = username;
    this.gameStatus = GAME_STATUS.WAIT_OTHER_PLAYERS_TO_ENTER;
    this.resource = new HashMap<>();
    this.mainPageController = mainPageController;
    this.mainPageController.setUiGame(this);

    this.polygonGetter = new PolygonGetter();
    this.playerColor = new HashMap<>();
    this.colors = new ArrayList<>(Arrays.asList(Color.AQUAMARINE, Color.AZURE, Color.VIOLET, Color.LIGHTCORAL));

    this.ordersHandler = new OrdersHandler(this.mainPageController);
    this.previouslySeenTerritories = new HashMap<>();
  }

  /**
   * This method closes the socket connection used by the game. Throws an
   * IOException if an I/O error occurs while closing the socket.
   *
   * @throws IOException If an I/O error occurs while closing the socket.
   */
  public void exit() throws IOException {
    socketHandler.socket.close();
  }

  /**
   * This method updates the game status of the current game instance.
   *
   * @param gameStatus The updated game status to set for the current game
   *                   instance.
   */
  public void updateGameStatus(GAME_STATUS gameStatus) {
    this.gameStatus = gameStatus;
  }

  /* ------------------------------------------------------------ */
  /* ----------------- Unit Placement UI ------------------- */
  /* ------------------------------------------------------------ */

  private final Map<Territory, Integer> territoryToNumUnitMapping = new HashMap<>();
  private Iterator<Territory> territoryIterator;
  private Territory currentTerritory;

  List<Integer> constructIntegersFrom0UpTo(int maxInteger) {
    List<Integer> intList = new ArrayList<>();
    for (int i = 0; i <= maxInteger; i++) {
      intList.add(i);
    }
    return intList;
  }

  /**
   * This method prompts the user to place units on territories in the game, and
   * updates the unit placement settings. It throws IOException,
   * ClassNotFoundException, InterruptedException, and ExecutionException.
   *
   * @throws IOException            If an I/O error occurs while communicating
   *                                with the server.
   * @throws ClassNotFoundException If the class of a serialized object could not
   *                                be found.
   * @throws InterruptedException   If a thread is interrupted while waiting for
   *                                an operation to complete.
   * @throws ExecutionException     If an exception occurs while attempting to
   *                                execute a Callable task.
   */
  public void nextUnitPlacementPrompt()
      throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
    if (territoryIterator.hasNext()) {
      System.out.println("nextUnitPlacementPrompt");
      currentTerritory = territoryIterator.next();

      if (!territoryIterator.hasNext()) { // Check if the current territory is the last territory
        territoryToNumUnitMapping.put(currentTerritory, setting.getRemainingNumUnits());

        Platform.runLater(() -> {

          mainPageController.showSuccess("The remaining " + setting.getRemainingNumUnits()
              + " units have been automatically placed onto territory " + currentTerritory.getName());
        });

        // Finish the placement phase and send the updated data to the server
      } else {
        int maxAvailUnits = setting.getRemainingNumUnits();

        List<Integer> remainingUnits = constructIntegersFrom0UpTo(maxAvailUnits);

        Integer numUnits = null;
        while (numUnits == null) {

          numUnits = showSelectionDialog(remainingUnits, "No items available for selection.", "Unit Placement",
              "Player " + this.username + ", how many units do you want to place on the " + currentTerritory.getName()
                  + " territory? (" + setting.getRemainingNumUnits() + " remaining)").get();

          if (numUnits == null) {
            Platform.runLater(() -> {
              mainPageController.showError("Must specify unit number to place on " + currentTerritory.getName());
            });
          }

        }
        try {
          setting.decreaseUnitsBy(numUnits);
        } catch (IllegalArgumentException e) {

          System.out.println("*** added runLater");
          Platform.runLater(() -> {
            mainPageController.showError(e.getMessage());
          });

          e.printStackTrace();
        }
        territoryToNumUnitMapping.put(currentTerritory, numUnits);
      }
    } else {
      // All territories have been handled, finish the placement phase and send the
      // updated data to the server
      setting.initializeUnitPlacement(territoryToNumUnitMapping);
      // send back the GameBasicSetting object with the updated unit placement info.
      socketHandler.sendUpdatedGameBasicSetting(setting);
      this.gameStatus = GAME_STATUS.ISSUE_ORDER;

      Platform.runLater(() -> {
        mainPageController.updateGameStatus(gameStatus);
      });
    }
  }

  /* ------------------------------------------------------------ */
  /* ----------------- Entry Point ------------------- */
  /* ------------------------------------------------------------ */

  /**
   * This method receives initial Territory information from the server And
   * display UI to the user so that user interact to input units information
   */
  public void entryPoint() throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
    System.out.println("Game status: " + gameStatus);
    if (this.gameStatus == GAME_STATUS.WAIT_OTHER_PLAYERS_TO_ENTER) {
      System.out.print("Waiting for other players to enter\n");
      String initMsg = (String) socketHandler.recvObject();
      System.out.println(initMsg);
      if (!"Connected to the server!".equals(initMsg)) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Platform.runLater(() -> {
          Alert connectedToServer = new Alert(Alert.AlertType.INFORMATION);
          connectedToServer.setTitle("New message arrives");
          connectedToServer.setHeaderText("Successfully returned to a game!");
          connectedToServer.setContentText(
                  "Alert: server was powered off.. The commit you made may be lost");
          Optional<?> result = connectedToServer.showAndWait();
          if (result.isPresent()) {
            future.complete(true);
          }
        });
        future.get();
      }
      if (initMsg.charAt(0) == 'C') {
        // first connection or reconnect to place units phase
        setting = socketHandler.recvGameBasicSetting();
        this.startingGameMap = setting.getGameMap();
        this.gameStatus = GAME_STATUS.PLACE_UNITS;
        Platform.runLater(() -> {
          mainPageController.setUsername(username + " (PlayerId " + setting.getPlayerId() + ")");
          mainPageController.updateGameStatus(this.gameStatus);
        });
        this.playerId = setting.getPlayerId();

        // this.startingGameMap = setting.getGameMap();
        // this.gameStatus = GAME_STATUS.PLACE_UNITS;
        // Platform.runLater(() -> {
        // mainPageController.setUsername(username + " (PlayerId " +
        // setting.getPlayerId() + ")");
        // mainPageController.updateGameStatus(this.gameStatus);
        // });

        Set<Territory> territories = setting.getAssignedTerritories();

        // Platform.runLater(() -> updateMap(mainPageController.getMapPane(),
        // setting.getGameMap().getTerritorySet()));
        renderingMap(setting.getGameMap());

        territoryIterator = territories.iterator();

        while (gameStatus == GAME_STATUS.PLACE_UNITS) {
          nextUnitPlacementPrompt();
        }
      } else {
        // reconnect to issue_order phase
        gameStatus = GAME_STATUS.ISSUE_ORDER;
        Platform.runLater(() -> {
          mainPageController.updateGameStatus(gameStatus);
        });

      }
    }
    refreshMap();
  }

  /* ------------------------------------------------------------ */
  /* -------- Receive Game Result from server -------- */
  /* ------------------------------------------------------------ */

  /**
   * This method receives the game result from the server and handles it. It
   * throws IOException and ClassNotFoundException.
   *
   * @return A string constant representing the game status - either GAME_OVER or
   *         GAME_LOST.
   * @throws IOException            If an I/O error occurs while communicating
   *                                with the server.
   * @throws ClassNotFoundException If the class of a serialized object could not
   *                                be found.
   */
  private String receiveGameResult() throws IOException, ClassNotFoundException {
    Result result = this.socketHandler.recvGameResult();

    if (result.getWinners().size() > 0) {
      this.gameStatus = GAME_STATUS.GAME_OVER;

      System.out.println("*** added runLater");
      Platform.runLater(() -> {
        mainPageController.updateGameStatus(gameStatus);
      });

      System.out.println("Player " + result.getWinners().toString() + " wins!");

      // Show an Alert with the game result
      System.out.println("*** added runLater");
      Platform.runLater(() -> {
        Alert gameOverAlert = new Alert(Alert.AlertType.INFORMATION);
        gameOverAlert.setTitle("Game Over");
        gameOverAlert.setHeaderText("The game has ended.");

        if (result.getWinners().contains(this.playerId)) {
          gameOverAlert.setContentText("Congratulations! You have won the game.");
        } else {
          gameOverAlert.setContentText("Player " + result.getWinners().toString() + " won the game.");
        }
        gameOverAlert.showAndWait();
      });

      return Constants.GAME_OVER;
    }

    if (this.hasLost) {
      refreshMap();
      return Constants.GAME_LOST;
    }

    if (result.getLosers().contains(this.playerId)) {
      this.hasLost = true;
      this.gameStatus = GAME_STATUS.GAME_OVER;
      Platform.runLater(() -> {
        mainPageController.updateGameStatus(gameStatus);
      });

      return Constants.GAME_LOST;
    }
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    return null;
  }

  /* ------------------------------------------------------------ */
  /* --------- Send Commit (Orders) to server --------- */
  /* ------------------------------------------------------------ */

  /**
   * This method submits the current commit to the server after showing a
   * confirmation dialog to the user. It updates the game status and disables the
   * play turns buttons on the UI while waiting for the result from the server. It
   * throws IOException and ClassNotFoundException.
   *
   * @throws IOException            If an I/O error occurs while communicating
   *                                with the server.
   * @throws ClassNotFoundException If the class of a serialized object could not
   *                                be found.
   */
  public void submitCommit() throws ExecutionException, InterruptedException {
    /* -------- Show a confirmation dialog with the orders-------- */
    String ordersString = this.currentCommit.toString();
    CompletableFuture<Boolean> future = new CompletableFuture<>();
    Platform.runLater(() -> {
      Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);

      confirmationDialog.setTitle("Confirm Orders");
      confirmationDialog.setHeaderText("Please confirm your orders:");
      // confirmationDialog.setContentText(ordersString);

      TextArea textArea = new TextArea();
      textArea.setEditable(false);
      textArea.setWrapText(true);
      textArea.setText(ordersString);

      ScrollPane scrollPane = new ScrollPane(textArea);
      scrollPane.setFitToWidth(true);
      scrollPane.setFitToHeight(true);
      scrollPane.setPrefSize(500, 300); // Set the size of the scroll pane

      confirmationDialog.getDialogPane().setContent(scrollPane);

      Optional<ButtonType> confirmOrder = confirmationDialog.showAndWait();

      if (confirmOrder.isPresent() && confirmOrder.get() == ButtonType.OK) {
        future.complete(true);
      } else {
        future.complete(false);
      }
    });
    if (future.get().equals(false)) {
      return;
    }
    try {
      socketHandler.sendCommit(currentCommit);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Successfully sent a commit to the server");
    updateGameStatus(GAME_STATUS.WAITING_FOR_RESULT);
    Platform.runLater(() -> {
      mainPageController.updateGameStatus(GAME_STATUS.WAITING_FOR_RESULT);
      mainPageController.setPlayTurnsButtonsDisabled(true);
    });
    try {
      // socketHandler.recvGameResult();
      String status = receiveGameResult();
      Platform.runLater(() -> {
        mainPageController.updateGameStatus(gameStatus);
      });
      System.out.println("Successfully received game result");

      if (Constants.GAME_OVER.equals(status)) {
        return;
      }
      refreshMap();
      System.out.println("Successfully refreshed map");
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      mainPageController.showError(e.getMessage());
    }
  }

  /**
   * This method receives a updated GameMap from the server and display the new
   * scene to the user
   */
  public void refreshMap() throws IOException, ClassNotFoundException {
    System.out.println("Wait for GlobalMapInfo");
    GlobalMapInfo mapInfo = socketHandler.recvGlobalMapInfo();
    System.out.println("Mapinfo got");
    if (mapInfo.playerId != -1) {
      this.playerId = mapInfo.playerId;
    }
    Platform.runLater(() -> {
      mainPageController.setUsername(username + " (PlayerId " + this.playerId + ")");
    });

    // TODO not sure if the commit will be null if reconnecting?
    renderingMap(mapInfo.getGameMap());

    // TODO Platform.runLater(() -> updateMap(mainPageController.getMapPane(),
    // mapInfo.getGameMap().getTerritorySet()));
    // this.thisView = new UIGameView(mapInfo);
    // this.gameMap = mapInfo.getGameMap();
    this.startingGameMap = mapInfo.getGameMap();

    initiateCommit();

    System.out.println("GameMap - UIGame.java" + this.currentCommit.getCurrentGameMap().toString());

    // Platform.runLater(
    // () -> updateMap(mainPageController.getMapPane(),
    // this.currentCommit.getCurrentGameMap().getTerritorySet()));
    renderingMap(this.currentCommit.getCurrentGameMap());

    this.resource.put(Constants.RESOURCE_FOOD,
        this.currentCommit.getCurrentGameMap().getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_FOOD));
    this.resource.put(Constants.RESOURCE_TECH,
        this.currentCommit.getCurrentGameMap().getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_TECH));

    Platform.runLater(() -> {
      mainPageController.updateTechLevel(this.currentCommit.getCurrentGameMap().getMaxTechLevel(playerId));
      mainPageController.updateFoodResources(
          this.currentCommit.getCurrentGameMap().getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_FOOD));
      mainPageController.updateTechResources(
          this.currentCommit.getCurrentGameMap().getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_TECH));

      mainPageController.setPlayTurnsButtonsDisabled(false);
    });

    if (this.hasLost) {

      Platform.runLater(() -> {
        mainPageController.setPlayTurnsButtonsDisabled(true);
      });
      receiveGameResult();
      return;
    }
  }

  /* ------------------------------------------------------------ */
  /* -------------- Order Constructions ------------------- */
  /* ------------------------------------------------------------ */

  private void prepWorkBeforeNewOrder() {
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    if (currentCommit == null) {
      initiateCommit();
    }
  }

  public void constructMoveOrder() throws IOException, InterruptedException, ExecutionException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleMoveOrder(currentCommit, playerId);
  }

  public void constructAttackOrder() throws IOException, InterruptedException, ExecutionException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleAttackOrder(currentCommit, playerId);

  }

  public void constructResearchOrder() {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleResearchOrder(currentCommit, playerId);
  }

  public void constructUpgradeOrder() throws IOException, InterruptedException, ExecutionException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleUpgradeOrder(currentCommit, playerId);
  }

  public void constructCloakTerritoryOrder() throws IOException, InterruptedException, ExecutionException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleCloakOrder(currentCommit, playerId);
  }

  public void constructGenerateSpyOrder() throws IOException, InterruptedException, ExecutionException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleGenerateSpyOrder(currentCommit, playerId);
  }

  public void constructMoveSpyOrder() throws IOException, InterruptedException, ExecutionException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleMoveSpyOrder(currentCommit, playerId);
  }

  public void constructSanBingOrder() throws ExecutionException, InterruptedException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleSanBingOrder(currentCommit, playerId);
  }

  public void constructSuperShieldOrder() throws ExecutionException, InterruptedException {
    prepWorkBeforeNewOrder();

    this.ordersHandler.handleSuperShieldOrder(currentCommit, playerId);
  }

  /**
   *
   * Shows a selection dialog with the specified list of options. If the list is
   * empty, an error message is shown to the user. The user's selection is
   * returned as a CompletableFuture that completes when the user closes the
   * dialog. The dialog runs on the JavaFX application thread to avoid blocking
   * the main thread.
   *
   * @param list              the list of options to display in the dialog
   * @param promptIfEmptyList the error message to display if the list is empty
   * @param title             the title of the dialog window
   * @param contentText       the text to display in the dialog
   * @param <T>               the type of the options in the list
   * @return a CompletableFuture that completes with the user's selection from the
   *         sql Copy code dialog, or null if the user cancels the dialog
   */
  private <T> CompletableFuture<T> showSelectionDialog(List<T> list, String promptIfEmptyList, String title,
      String contentText) {
    CompletableFuture<T> future = new CompletableFuture<>();
    if (list.isEmpty()) {
      mainPageController.showError(promptIfEmptyList);
      future.complete(null);
      return future;
    }
    Platform.runLater(() -> {
      ChoiceDialog<T> dialog = new ChoiceDialog<>(list.get(0), list);
      dialog.setTitle(title);
      dialog.setHeaderText(null);
      dialog.setContentText(contentText);

      Optional<T> result = dialog.showAndWait();

      if (result.isPresent()) {
        future.complete(result.get());
      } else {
        future.complete(null);
      }
    });

    return future;
  }

  private Commit currentCommit = null;
  private GameMap startingGameMap = null;

  /**
   *
   * Initiates a new commit for the current player by creating a new Commit object
   * with a clone of the current game map and a copy of the player's resources.
   */
  public void initiateCommit() {
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    Map<String, Integer> copiedResource = new HashMap<>();
    copiedResource.putAll(this.resource);
    System.out.println("Initiating Commit");
    currentCommit = new Commit(this.playerId, (GameMap) this.startingGameMap.clone(), copiedResource);

    // Platform.runLater(
    // () -> updateMap(mainPageController.getMapPane(),
    // this.currentCommit.getCurrentGameMap().getTerritorySet()));
    renderingMap(this.currentCommit.getCurrentGameMap());
  }

  private void renderingMap(GameMap gameMap) {
    MapView mapView = new MapView(mainPageController, gameMap, this.playerId, this.polygonGetter, this.playerColor,
        this.colors, this.previouslySeenTerritories);
    Platform.runLater(() -> {
      mapView.refresh();
    });
  }
}
