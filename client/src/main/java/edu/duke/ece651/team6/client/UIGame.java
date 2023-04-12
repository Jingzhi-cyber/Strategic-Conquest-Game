package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
import edu.duke.ece651.team6.shared.ResearchOrder;
import edu.duke.ece651.team6.shared.Result;
import edu.duke.ece651.team6.shared.Territory;
import edu.duke.ece651.team6.shared.UpgradeOrder;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

public class UIGame extends Game {

  // private UnitPlacementController unitPlacementController;
  private MainPageController mainPageController;

  int gameId; // unique for games that are of each player
  // Integer playerId;
  String username;

  Scene scene;

  GameMap gameMap;

  MapView thisView = null;

  GAME_STATUS gameStatus;

  GameBasicSetting setting;

  Map<String, Integer> resource;

  GameLounge gameLounge;

  public void setScene(Scene scene) {
    this.scene = scene;
  }

  public Scene getScene() {
    return this.scene;
  }

  public GAME_STATUS getStatus() {
    return this.gameStatus;
  }

  public void setGameLounge(GameLounge gameLounge) {
    this.gameLounge = gameLounge;
  }

  public MainPageController getMainPageController() {
    return this.mainPageController;
  }

  // public UnitPlacementController getUnitPlacementController() {
  // return this.unitPlacementController;
  // }

  public void setMainPageController(MainPageController mainPageController) {
    this.mainPageController = mainPageController;
  }

  // public void setUnitPlacementController(UnitPlacementController
  // unitPlacementController) {
  // this.unitPlacementController = unitPlacementController;
  // }

  public UIGame(int gameId, String username, SocketHandler socketHandler, MainPageController mainPageController) {
    super(socketHandler);
    this.gameId = gameId;
    this.username = username;
    this.gameStatus = GAME_STATUS.PLACE_UNITS;
    this.resource = new HashMap<>();
    // Initialize the controller, e.g., by loading the FXML file
    // this.unitPlacementController = unitPlacementController;// TODO
    // this.unitPlacementController.setUIGame(this);
    this.mainPageController = mainPageController;
    this.mainPageController.setUiGame(this);
    // this.mainPageController.setUsername(username);
  }

  public void exit() throws IOException {
    socketHandler.socket.close();
  }

  public void updateGameStatus(GAME_STATUS gameStatus) {
    this.gameStatus = gameStatus;
  }

  /* ----------1. Handling Unit Placement ----------- */
  private Map<Territory, Integer> territoryToNumUnitMapping = new HashMap<>();
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

  public void nextUnitPlacementPrompt()
      throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
    if (territoryIterator.hasNext()) {
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
              "Player" + this.playerId + ", how many units do you want to place on the " + currentTerritory.getName()
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

  /**
   * This method receives initial Territory information from the server And
   * display UI to the user so that user interact to input units information
   */
  public void entryPoint() throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
    System.out.println("Game status: " + gameStatus);
    if (this.gameStatus == GAME_STATUS.PLACE_UNITS) {
      System.out.print("Placing units\n");
      System.out.println((String) socketHandler.recvObject());
      setting = socketHandler.recvGameBasicSetting();

      // territoryToNumUnitMapping = new HashMap<>();

      Set<Territory> territories = setting.getAssignedTerritories();
      territoryIterator = territories.iterator();

      while (gameStatus == GAME_STATUS.PLACE_UNITS) {
        nextUnitPlacementPrompt();
      }
    }

    refreshMap();

    Platform.runLater(() -> {
      mainPageController.setPlayTurnsButtonsDisabled(false);
    });
  }

