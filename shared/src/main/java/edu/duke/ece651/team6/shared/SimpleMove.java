package edu.duke.ece651.team6.shared;

public abstract class SimpleMove {
  final Territory src;
  final Territory dest;
  final int numUnits;

  public SimpleMove(Territory src, Territory dest, int num) {
    this.src = src;
    this.dest = dest;
    this.numUnits = num;
  }

  abstract public void takeAction(GameMap gameMap);
}
