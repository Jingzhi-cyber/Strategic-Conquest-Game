package edu.duke.ece651.team6.shared;

public class CloakResearchOrder extends Order {
    int playerId;

    public CloakResearchOrder(int playerId) {
        super("CloakResearch");
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void takeAction(GameMap gameMap) {
        int cost = 1;
        gameMap.consumeResource(playerId, Constants.RESOURCE_TECH, cost);
        gameMap.enableCloakForPlayerId(playerId);
    }

    public String toString() {
        return getName();
    }
    
}
