package edu.duke.ece651.team6.shared;

public class EliminateFogOrder extends Order {
    private Territory territory;
    private int playerId;

    public EliminateFogOrder(Territory territory, int playerId) {
        super("EliminateFog");
        this.territory = territory;
        this.playerId = playerId;
    }

    public Territory getTerritory() {
        return this.territory;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void takeAction(GameMap gameMap) {
        int cost = 1;
        gameMap.consumeResource(territory.getOwnerId(), Constants.RESOURCE_TECH, cost);
        Territory target = gameMap.getTerritoryByName(this.territory.getName());
        target.addVisiblePlayer(playerId);
    }

    @Override
    public String toString() {
        return getName();
    }
}
