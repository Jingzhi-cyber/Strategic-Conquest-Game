package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.Territory;

/**
 * An interface representing a player in the risc game
 */
public abstract class Game {
  protected final SocketHandler socketHandler;
  protected boolean hasLost = false;
  protected int playerId = -1;

  public Game(SocketHandler socketHandler) {
    this.socketHandler = socketHandler;
  }
  /**
   * Initialize the Player Postcondition: if return true, the connection to the
   * Master is established
   * 
   * @return true if success
   */
  // public boolean init() throws IOException, ClassNotFoundException;

  /**
   * At the beginning of the game, Player needs to place units to the territories
   */
  // abstract public void placeUnit() throws IOException, ClassNotFoundException,
  // InterruptedException, ExecutionException;

  /**
   * Player's one turn of game includes: 1. Receive playerinfo from master 2. Make
   * several actions, create a commit 3. Send commit to master, if the master says
   * the commit is invalid, repeat 2
   * 
   * @return a String indicating the exit info of the player of the turn
   */
  // abstract public String playOneTurn() throws IOException,
  // ClassNotFoundException;

  /**
   * Play the game by repeatedly calling playOneTurn, and properly handle result
   * of each turn
   * 
   * @throws exceptions, {@link IOException}, {@link UnknownHostException},
   *                     {@link ClassNotFoundException}
   */
  // abstract public void playGame() throws IOException, UnknownHostException,
  // ClassNotFoundException;

  // abstract protected Territory findTerritory(Territory src, String message)
  // throws IOException;

  // abstract protected Set<Territory> findEnemyTerritories();

  // abstract public MoveOrder constructMoveOrder() throws IOException;

  // abstract public AttackOrder constructAttackOrder() throws IOException;

  // abstract public ResearchOrder constructResearchOrder();

  // abstract public UpgradeOrder constructUpgradeOrder() throws IOException;

  // abstract public Commit constructCommit() throws IOException,
  // ClassNotFoundException;

  /**
   * Receive updated map information and display it to player
   * 
   * @return received GlobalMapInfo
   * @throws IOException, {@link ClassNotFoundException}
   */
  // abstract public GlobalMapInfo refreshMap() throws IOException,
  // ClassNotFoundException;
}
