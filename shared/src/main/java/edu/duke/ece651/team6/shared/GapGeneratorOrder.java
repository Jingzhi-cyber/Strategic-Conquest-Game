package edu.duke.ece651.team6.shared;

public class GapGeneratorOrder extends Order {
    Territory territory;

    public GapGeneratorOrder (Territory territory) {
        super("GapGenerator");
        this.territory = territory;
    }

    public Territory getTerritory() {
        return this.territory;
    }

    public void takeAction(GameMap gameMap) {
        int cost = 1;
        gameMap.consumeResource(territory.getOwnerId(), Constants.RESOURCE_TECH, cost);
        Territory target = gameMap.getTerritoryByName(this.territory.getName());
        target.setGapGenerated();
    }

    @Override
    public String toString() {
        return getName();
    }
    
}
