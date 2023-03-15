package edu.duke.ece651.team6.shared;

public class MoveOrder extends SimpleMove {

  public MoveOrder(Territory src, Territory dest, int numUnits) {
    super(src, dest, numUnits);
  }

  // Modify the gameMap on the game master side
  @Override
  public void takeAction(GameMap gameMap) {
    // TODO: modify Territories on gameMap
    // Territory from = gameMap.getTerritoryByName(src.getName());
    // Territory to = gameMap.getTerritoryByName(dest.getName());
    // from.moveTo(to, numUnits);
    src.moveTo(dest, numUnits);
  }
}
