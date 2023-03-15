package edu.duke.ece651.team6.shared;

/**
 * Represents a simple move order
 * It contains src and dest territory information and number of units to move
 */
public abstract class SimpleMove implements java.io.Serializable {
  final Territory src;
  final Territory dest;
  final int numUnits;

  /**
   * Constructs a simple move
   * @param src represents a src territory on the client side
   * @param dest represents a dest territory on the server side
   * @param num represents number of units to move
   */
  public SimpleMove(Territory src, Territory dest, int num) {
    this.src = src;
    this.dest = dest;
    this.numUnits = num;
  }

  /**
   * Take action
   * 
   * @param gameMap is the game map to perform actions on
   */
  abstract public void takeAction(GameMap gameMap);
}
