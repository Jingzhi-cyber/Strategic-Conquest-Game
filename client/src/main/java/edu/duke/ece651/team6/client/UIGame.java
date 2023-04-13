package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import edu.duke.ece651.team6.client.controller.MainPageController;
import edu.duke.ece651.team6.client.model.GameLounge;
import edu.duke.ece651.team6.shared.AttackOrder;
import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.Constants;
import edu.duke.ece651.team6.shared.Constants.GAME_STATUS;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.MoveOrder;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.PolygonGetter;
import edu.duke.ece651.team6.shared.ResearchOrder;
import edu.duke.ece651.team6.shared.Result;
import edu.duke.ece651.team6.shared.Territory;
import edu.duke.ece651.team6.shared.UpgradeOrder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Tooltip;

/**
 * The UIGame class represents one game with a user interface, which is
 * responsible for handling the game's state, user interactions, and
 * communication with the server. It has a SocketHandler object to manage the
 * communication with the server through a socket.
 */
public class UIGame extends Game {

  private MainPageController mainPageController;

  int gameId; // unique for games that are of each player

  Integer playerId;

  String username;

  Scene scene;

  GameMap gameMap;

  MapView thisView = null;

  GAME_STATUS gameStatus;

  GameBasicSetting setting;

  Map<String, Integer> resource;

  GameLounge gameLounge;

  PolygonGetter polygonGetter;

  Map<Integer, Color> playerColor;

  ArrayList<Color> colors;

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
    // Initialize the controller, e.g., by loading the FXML file
    // this.unitPlacementController = unitPlacementController;// TODO
    // this.unitPlacementController.setUIGame(this);
    this.mainPageController = mainPageController;
    this.mainPageController.setUiGame(this);
    this.polygonGetter = new PolygonGetter();
    this.playerColor = new HashMap<>();
    this.colors = new ArrayList<>(Arrays.asList(Color.AQUAMARINE, Color.AZURE, Color.VIOLET, Color.LIGHTCORAL));
    // this.mainPageController.setUsername(username);
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

  /* ----------1. Handling Unit Placement ----------- */
  private final Map<Territory, Integer> territoryToNumUnitMapping = new HashMap<>();
  private Iterator<Territory> territoryIterator;
  private Territory currentTerritory;

  List<Integer> constructIntegersFrom0UpTo(int maxInteger) {
    List<Integer> intList = new ArrayList<>();
    // TODO <=
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
                  + " territory? (" + setting.getRemainingNumUnits() + " remaining)")
              .get();

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

