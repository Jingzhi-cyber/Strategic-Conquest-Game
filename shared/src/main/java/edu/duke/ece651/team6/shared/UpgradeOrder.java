package edu.duke.ece651.team6.shared;

public class UpgradeOrder extends Order {
  private Territory t;
  private int nowLevel;
  private int targetLevel;
  private int numUnits;

  public UpgradeOrder(Territory territory, int nowLevel, int targetLevel, int numUnits) {
    super("Upgrade");
    this.t = territory;
    this.nowLevel = nowLevel;
    this.targetLevel = targetLevel;
    this.numUnits = numUnits;
  }

  public Territory getTerritory() {
    return this.t;
  }

  public int getNowLevel() {
    return this.nowLevel;
  }

  public int getTargetLevel() {
    return this.targetLevel;
  }

  public int getNumUnits() {
    return this.numUnits;
  }

  public void takeAction(GameMap gameMap) {
    int cost = UnitManager.costToUpgrade(nowLevel, targetLevel) * numUnits;
    gameMap.consumeResource(t.getOwnerId(), Constants.RESOURCE_TECH, cost);
    Territory territory = gameMap.getTerritoryByName(t.getName());
    for (int i = 0; i < numUnits; i++) {
      territory.upgradeOneUnit(nowLevel, targetLevel);
    }
  }

  @Override
  public String toString() {
    return getName() + " " + numUnits + " units from " + nowLevel + " to " + targetLevel + " on Territory "
        + t.getName();
  }
}
