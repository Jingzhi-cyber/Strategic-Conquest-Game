package edu.duke.ece651.team6.shared;

public class MoveOrder extends SimpleMove {

  public MoveOrder(Territory src, Territory dest, int[] numUnitsByLevel) {
    super("Move", src, dest, numUnitsByLevel);
  }

  /**
   * Take move action and modify the gameMap on the game master side
   * 
   * @param gameMap is the map where actions happen
   */
  @Override
  public void takeAction(GameMap gameMap) {
    Territory from = gameMap.getTerritoryByName(src.getName());
    Territory to = gameMap.getTerritoryByName(dest.getName());
    from.moveTo(to, numUnitsByLevel);
    int cost = CostCalculator.calculateMoveCost(this, gameMap);
    gameMap.consumeResource(from.getOwnerId(), Constants.RESOURCE_FOOD, cost);
  }
}
