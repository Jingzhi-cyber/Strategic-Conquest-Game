package edu.duke.ece651.team6.client.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import edu.duke.ece651.team6.client.controller.Controller;
import edu.duke.ece651.team6.shared.*;
import javafx.scene.control.ChoiceDialog;

public class OrdersHandler {
  Controller mainPageController;

  public OrdersHandler(Controller mainPageController) {
    this.mainPageController = mainPageController;
  }

  public void handleMoveOrder(Commit currentCommit, int playerId)
      throws IOException, InterruptedException, ExecutionException {
    Territory src = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, null, true), playerId,
        "Move Order", "Which territory do you want to move units from?").get();

    if (src == null) {
      // mainPageController.showError("Must specify a territory to move units from");
      return; // User cancelled the dialog
    }

    Territory dest = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, src, true), playerId,
        "Move Order", "Which territory do you want to move units to?").get();

    if (dest == null) {
      // mainPageController.showError("Must specify a territory to move units to");
      return; // User cancelled the dialog
    }

    int[] numUnitsByLevel = new int[Constants.MAX_LEVEL + 1];
    Integer selectedLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL, src, "Move Order",
        "Which level of units do you want to move?").get();
    if (selectedLevel == null) {
      // mainPageController.showError("Must specify a level to move");
      return; // User cancelled the dialog
    }
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedLevel, src, "How many of them do you want to move?")
        .get();

    if (numUnits == null) {
      // mainPageController.showError("Must specify the number of units to move");
      return; // User cancelled the dialog
    }

    numUnitsByLevel[selectedLevel] = numUnits;
    // MoveOrder move = null;
    MoveOrder move = new MoveOrder(src, dest, numUnitsByLevel);

    /* -------- Try adding the order -------- */
    try {
      currentCommit.addMove(move);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return;
    }
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
  public void handleAttackOrder(Commit currentCommit, int playerId)
      throws IOException, InterruptedException, ExecutionException {

    Territory src = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, null, false), playerId,
        "Attack Order", "Which territory do you want to attack units from").get();

    if (src == null) {
      // mainPageController.showError("Must specify a territory to attack from");
      return; // User cancelled the dialog
    }

    Territory dest = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, src, false), playerId,
        "Attack Order", "Which territory do you want to attack units to").get();

    if (dest == null) {
      // mainPageController.showError("Must specify a territory to attack units to");
      return; // User cancelled the dialog
    }

    int[] numUnitsByLevel = new int[Constants.MAX_LEVEL + 1];

    Integer selectedLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL, src, "Attack Order",
        "Which level of units do you want to attack?").get();
    if (selectedLevel == null) {
      // mainPageController.showError("Must specify a level to attack");
      return; // User cancelled the dialog
    }
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedLevel, src, "How many of them are used to attack")
        .get();

    if (numUnits == null) {
      // mainPageController.showError("Must specify the number of units to attack");
      return; // User cancelled the dialog
    }

    numUnitsByLevel[selectedLevel] = numUnits;

    AttackOrder attack = new AttackOrder(src, dest, numUnitsByLevel);
    try {
      currentCommit.addAttack(attack);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return;
    }
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
  public void handleResearchOrder(Commit currentCommit, int playerId) throws IllegalArgumentException {
    ResearchOrder research = new ResearchOrder(playerId);
    try {
      currentCommit.addResearch(research);
      mainPageController.showSuccess("Successfully added a research order");
      // return research;
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      // return null;
    }
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
  public void handleUpgradeOrder(Commit currentCommit, int playerId)
      throws IOException, InterruptedException, ExecutionException {
    // Show a dialog to get the source territory from the user
    Territory src = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, null, true), playerId,
        "On which territory do you want to upgrade units?", "Choose a territory").get(); // true/false
    // both okay if
    // src is null
    if (src == null) {
      // mainPageController.showError("Must specify a territory to upgrade units");
      return; // User cancelled the dialog
    }

    // Show a dialog to get the current unit level from the user
    Integer selectedNowLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL - 1, src, "Upgrade Order",
        "From which level do you want to upgrade units?").get();
    if (selectedNowLevel == null) {
      // mainPageController.showError("Must specify a level to upgrade");
      return; // User cancelled the dialog
    }

    // Show a dialog to get the target unit level from the user
    Integer selectedTargetLevel = showUnitLevelSelectionDialog(selectedNowLevel + 1, Constants.MAX_LEVEL, src,
        "Upgrade Order", "To which level do you want to upgrade units?").get();
    if (selectedTargetLevel == null) {
      // mainPageController.showError("Must specify a target level");
      return;
    }

    // Show a dialog to get the number of units to upgrade from the user
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedNowLevel, src,
        "How many of them do you want to upgrade?").get();
    if (numUnits == null) {
      // mainPageController.showError("Must specify the number of units to upgrade");
      return;
    }

    // Create and execute the upgrade order
    UpgradeOrder upgrade = new UpgradeOrder(src, selectedNowLevel, selectedTargetLevel, numUnits);

    try {
      currentCommit.addUpgrade(upgrade);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return;
    }
  }

  /* ---------------------- Cloak and Spy -------------------- */

  public void handleCloakOrder(Commit currentCommit, int playerId)
      throws IOException, InterruptedException, ExecutionException {
    // Show a dialog to get the source territory from the user
    Territory territory = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, null, true), playerId,
        "On which territory do you want to cloak?", "Choose a territory").get(); // true/false
    // both okay if
    // src is null
    if (territory == null) {
      // mainPageController.showError("Must specify a territory to units");
      return; // User cancelled the dialog
    }

    // Create and execute the upgrade order
    CloakTerritoryOrder cloak = new CloakTerritoryOrder(territory);

    try {
      currentCommit.addCloakTerritoryOrder(cloak);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return;
    }
  }

  public void handleGenerateSpyOrder(Commit currentCommit, int playerId)
      throws IOException, InterruptedException, ExecutionException {
    // Show a dialog to get the source territory from the user
    Territory src = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, null, true), playerId,
        "On which territory do you want to generate spies?", "Choose a territory").get(); // true/false
    // both okay if
    // src is null
    if (src == null) {
      // mainPageController.showError("Must specify a territory to generate spies");
      return; // User cancelled the dialog
    }

    // Show a dialog to get the current unit level from the user
    Integer selectedNowLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL - 1, src, "Upgrade Order",
        "From which level do you want to generate spies?").get();
    if (selectedNowLevel == null) {
      // mainPageController.showError("Must specify a level to generate spies");
      return; // User cancelled the dialog
    }

    // Show a dialog to get the number of spies to generate
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedNowLevel, src,
        "How many spies do you want to generate from them?").get();
    if (numUnits == null) {
      // mainPageController.showError("Must specify the number of units to generate
      // spies");
      return;
    }

    // Create and execute the upgrade order
    GenerateSpyOrder generateSpyOrder = new GenerateSpyOrder(playerId, src, selectedNowLevel, numUnits);

    try {
      currentCommit.addGenerateSpyOrder(generateSpyOrder);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return;
    }
  }

  public void handleMoveSpyOrder(Commit currentCommit, int playerId)
      throws IOException, InterruptedException, ExecutionException {
    Territory src = showTerritorySelectionDialog(
        currentCommit.getCurrentGameMap().getSpyingTerritoriesOfAPlayer(playerId), playerId, "Move Spies Order",
        "Which territory do you want to move spies from?").get();

    if (src == null) {
      // mainPageController.showError("Must specify a territory to move spies from");
      return; // User cancelled the dialog
    }

    Territory dest = showTerritorySelectionDialog(currentCommit.getCurrentGameMap().getNeighborDist(src).keySet(),
        playerId, "Move Spies Order", "Which territory do you want to move spies to?").get();

    if (dest == null) {
      // mainPageController.showError("Must specify a territory to move spies to");
      return; // User cancelled the dialog
    }

    int spyNum = src.getSpyNumByPlayerId(playerId, true) + src.getSpyNumByPlayerId(playerId, false);
    List<Integer> numList = new ArrayList<>();
    for (int i = 1; i <= spyNum; i++) {
      numList.add(i);
    }

    Integer numUnits = showNumberSelectionDialog(numList,
        "Please change a source territory as the current one doesn't have enough spies to move", "Move Spies Order",
        "How many of them do you want to move?").get();

    if (numUnits == null) {
      // mainPageController.showError("Must specify the number of spies to move");
      return; // User cancelled the dialog
    }

    // MoveOrder move = null;
    MoveSpyOrder moveSpy = new MoveSpyOrder(playerId, src, dest, numUnits);

    /* -------- Try adding the order -------- */
    try {
      currentCommit.addMoveSpyOrder(moveSpy);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
      return;
    }
  }

  public void handleSanBingOrder(Commit currentCommit, int playerId) throws ExecutionException, InterruptedException {

    Territory src = showTerritorySelectionDialog(getTerritories(currentCommit, playerId, null, false), playerId,
            "San Bing", "Which territory do you want to attack units from").get();

    if (src == null) {
      // mainPageController.showError("Must specify a territory to attack from");
      return; // User cancelled the dialog
    }

    Territory dest = showTerritorySelectionDialog(currentCommit.getCurrentGameMap().getEnemyTerritorySetByPlayerId(playerId),
            playerId, "San Bing", "Which territory do you want to attack units to").get();

    if (dest == null) {
      // mainPageController.showError("Must specify a territory to attack units to");
      return; // User cancelled the dialog
    }

    int[] numUnitsByLevel = new int[Constants.MAX_LEVEL + 1];

    Integer selectedLevel = showUnitLevelSelectionDialog(0, Constants.MAX_LEVEL, src, "San Bing",
            "Which level of units do you want to attack?").get();
    if (selectedLevel == null) {
      // mainPageController.showError("Must specify a level to attack");
      return; // User cancelled the dialog
    }
    Integer numUnits = showNumberOfUnitsSelectionDialog(selectedLevel, src, "How many of them are used as San Bing?")
            .get();

    if (numUnits == null) {
      // mainPageController.showError("Must specify the number of units to attack");
      return; // User cancelled the dialog
    }

    numUnitsByLevel[selectedLevel] = numUnits;
    SanBingOrder sanBing = new SanBingOrder(src, dest, numUnitsByLevel);
    try {
      currentCommit.addSanBingOrder(sanBing);
    } catch (IllegalArgumentException e) {
      mainPageController.showError(e.getMessage());
    }
  }

  public void handleSuperShieldOrder(Commit currentCommit, int playerId) throws ExecutionException, InterruptedException {
    Territory src = showTerritorySelectionDialog(currentCommit.getCurrentGameMap().getTerritorySetByPlayerId(playerId), playerId,
            "San Bing", "Which territory do you want to use Super Shield:").get();

    if (src == null) {
      // mainPageController.showError("Must specify a territory to attack from");
      return; // User cancelled the dialog
    }
    SuperShieldOrder superShield = new SuperShieldOrder(src);
    currentCommit.addSuperShieldOrder(superShield);
  }

  /* --------------------------------------------------------------- */

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
  private CompletableFuture<Territory> showTerritorySelectionDialog(Set<Territory> territories, int playerId,
      String title, String content) {
    CompletableFuture<Territory> future = new CompletableFuture<>();

    // Set<Territory> territories = getTerritories(currentCommit, playerId, src,
    // isMove);
    List<String> territoryNames = territories.stream().map(Territory::getName).collect(Collectors.toList());

    if (territories.isEmpty()) {
      mainPageController.showError("There aren't enough territories");
      future.complete(null);
    } else {
      ChoiceDialog<String> dialog = new ChoiceDialog<>(territoryNames.get(0), territoryNames);
      dialog.setTitle(title);
      dialog.setHeaderText(null);
      dialog.setContentText(content);

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
    CompletableFuture<Integer> future = new CompletableFuture<>();

    List<Integer> levels = new ArrayList<>();
    for (int i = inclusiveLowerLevel; i <= inclusiveUpperLevel; i++) {
      levels.add(i);
    }

    if (levels.isEmpty()) {
      mainPageController.showError("Cannot upgrade unit level from " + inclusiveLowerLevel + " to "
          + inclusiveUpperLevel + " on " + src.getName());
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

    return showNumberSelectionDialog(nums,
        "Number of units not enough for level " + currentLevel + " on territory " + src.getName(), title,
        "Choose unit number:");
    // CompletableFuture<Integer> future = new CompletableFuture<>();
    // if (nums.isEmpty()) {
    // mainPageController
    // .showError("Number of units not enough for level " + currentLevel + " on
    // territory " + src.getName());
    // future.complete(null);
    // } else {
    // ChoiceDialog<Integer> dialog = new ChoiceDialog<>(nums.get(0), nums);
    // dialog.setTitle(title);
    // dialog.setHeaderText(null);
    // dialog.setContentText("Choose unit number:");

    // Optional<Integer> result = dialog.showAndWait();

    // if (result.isPresent()) {
    // future.complete(result.get());
    // } else {
    // future.complete(null);
    // }
    // }

    // return future;
  }

  private CompletableFuture<Integer> showNumberSelectionDialog(List<Integer> nums, String errorMessage, String title,
      String content) {
    CompletableFuture<Integer> future = new CompletableFuture<>();
    if (nums.isEmpty()) {
      mainPageController.showError(errorMessage);
      future.complete(null);
    } else {
      ChoiceDialog<Integer> dialog = new ChoiceDialog<>(nums.get(0), nums);
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
    return future;
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
   * It is worth mentioning that the territories are gotten from the
   * currentCommit's gameMap
   *
   * @param src  the source territory for the order
   * @param move true if the order is a move, false if it is an attack
   * @return a set of territories that can be selected for issuing the order
   */
  private Set<Territory> getTerritories(Commit currentCommit, int playerId, Territory src, boolean move) {
    GameMap newestGameMap = currentCommit.getCurrentGameMap();
    if (src == null) {
      return newestGameMap.getTerritorySetByPlayerId(playerId).stream().filter(territory -> territory.getNumUnits() > 0)
          .collect(Collectors.toSet()); // to ensure only display src
                                        // territory that has more than
                                        // 0 units
    } else {
      if (move) {
        return newestGameMap.getHasPathSelfTerritories(src);
      } else {
        return newestGameMap.getEnemyNeighbors(src);
      }
    }
  }
}
