package edu.duke.ece651.team6.shared;

public class CloakTerritoryOrder extends Order {
    Territory territory;

    public CloakTerritoryOrder(Territory territory) {
        super("CloakTerritory");
        this.territory = territory;
    }

    public Territory getTerritory() {
        return this.territory;
    }

    public void takeAction(GameMap gameMap) {
        // TODO: calculate cost
        int cost = 1;
        gameMap.consumeResource(territory.getOwnerId(), Constants.RESOURCE_TECH, cost);
        Territory t = gameMap.getTerritoryByName(territory.getName());
        t.setCloakedTurn(3);
    }

    public String toString() {
        return getName() + " on Territory: " + territory.getName();
    }
}
