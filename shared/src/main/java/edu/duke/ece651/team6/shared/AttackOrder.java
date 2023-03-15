package edu.duke.ece651.team6.shared;

public class AttackOrder extends SimpleMove {

  public AttackOrder(Territory src, Territory dest, int numUnits) {
    super(src, dest, numUnits);
  }

  @Override
  public void takeAction(GameMap gameMap) {
    // TODO: modify Territories on gameMap
    // Territory from = gameMap.getTerritoryByName(src.getName());
    // Territory to = gameMap.getTerritoryByName(dest.getName());
    // from.moveTo(to, numUnits);
    src.attack(dest, numUnits);
  }
}