  /**
   * This method receives initial Territory information from the server And
   * display UI to the user so that user interact to input units information
   */
  public void entryPoint() throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
    System.out.println("Game status: " + gameStatus);
    if (this.gameStatus == GAME_STATUS.WAIT_OTHER_PLAYERS_TO_ENTER) {
      System.out.print("Waiting for other players to enter\n");

      System.out.println((String) socketHandler.recvObject());
      setting = socketHandler.recvGameBasicSetting();
      this.gameMap = setting.getGameMap();
      this.gameStatus = GAME_STATUS.PLACE_UNITS;
      Platform.runLater(() -> {
        mainPageController.setUsername(username + " (PlayerId " + setting.getPlayerId() + ")");
        mainPageController.updateGameStatus(this.gameStatus);
      });

      // territoryToNumUnitMapping = new HashMap<>();

      Set<Territory> territories = setting.getAssignedTerritories();

      Platform.runLater(() -> updateMap(mainPageController.getMapPane(), setting.getGameMap().getTerritorySet()));
      territoryIterator = territories.iterator();

      while (gameStatus == GAME_STATUS.PLACE_UNITS) {
        nextUnitPlacementPrompt();
      }
    }
    refreshMap();
  }

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
  public void submitCommit() {
    /* -------- Show a confirmation dialog with the orders-------- */
    String ordersString = this.currentCommit.toString();
    AtomicBoolean buttonOK = new AtomicBoolean(false);
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
        buttonOK.set(true);
      }
    });
    class MyTask extends Task<Void> {

      final UIGame game;

      MyTask(UIGame game) {
        this.game = game;
      }

      @Override
      protected Void call() throws Exception {
        while (!buttonOK.get()) {
          Thread.sleep(100);
        }
        try {
          game.socketHandler.sendCommit(game.currentCommit);
        } catch (Exception e) {
          e.printStackTrace();
        }
        System.out.println("Successfully sent a commit to the server");
        game.updateGameStatus(GAME_STATUS.WAITING_FOR_RESULT);
        Platform.runLater(() -> {
          game.mainPageController.updateGameStatus(GAME_STATUS.WAITING_FOR_RESULT);
          game.mainPageController.setPlayTurnsButtonsDisabled(true);
        });
        try {
          // socketHandler.recvGameResult();
          String status = game.receiveGameResult();
          Platform.runLater(() -> {
            game.mainPageController.updateGameStatus(game.gameStatus);
          });
          System.out.println("Successfully received game result");

          if (Constants.GAME_OVER.equals(status)) {
            return null;
          }
          game.refreshMap();
          System.out.println("Successfully refreshed map");
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
          game.mainPageController.showError(e.getMessage());
        }
        return null;
      }
    }
    new Thread(new MyTask(this)).start();
  }

  /**
   * Sets the fill color of a given polygon to the color associated with a given
   * owner ID.
   *
   * @param polygon The polygon whose fill color is to be set.
   * @param ownerID The ID of the owner associated with the color to be used for
   *                the polygon.
   */
  protected void setPolygonColor(Polygon polygon, int ownerID) {
    polygon.setFill(this.playerColor.get(ownerID));
  }

  /**
   * Performs a simple animation on a given polygon by briefly changing its fill
   * color to light gray and then changing it back to its original color.
   *
   * @param polygon The polygon to be animated.
   */
  protected void performPolygonAnimation(Polygon polygon) {
    Color color = (Color) polygon.getFill();
    polygon.setFill(Color.LIGHTGRAY);

    new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      polygon.setFill(color);
    }).start();
  }

  protected void dispalyTerritoryInfo(Text territoryInfoText1, Text territoryInfoText2, Text territoryInfoText3,
      Territory territory) {
    String info1 = "Territory: " + territory.getName() + "\nOwnerID: " + territory.getOwnerId() + " \n";
    String info2 = getNeighborDistance(territory);
    String info3 = getUnitsNumberByLevel(territory);
    territoryInfoText1.setText(info1);
    territoryInfoText2.setText(info2);
    territoryInfoText3.setText(info3);
  }

  /**
   * Sets a mouse click event on a given polygon that performs a polygon animation
   * when the polygon is clicked.
   *
   * @param polygon The polygon to set the mouse click event on.
   */
  protected void setPolygonMouseClick(Polygon polygon, Territory territory) {
    polygon.setOnMouseClicked((MouseEvent click_event) -> {
      performPolygonAnimation(polygon);
      dispalyTerritoryInfo(this.mainPageController.getTerritoryInfoText1(),
          this.mainPageController.getTerritoryInfoText2(), this.mainPageController.getTerritoryInfoText3(), territory);
    });
  }

  /**
   * Sets the text of a given polygon to a given string, or creates a new Text
   * object for the polygon if one does not exist.
   *
   * @param mapPane The map pane containing the polygon.
   * @param polygon The polygon to set the text on.
   * @param text    The text to set on the polygon.
   * @return The Text object that was created or modified.
   */
  protected Text setPolygonText(Pane mapPane, Polygon polygon, String text) {
    for (Node node : mapPane.getChildren()) {
      if (node.getId().equals(polygon.getId() + "Text")) {
        Text territoryInfo = (Text) node;
        territoryInfo.setText(text);
        return territoryInfo;
      }
    }
    Text territoryInfo = new Text(text);
    territoryInfo.setId(polygon.getId() + "Text");
    territoryInfo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
    territoryInfo.setFill(Color.BLACK);
    double centerX = polygon.getBoundsInLocal().getWidth() / 2;
    double centerY = polygon.getBoundsInLocal().getHeight() / 2;
    territoryInfo.setLayoutX(polygon.getLayoutX() + polygon.getBoundsInLocal().getMinX() + centerX
        - territoryInfo.getBoundsInLocal().getWidth() / 2);
    territoryInfo.setLayoutY(polygon.getLayoutY() + polygon.getBoundsInLocal().getMinY() + centerY
        + territoryInfo.getBoundsInLocal().getHeight() / 2);
    mapPane.getChildren().add(territoryInfo);
    return territoryInfo;
  }

  /**
   *
   * This method sets the tooltip of a given Polygon with the specified text.
   *
   * @param polygon The Polygon to set the tooltip for.
   * @param text    The text to set as the tooltip for the Polygon.
   */
  protected void setPolygonTooltip(Polygon polygon, String text) {
    Tooltip tooltip = new Tooltip(text);
    Font font = Font.font("Arial", 18);
    tooltip.setFont(font);
    Tooltip.install(polygon, tooltip);
  }

  protected String getNeighborDistance(Territory territory) {
    Map<Territory, Integer> distanceMap = this.gameMap.getNeighborDist(territory);
    String info = "Neighbor distance: \n";
    for (Territory currTerritory : distanceMap.keySet()) {
      info = info + currTerritory.getName() + " : " + distanceMap.get(currTerritory) + " \n";
    }
    return info;
  }

  protected String getUnitsNumberByLevel(Territory territory) {
    String info = "Units number by level: \n";
    for (int i = 0; i < territory.getNumLevels(); i++) {
      info = info + "Level " + i + ": " + territory.getUnitsNumByLevel(i) + " \n";
    }
    return info;
  }

  /**
   *
   * Sets the polygons representing the territories on the game map. For each
   * territory in the set, a polygon is created using the PolygonGetter object,
   * and the color, tooltip, mouse click event, and text of the polygon are set.
   * The polygon is then added to the mapPane along with its text representation.
   *
   * @param mapPane:     The pane representing the game map.
   * @param territories: A set of Territory objects representing the territories
   *                     on the game map.
   */
  protected void setMap(Pane mapPane, Set<Territory> territories) {
    for (Territory currTerritory : territories) {
      int ownerID = currTerritory.getOwnerId();
      if (!playerColor.containsKey(ownerID)) {
        this.playerColor.put(ownerID, this.colors.remove(0));
      }
      String name = currTerritory.getName();
      Polygon currPolygon = this.polygonGetter.getPolygon(currTerritory);
      currPolygon.setId(name);
      setPolygonColor(currPolygon, ownerID);
      setPolygonTooltip(currPolygon, "Territory: " + name + "\nOwnerID: " + ownerID + "\n"
          + getNeighborDistance(currTerritory) + getUnitsNumberByLevel(currTerritory));
      setPolygonMouseClick(currPolygon, currTerritory);
      Text polygonInfo = setPolygonText(mapPane, currPolygon, name + " - " + ownerID);
      mapPane.getChildren().add(currPolygon);
      polygonInfo.toFront();
    }

  }

  /**
   *
   * This method updates the game map UI by modifying the colors, text, and
   * tooltips of the polygons in the
   *
   * mapPane to reflect the current state of the game.
   *
   * @param mapPane     the Pane object representing the game map UI.
   *
   * @param territories a Set of Territory objects representing the territories in
   *                    the game.
   */
  protected void updateMap(Pane mapPane, Set<Territory> territories) {

    if (mapPane.getChildren().isEmpty()) {
      setMap(mapPane, territories);
    } else {
      for (Territory currTerritory : territories) {
        int ownerID = currTerritory.getOwnerId();
        String name = currTerritory.getName();
        for (Node node : mapPane.getChildren()) {
          if (node.getId() != null && node.getId().equals(name)) {
            Polygon polygon = (Polygon) node;
            setPolygonColor(polygon, ownerID);
            setPolygonMouseClick(polygon, currTerritory);
            setPolygonText(mapPane, polygon, name + " - " + ownerID);
            Tooltip.uninstall(polygon, null);
            setPolygonTooltip(polygon, "Territory: " + name + "\nOwnerID: " + ownerID + "\n"
                + getNeighborDistance(currTerritory) + getUnitsNumberByLevel(currTerritory));
          }
        }
      }
    }
  }

  /**
   * This method receives a updated GameMap from the server and display the new
   * scene to the user
   */
  public void refreshMap() throws IOException, ClassNotFoundException {

    Platform.runLater(() -> {
      mainPageController.setUsername(username + " (PlayerId " + setting.getPlayerId() + ")");
    });

    GlobalMapInfo mapInfo = socketHandler.recvGlobalMapInfo();
    System.out.println("Mapinfo got");
    if (mapInfo.playerId != -1) {
      this.playerId = mapInfo.playerId;
    }
    this.thisView = new UIGameView(mapInfo);
    this.gameMap = mapInfo.getGameMap();
    this.unChangedGameMap = mapInfo.getGameMap();
    System.out.println("GameMap - UIGame.java" + gameMap.toString());

    Platform.runLater(() -> updateMap(mainPageController.getMapPane(), this.gameMap.getTerritorySet()));

    this.resource.put(Constants.RESOURCE_FOOD,
        this.gameMap.getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_FOOD));
    this.resource.put(Constants.RESOURCE_TECH,
        this.gameMap.getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_TECH));

    Platform.runLater(() -> {
      mainPageController.updateTechLevel(gameMap.getMaxTechLevel(playerId));
      mainPageController
          .updateFoodResources(this.gameMap.getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_FOOD));
      mainPageController
          .updateTechResources(this.gameMap.getResourceByPlayerId(this.playerId).get(Constants.RESOURCE_TECH));

      mainPageController.setPlayTurnsButtonsDisabled(false);
    });

    if (this.hasLost) {

      Platform.runLater(() -> {
        mainPageController.setPlayTurnsButtonsDisabled(true);
      });
      receiveGameResult();
      return;
    }

    initiateCommit();

    // TODO Update Map!

    // thisView.updateScene();
    // TODO Map<String, Territory> territoriesMap;
    // populate the map with Territory objects

    // TODO SelectableTerritories selectableTerritories = new
    // SelectableTerritories(territoriesMap);

    // TODO display the globalMap on UI
  }

  public MoveOrder constructMoveOrder() throws IOException, InterruptedException, ExecutionException {
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    if (currentCommit == null) {
      initiateCommit();
    }

    Territory src = showTerritorySelectionDialog(null, true, "Move Order",
        "Which territory do you want to move units from?").get();

    if (src == null) {
      mainPageController.showError("Must specify a territory to move units from");
      return null; // User cancelled the dialog
    }

    Territory dest = showTerritorySelectionDialog(src, true, "Move Order",
        "Which territory do you want to move units to?").get();

    if (dest == null) {
      mainPageController.showError("Must specify a territory to move units to");
      return null; // User cancelled the dialog
    }

    int[] numUnitsByLevel = new int[Constants.MAX_LEVEL + 1];
    Integer selectedLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL, src, "Move Order",
        "Which level of units do you want to move?").get();
    if (selectedLevel == null) {
      mainPageController.showError("Must specify a level to move");
      return null; // User cancelled the dialog
    }
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedLevel, src, "How many of them do you want to move?")
        .get();

    if (numUnits == null) {
      mainPageController.showError("Must specify the number of units to move");
      return null; // User cancelled the dialog
    }

    numUnitsByLevel[selectedLevel] = numUnits;
    // MoveOrder move = null;
    MoveOrder move = new MoveOrder(src, dest, numUnitsByLevel);

    /* -------- Try adding the order -------- */
    try {
      this.currentCommit.addMove(move);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return null;
    }

    return move;
  }

  /**
   *
   * Constructs an attack order by prompting the player to select a territory to
   * attack from, a territory to attack, a unit level to use in the attack, and
   * the number of units to use in the attack. If the user cancels any of the
   * selection dialogs, this method returns null. Otherwise, it creates and
   * returns an AttackOrder object with the selected parameters and adds it to the
   * current commit.
   *
   * @return an AttackOrder object representing the player's attack order, or null
   *         if the user cancels the selection dialogs
   * @throws IOException          if there is an error in the socket connection
   * @throws InterruptedException if the thread is interrupted while waiting for
   *                              the user's input
   * @throws ExecutionException   if the future task fails to execute
   */
  public AttackOrder constructAttackOrder() throws IOException, InterruptedException, ExecutionException {
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    // TODO Auto-generated method stub
    if (currentCommit == null) {
      initiateCommit();
    }

    Territory src = showTerritorySelectionDialog(null, true, "Attack Order",
        "Which territory do you want to attack units from").get();

    if (src == null) {
      mainPageController.showError("Must specify a territory to attack from");
      return null; // User cancelled the dialog
    }

    Territory dest = showTerritorySelectionDialog(src, false, "Attack Order",
        "Which territory do you want to attack units to").get();

    if (dest == null) {
      mainPageController.showError("Must specify a territory to attack units to");
      return null; // User cancelled the dialog
    }

    int[] numUnitsByLevel = new int[Constants.MAX_LEVEL + 1];

    Integer selectedLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL, src, "Attack Order",
        "Which level of units do you want to attack?").get();
    if (selectedLevel == null) {
      mainPageController.showError("Must specify a level to attack");
      return null; // User cancelled the dialog
    }
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedLevel, src, "How many of them are used to attack")
        .get();

    if (numUnits == null) {
      mainPageController.showError("Must specify the number of units to attack");
      return null; // User cancelled the dialog
    }

    numUnitsByLevel[selectedLevel] = numUnits;

    // AttackOrder attack = null;
    AttackOrder attack = new AttackOrder(src, dest, numUnitsByLevel);
    try {
      this.currentCommit.addAttack(attack);
    } catch (IllegalArgumentException e) {

      // Platform.runLater(() -> {
      mainPageController.showError(e.getMessage());
      // });

      return null;
    }

    return attack;
  }

  /**
   *
   * Constructs a research order and adds it to the current commit. If the current
   * commit is null, this method first initiates the commit phase. If the research
   * order is successfully added, this method displays a success message to the
   * player. Otherwise, it displays an error message.
   *
   * @return a ResearchOrder object representing the player's research order, or
   *         null if the order cannot be added to sql Copy code the current commit
   */
  public ResearchOrder constructResearchOrder() {
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    if (currentCommit == null) {
      initiateCommit();
    }

    ResearchOrder research = new ResearchOrder(playerId);
    try {
      currentCommit.addResearch(research);
      mainPageController.showSuccess("Successfully added a research order");
    } catch (IllegalArgumentException e) {

      // Platform.runLater(() -> {
      mainPageController.showError(e.getMessage());
      // });

      return null;
    }

    return research;
  }

  /**
   **
   *
   * Constructs an UpgradeOrder object by getting input from the user via several
   * dialog boxes. The method shows a dialog to get the source territory from the
   * user. If the user cancels the dialog, the method returns null. The method
   * then shows a dialog to get the current unit level from the user, followed by
   * a dialog to get the target unit level, and finally a dialog to get the number
   * of units to upgrade. If the user cancels any of these dialogs, the method
   * returns null. If all inputs are obtained, the method creates an UpgradeOrder
   * object and adds it to the current commit phase. If an illegal argument is
   * thrown, an error message is displayed to the user and the method returns
   * null.
   *
   * @return the constructed UpgradeOrder object, or null if the user cancels any
   *         of the dialog boxes or if an illegal argument is thrown
   *
   * @throws IOException          if there is an error in the socket connection
   * @throws InterruptedException if the thread is interrupted while waiting for
   *                              user input
   * @throws ExecutionException   if an error occurs while waiting for user input
   */
  public UpgradeOrder constructUpgradeOrder() throws IOException, InterruptedException, ExecutionException {
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    if (currentCommit == null) {
      initiateCommit();
    }

    // Show a dialog to get the source territory from the user
    Territory src = showTerritorySelectionDialog(null, true, "On which territory do you want to upgrade units?",
        "Choose a territory").get(); // true/false
    // both okay if
    // src is null
    if (src == null) {
      mainPageController.showError("Must specify a territory to upgrade units");
      return null; // User cancelled the dialog
    }

    // Show a dialog to get the current unit level from the user
    Integer selectedNowLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL - 1, src, "Upgrade Order",
        "From which level do you want to upgrade units?").get();
    if (selectedNowLevel == null) {
      mainPageController.showError("Must specify a level to upgrade");
      return null; // User cancelled the dialog
    }

    // Show a dialog to get the target unit level from the user
    Integer selectedTargetLevel = showUnitLevelSelectionDialog(selectedNowLevel + 1, Constants.MAX_LEVEL, src,
        "Upgrade Order", "To which level do you want to upgrade units?").get();
    if (selectedTargetLevel == null) {
      mainPageController.showError("Must specify a target level");
      return null;
    }

    // Show a dialog to get the number of units to upgrade from the user
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedNowLevel, src,
        "How many of them do you want to upgrade?").get();
    if (numUnits == null) {
      mainPageController.showError("Must specify the number of units to upgrade");
      return null;
    }

    // Create and execute the upgrade order
    UpgradeOrder upgrade = new UpgradeOrder(src, selectedNowLevel, selectedTargetLevel, numUnits);

    try {
      this.currentCommit.addUpgrade(upgrade);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return null;
    }

    return upgrade;
  }

  /**
   *
   * Shows a dialog for the user to select a territory from a set of available
   * territories. The available territories are determined based on whether the
   * user is selecting a source territory for a move or an attack order. If the
   * user is selecting a source territory for a move order, only territories owned
   * by the player and with at least two units are available. If the user is
   * selecting a source territory for an attack order, only territories owned by
   * the player and with at least one unit are available. If the user is selecting
   * a destination territory for either order, only adjacent territories that are
   * not owned by the player are available.
   *
   * @param src     the source territory, or null if the user is selecting a
   *                destination territory
   * @param isMove  a boolean indicating whether the user is selecting a territory
   *                for a move order
   * @param title   the title of the selection dialog
   * @param context the context message displayed in the selection dialog
   * @return a CompletableFuture that completes with the selected territory or
   *         null if the user cancelled the dialog
   */
  private CompletableFuture<Territory> showTerritorySelectionDialog(Territory src, boolean isMove, String title,
      String context) {
    CompletableFuture<Territory> future = new CompletableFuture<>();

    // Platform.runLater(() -> {
    Set<Territory> territories = getTerritories(src, isMove);
    List<String> territoryNames = territories.stream().map(Territory::getName).collect(Collectors.toList());

    if (territories.isEmpty()) {
      mainPageController.showError(title + " Cannot " + " because there aren't available territories");
      future.complete(null);
    } else {
      ChoiceDialog<String> dialog = new ChoiceDialog<>(territoryNames.get(0), territoryNames);
      dialog.setTitle(title);
      dialog.setHeaderText(null);
      dialog.setContentText(context);

      Optional<String> result = dialog.showAndWait();

      if (result.isPresent()) {
        String selectedTerritoryName = result.get();
        Territory selectedTerritory = territories.stream().filter(t -> t.getName().equals(selectedTerritoryName))
            .findFirst().orElse(null);
        future.complete(selectedTerritory);
      } else {
        future.complete(null);
      }
    }

    return future;
  }

  /**
   *
   * Displays a dialog box that allows the user to select the level of units to be
   * used in a certain order. The dialog box displays a list of levels that are
   * available for selection, based on the inclusive lower and upper limits of
   * levels that can be selected. If the list is empty, an error message is
   * displayed to the user and the future is completed with a null value.
   * Otherwise, a ChoiceDialog is displayed to the user with the available levels
   * and the user's selection is returned via a CompletableFuture. If the user
   * cancels the dialog, the future is completed with a null value.
   *
   * @param inclusiveLowerLevel the lowest level of units that can be selected
   *                            (inclusive)
   * @param inclusiveUpperLevel the highest level of units that can be selected
   *                            (inclusive)
   * @param src                 the source territory from which the units will be
   *                            selected
   * @param title               the title of the dialog box
   * @param content             the content displayed in the dialog box
   * @return a CompletableFuture that is completed with the user's selected unit
   *         level or a null value if the user cancels or if the list of available
   *         levels is empty
   */
  private CompletableFuture<Integer> showUnitLevelSelectionDialog(int inclusiveLowerLevel, int inclusiveUpperLevel,
      Territory src, String title, String content) {

    List<Integer> levels = new ArrayList<>();
    for (int i = inclusiveLowerLevel; i <= inclusiveUpperLevel; i++) {
      levels.add(i);
    }

    CompletableFuture<Integer> future = showSelectionDialog(levels,
        title + " Cannot upgrade level for units on " + src.getName() + " because of level upper limit. ", title,
        content);

    return future;
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

  /**
   *
   * Shows a dialog to select the number of units to move or upgrade from a
   * specified territory. The dialog presents the user with a list of integers
   * from 1 up to the maximum number of units available to move or upgrade. If
   * there are no units available at the specified level to upgrade, an error
   * message is displayed and the method returns null. Otherwise, a
   * CompletableFuture is returned that completes with the selected number of
   * units.
   *
   * @param currentLevel the current level of units to move or upgrade
   * @param src          the source territory to move or upgrade units from
   * @param title        the title of the dialog
   * @return a CompletableFuture that completes with the selected number of units,
   *         or null if there are no units available to upgrade
   */
  private CompletableFuture<Integer> showNumberOfUnitsSelectionDialog(Integer currentLevel, Territory src,
      String title) {

    List<Integer> nums = new ArrayList<>();
    int num = src.getUnitsNumByLevel(currentLevel);

    for (int i = 1; i <= num; i++) {
      nums.add(i);
    }

    CompletableFuture<Integer> future = showSelectionDialog(nums,
        title + " Cannot " + " to upgrade level for units on " + src.getName() + " because units are not enough", title,
        "Choose unit number:");

    return future;
  }

  /**
   * Construct, display and return self-owned territories with serial numbers
   * 
   * @return a Map<Integer, Territory> mapping from serial number to the territory
   */
  private Set<Territory> getSelfOwnedTerritories() {
    PlayerMapInfo playerMapInfo = this.thisView.globalMapInfo.getPlayerMapInfo(this.playerId);

    return playerMapInfo.getTerritories();
  }

  /**
   * 
   * Finds all enemy territories on the current game map and returns them as a Set
   * of Territory objects.
   *
   * @return a Set of all enemy territories
   */
  protected Set<Territory> findEnemyTerritories() {
    Set<Territory> set = new HashSet<>();
    Map<Integer, PlayerMapInfo> globalInfo = this.thisView.globalMapInfo.getGlobalMap();
    for (Map.Entry<Integer, PlayerMapInfo> entry : globalInfo.entrySet()) {
      if (!entry.getKey().equals(this.playerId)) {
        Set<Territory> territories = entry.getValue().getTerritories();
        for (Territory t : territories) {
          set.add(t);
        }
      }
    }
    return set;
  }

  /**
   * 
   * Finds all enemy territories that are directly connected to the given source
   * territory. It first calls the findEnemyTerritories() method to retrieve all
   * enemy territories, then checks the neighboring territories of the source
   * territory to see which ones are also enemy territories. The result is a set
   * of all enemy territories that are directly connected to the source territory.
   *
   * @param src the source territory for which to find connected enemy territories
   * @return a set of all enemy territories that are directly connected to the
   *         source territory
   */
  private Set<Territory> getConnectedEnemyTerritories(Territory src) {
    Set<Territory> enemyTerritories = findEnemyTerritories();
    Map<Territory, Integer> neighs = this.gameMap.getNeighborDist(src);
    Set<Territory> result = new HashSet<>();
    for (Territory t : neighs.keySet()) {
      if (enemyTerritories.contains(t)) {
        result.add(t);
      }
    }
    return result;
  }

  /**
   * 
   * Returns a set of self-owned territories that are reachable from the given
   * source territory via a path owned by the same player. This method uses the
   * hasSamePlayerPath() method from the GameMap class to check if a path between
   * the source and each potential territory exists and is owned by the same
   * player as the source. If a reachable territory is found, it is added to the
   * result set.
   *
   * @param src the source territory
   * @return a set of self-owned territories reachable from the source territory
   */
  private Set<Territory> getHasPathSelfTerritories(Territory src) {
    Set<Territory> selfTerritories = getSelfOwnedTerritories();

    Set<Territory> result = new HashSet<>();
    for (Territory t : selfTerritories) {
      if (this.gameMap.hasSamePlayerPath(src, t)) {
        result.add(t);
      }
    }
    return result;
  }

  /**
   *
   * Returns a set of territories that can be selected for issuing a move or
   * attack order, based on the given source territory and whether the order is a
   * move or attack. If the source territory is null, the method returns all
   * territories owned by the current player that have at least one unit on them.
   * If the source territory is not null and the order is a move, the method
   * returns all territories that are owned by the current player and have a path
   * to the source territory. If the order is an attack, the method returns all
   * enemy territories that are adjacent to the source territory.
   *
   * @param src  the source territory for the order
   * @param move true if the order is a move, false if it is an attack
   * @return a set of territories that can be selected for issuing the order
   */
  private Set<Territory> getTerritories(Territory src, boolean move) {
    if (src == null) {
      return getSelfOwnedTerritories().stream().filter(territory -> territory.getNumUnits() > 0)
          .collect(Collectors.toSet()); // to ensure only display src territory that has more than 0 units
    } else {
      if (move) {
        return getHasPathSelfTerritories(src);
      } else {
        return getConnectedEnemyTerritories(src);
      }
    }
  }

  private Commit currentCommit = null;
  private GameMap unChangedGameMap = null;

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
    currentCommit = new Commit(this.playerId, (GameMap) this.unChangedGameMap.clone(), copiedResource);
  }
}
