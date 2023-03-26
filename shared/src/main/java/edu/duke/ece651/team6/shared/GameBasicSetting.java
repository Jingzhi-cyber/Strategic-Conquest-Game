package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Contains all information for displaying to each player at the biginning so that they can do setting based on the information
public class GameBasicSetting implements java.io.Serializable {
  /* 1. Initialized by the server. */
  final int playerId;
  final int numPlayers;

  final Set<Territory> assignedTerritories; // assigned by the game master
  int remainingNumUnits;

  /* Units initialization by the client. */
  Map<Territory, Integer> unitPlacement;

  /**
   * Constructs a GameBasicSetting with 4 params
   *
   * @param playerId
   * @param numPlayers     in the game
   * @param territories    is a group of territories assigned to the player
   * @param availableUnits is the total number of units can be used on the board
   */
  public GameBasicSetting(int playerId, int numPlayers, Set<Territory> territories, int availableUnits) {
    this.playerId = playerId;
    this.numPlayers = numPlayers;
    this.assignedTerritories = territories;
    this.remainingNumUnits = availableUnits;
    this.unitPlacement = new HashMap<>();
  }

  /* Get playerId */
  public int getPlayerId() {
    return playerId;
  }

  /* Get number of players */
  public int getNumPlayers() {
    return numPlayers;
  }

  /* Get assigned territories */
  public Set<Territory> getAssignedTerritories() {
    return assignedTerritories;
  }

  /* Get remaining number of units */
  public int getRemainingNumUnits() {
    return remainingNumUnits;
  }

  /**
   * Decrease units by some amount during units initialization from client side
   * 
   * @param amount
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

  /**
   * Unit initialization phase
   * 
   * @param map : Territory -> numUnits
   */
  public void initializeUnitPlacement(Map<Territory, Integer> map) {
    this.unitPlacement = map;
  }

  /* */

  /**
   * Used by server side to initialize (game map)
   * 
   * @return a HashMap from territory to number of units
   */
  public Map<Territory, Integer> getUnitPlacement() {
    return this.unitPlacement;
  }
}
