package edu.duke.ece651.team6.shared;

/**
 * Represents a simple move order
 * It contains src and dest territory information and number of units to move
 */
public abstract class SimpleMove extends Order {
  final Territory src;
  final Territory dest;
  final int[] numUnitsByLevel;

  /**
   * Constructs a simple move
   * 
   * @param src  represents a src territory on the client side
   * @param dest represents a dest territory on the server side
   * @param num  represents number of units to move
   */
  public SimpleMove(String name, Territory src, Territory dest, int[] numUnitsByLevel) {
    super(name);
    this.src = src;
    this.dest = dest;
    this.numUnitsByLevel = numUnitsByLevel;
  }

  /**
   * Get total number of units of all levels
   * @return total
   */
  public int getTotalUnits() {
    int total = 0;
    for (int i = 0; i < numUnitsByLevel.length; i++) {
      total += numUnitsByLevel[i];
    }
    return total;
  }

  /**
   * Take action
   * 
   * @param gameMap is the game map to perform actions on
   */
  abstract public void takeAction(GameMap gameMap);

  @Override
  public String toString() {
    return "{ from: " + src.toString() + ", to: " + dest.toString() + ", numUnits: " + numUnitsByLevel[0] + " }";
  }
}
