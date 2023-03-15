package edu.duke.ece651.team6.client;

import java.io.IOException;

/**
 * An interface representing a player in the risc game
 */
public interface Player {

  /**
   * Initialize the Player
   * Postcondition: if return true, the connection to the Master is established
   * 
   * @return true if success
   */
  // public boolean init() throws IOException, ClassNotFoundException;

  /**
   * At the beginning of the game, Player needs to place units to the territories
   */
  public void placeUnit(String prompt) throws IOException, ClassNotFoundException;

  /**
   * Player's one turn of game includes:
   * 1. Receive playerinfo from master
   * 2. Make several actions, create a commit
   * 3. Send commit to master, if the master says the commit is invalid, repeat 2
   */
  public void playOneTurn() throws IOException, ClassNotFoundException;

  /**
   * Receive updated map information
   * and display it to player
   * 
   * @throws IOException, {@link ClassNotFoundException}
   */
  public void updateAndDisplayMapInfo() throws IOException, ClassNotFoundException;
}
