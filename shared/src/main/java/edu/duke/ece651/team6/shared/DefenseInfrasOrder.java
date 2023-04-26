package edu.duke.ece651.team6.shared;

public class DefenseInfrasOrder extends Order {
    private Territory territory;

    public DefenseInfrasOrder(Territory territory) {
        super("DefenseInfras");
        this.territory = territory;
    }

    public Territory getTerritory() {
        return this.territory;
    }

    public void takeAction(GameMap gameMap) {
        int cost = 1;
        gameMap.consumeResource(territory.getOwnerId(), Constants.RESOURCE_TECH, cost);
        Territory target = gameMap.getTerritoryByName(this.territory.getName());
        target.setDefenseInfras();
    }

    @Override
    public String toString() {
        return getName();
    }
}
