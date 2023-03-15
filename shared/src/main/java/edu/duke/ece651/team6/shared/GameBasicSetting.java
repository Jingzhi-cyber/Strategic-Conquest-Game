package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;

// Contains all information for displaying to each player at the biginning so that they can do setting based on the information
public class GameBasicSetting {
  /* 1. Initialized by the server. */
  final int playerId;
  final int numPlayers;

  final HashSet<Territory> assignedTerritories; // assigned by the game master
  int remainingNumUnits;

  /* 2. Initialized by the client. */
  HashMap<Territory, Integer> unitPlacement;

  public GameBasicSetting(int playerId, int numPlayers, HashSet<Territory> territories, int availableUnits) {
    this.playerId = playerId;
    this.numPlayers = numPlayers;
    this.assignedTerritories = territories;
    this.remainingNumUnits = availableUnits;
    this.unitPlacement = new HashMap<>();
  }

  /* Get methods */
  public int getPlayerId() {
    return playerId;
  }

  public int getNumPlayers() {
    return numPlayers;
  }

  public HashSet<Territory> getAssignedTerritories() {
    return assignedTerritories;
  }

  public int getRemainingNumUnits() {
    return remainingNumUnits;
  }

  /*
   * Decrease units by some amount
   * 
   * @param amount
   * 
   * @throws IllegalArgumentException
   */
  public void decreaseUnitsBy(int amount) {
    if (amount < 0 || remainingNumUnits < amount) {
      throw new IllegalArgumentException(
          "The amount of units is invalid. You have " + remainingNumUnits + " units in total, but the amount was "
              + amount);
    } else {
      remainingNumUnits -= amount;
    }
  }

  /*
   * Initialize unitPlacement after the user has input unit placement information.
   * 
   * @param map how many units are put onto which Territory
   * 
   * @throws InternalError
   */
  /*
   * public void initializeUnitPlacement(HashMap<Territory, Integer> map) {
   * this.unitPlacement = map;
   * for (Territory t : assignedTerritories) {
   * if (!map.containsKey(t)) {
   * throw new InternalError("Missing unit placement information.");
   * }
   * t.initNumUnits(map.get(t));
   * }
   * }
   */

  public void initializeUnitPlacement(HashMap<Territory, Integer> map) {
    this.unitPlacement = map;
  }

  public HashMap<Territory, Integer> getUnitPlacement() {
    return this.unitPlacement;
  }
}
