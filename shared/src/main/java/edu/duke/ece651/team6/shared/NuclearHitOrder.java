package edu.duke.ece651.team6.shared;

public class NuclearHitOrder extends Order {
    private Territory territory;
    private int playerId;

    public NuclearHitOrder(Territory territory, int playerId) {
        super("NuclearHit");
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
        gameMap.consumeResource(playerId, Constants.RESOURCE_TECH, cost);
        Territory target = gameMap.getTerritoryByName(this.territory.getName());
        target.hitByNuclearWeapon(this.playerId);
    }

    @Override
    public String toString() {
        return getName();
    }
}