  /**
   * In each turn, receive game result, decide whether to exit if having lost the
   * game and return player's choice based on game result
   * 
   * @return String is player's choice based on game status
   * @throws IOException, {@link ClassNotFoundException}
   * @catch {@link IllegalArgumentException}
   */
  private String receiveGameResult() throws IOException, ClassNotFoundException {
    Result result = this.socketHandler.recvGameResult();

    if (result.getWinners().size() > 0) {
      this.gameStatus = GAME_STATUS.GAME_OVER;

      System.out.println("*** added runLater");
      Platform.runLater(() -> {
        mainPageController.updateGameStatus(gameStatus);
      });

      // TODO printLine("Player " + result.getWinners().toString() + " wins!");

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

    if (result.getLosers().contains(this.playerId)) {
      this.hasLost = true;
      this.gameStatus = GAME_STATUS.GAME_OVER;
      Platform.runLater(() -> {
        mainPageController.updateGameStatus(gameStatus);
      });

      return Constants.GAME_OVER;
    }

    return null;
  }

  /**
   * This method plays one turn in one game
   */
  @Override
  public String playOneTurn() throws IOException, ClassNotFoundException {
    /* -------- 1. Receive and display the map --------- */
    // will update local variables: mapTextView and gameMap
    refreshMap();

    System.out.println("Refreshed Map");
    /*
     * ---------2. Skip constructing a commit, and skip sending it to the server,
     * instead, directly waiting for the game result. ----------
     */
    if (this.hasLost) {
      receiveGameResult();
    }

    /*
     * ---------3. Set game status and initiate a commit -----------
     */
    initiateCommit();
    // updateGameStatus(this.gameStatus);

    Platform.runLater(() -> {
      this.mainPageController.setOrderMenuDisabled(false);
      this.mainPageController.setSubmitOrderDisabled(false);
    });

    /* User interactions to place orders and sending commit */

    /* -------- Handle game result of this turn -------- */
    String result = receiveGameResult();

    initiateCommit();

    Platform.runLater(() -> {
      this.mainPageController.setOrderMenuDisabled(false);
      this.mainPageController.setSubmitOrderDisabled(false);
    });

    return result;
  }

  public void submitCommit() {
    /* -------- Show a confirmation dialog with the orders-------- */
    String ordersString = this.currentCommit.toString();

    Platform.runLater(() -> {
      Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
      confirmationDialog.setTitle("Confirm Orders");
      confirmationDialog.setHeaderText("Please confirm your orders:");
      confirmationDialog.setContentText(ordersString);

      Optional<ButtonType> confirmOrder = confirmationDialog.showAndWait();

      if (confirmOrder.isPresent() && confirmOrder.get() == ButtonType.OK) {
        // Send commit to the server if the user confirms

        try {
          this.socketHandler.sendCommit(this.currentCommit);
        } catch (Exception e) {
          e.printStackTrace();
        }

        System.out.println("Successfully sent a commit to the server");
        updateGameStatus(GAME_STATUS.WAITING_FOR_RESULT);

        this.mainPageController.setPlayTurnsButtonsDisabled(true);

        // TODO
        try {
          socketHandler.recvGameResult();
          System.out.println("Successfully received game result");

          refreshMap();
          System.out.println("Successfully refreshed map");
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
          mainPageController.showError(e.getMessage());
        }

      } else {
        // Handle the case when the user cancels the confirmation dialog
        return;
      }
    });

  }

  /**
   * This method handles a whole game for a user
   */
  public void playGame() throws IOException, UnknownHostException, ClassNotFoundException {
    System.out.println("UIGame - playGame - GameStatus " + gameStatus);
    while (gameStatus != GAME_STATUS.GAME_OVER) {
      System.out.println("UIGame - playGame - GameStatus " + gameStatus);
      String result = playOneTurn();
      if (result != null) {
        // System.exit(0);
      }
    }
  }

  /**
   * This method receives a updated GameMap from the server and display the new
   * scene to the user
   */
  @Override
  public GlobalMapInfo refreshMap() throws IOException, ClassNotFoundException {

    GlobalMapInfo mapInfo = socketHandler.recvGlobalMapInfo();
    if (mapInfo.playerId != -1) {
      this.playerId = mapInfo.playerId;
    }
    this.thisView = new UIGameView(mapInfo);
    this.gameMap = mapInfo.getGameMap();
    System.out.println("GameMap - UIGame.java" + gameMap.toString());

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

    initiateCommit();

    // TODO Update Map!

    // thisView.updateScene();
    // TODO Map<String, Territory> territoriesMap;
    // populate the map with Territory objects

    // TODO SelectableTerritories selectableTerritories = new
    // SelectableTerritories(territoriesMap);

    // TODO display the globalMap on UI
    return mapInfo;
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

    try {
      this.currentCommit.addMove(move);
    } catch (IllegalArgumentException e) {
      // Platform.runLater(() -> {
      mainPageController.showError(e.getMessage());
      // });

      return null;
    }

    return move;
  }

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

  private CompletableFuture<Territory> showTerritorySelectionDialog(Territory src, boolean isMove, String title,
      String context) {
    CompletableFuture<Territory> future = new CompletableFuture<>();

    // Platform.runLater(() -> {
    Set<Territory> territories = getTerritories(src, isMove);
    List<String> territoryNames = territories.stream().map(Territory::getName).collect(Collectors.toList());

    if (territories.isEmpty()) {
      mainPageController.showError("Cannot " + title + " because there aren't available territories");
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
    // });

    return future;
  }

  private CompletableFuture<Integer> showUnitLevelSelectionDialog(int inclusiveLowerLevel, int inclusiveUpperLevel,
      Territory src, String title, String content) {
    CompletableFuture<Integer> future = new CompletableFuture<>();

    // Platform.runLater(() -> {
    List<Integer> levels = new ArrayList<>();
    // TODO <=
    for (int i = inclusiveLowerLevel; i <= inclusiveUpperLevel; i++) {
      levels.add(i);
    }

    if (levels.isEmpty()) {
      mainPageController.showError(
          "Cannot " + title + " to upgrade level for units on " + src.getName() + " because of level upper limit");
      future.complete(null);
    } else {
      ChoiceDialog<Integer> dialog = new ChoiceDialog<>(levels.get(0), levels);
      dialog.setTitle(title);
      dialog.setHeaderText(null);
      dialog.setContentText(content);

      Optional<Integer> result = dialog.showAndWait();

      if (result.isPresent()) {
        future.complete(result.get());
      } else {
        future.complete(null);
      }
    }
    // });

    return future;
  }

  private <T> CompletableFuture<T> showSelectionDialog(List<T> list, String promptIfEmptyList, String title,
      String contentText) {
    if (list.isEmpty()) {
      mainPageController.showError(promptIfEmptyList);
    }
    CompletableFuture<T> future = new CompletableFuture<>();
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

  private CompletableFuture<Integer> showNumberOfUnitsSelectionDialog(Integer currentLevel, Territory src,
      String title) {
    CompletableFuture<Integer> future = new CompletableFuture<>();

    // Platform.runLater(() -> {
    List<Integer> nums = new ArrayList<>();
    int num = src.getUnitsNumByLevel(currentLevel);
    // TODO <=
    for (int i = 1; i <= num; i++) {
      nums.add(i);
    }

    if (nums.isEmpty()) {
      mainPageController.showError(
          "Cannot " + title + " to upgrade level for units on " + src.getName() + " because units are not enough");
      future.complete(null);
    } else {
      ChoiceDialog<Integer> dialog = new ChoiceDialog<>(nums.get(0), nums);
      dialog.setTitle(title);
      dialog.setHeaderText(null);
      dialog.setContentText("Choose unit number:");

      Optional<Integer> result = dialog.showAndWait();

      if (result.isPresent()) {
        future.complete(result.get());
      } else {
        future.complete(null);
      }
    }
    // });

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
   * Construct a list of all enemy territories
   * 
   * @return HashSet<Territory> enemyTerritories
   */
  @Override
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
   * Construct and display a list of possible enemy territories with serial
   * numbers to attack by checking if the enemy territory is directly connected
   * with the source territory
   * 
   * @param src is the source self-owned territory of the player
   * @return Map<Integer, Territory> a map from the serial number to a territory
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
   * getHasPathSelfTerritories with the source territory
   * 
   * @param src is the source self-owned territory of the player, not null
   * @return a map from the serial number to a territory
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

  public void initiateCommit() {
    this.gameStatus = GAME_STATUS.ISSUE_ORDER;

    Map<String, Integer> copiedResource = new HashMap<>();
    copiedResource.putAll(this.resource);
    System.out.println("Initiating Commit");
    currentCommit = new Commit(this.playerId, (GameMap) this.gameMap.clone(), copiedResource);
    System.out.println("Finished initiating a new commit");
  }

  // TODO protected ResearchOrder constructResearch() throws IOException;

  // TODO protected UpgradeOrder constructUpgrade() throws IOException;

}
