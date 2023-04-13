package edu.duke.ece651.team6.shared;

public class AttackOrder extends SimpleMove {

  public AttackOrder(Territory src, Territory dest, int[] numUnitsByLevel) {
    super("Attack", src, dest, numUnitsByLevel);
  }

  /**
   * Take attack action and modify the gameMap on the game master side
   * 
   * @param gameMap is the map where attacks happen
   */
  @Override
  public void takeAction(GameMap gameMap) {
    Territory from = gameMap.getTerritoryByName(src.getName());
    Territory to = gameMap.getTerritoryByName(dest.getName());
    from.attack(to, numUnitsByLevel);
    int cost = CostCalculator.calculateAttackCost(this, gameMap);
    gameMap.consumeResource(from.getOwnerId(), Constants.RESOURCE_FOOD, cost);
  }
}
