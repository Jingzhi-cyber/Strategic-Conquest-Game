package edu.duke.ece651.team6.shared;

public class ResearchOrder extends Order {
    private int playerId;

    public ResearchOrder(int playerId) {
        super("Research");
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    /**
     * Upgrade the maximum tech level of player with playerId
     * @param gameMap
     */
    public void takeAction(GameMap gameMap) {
        int currLevel = gameMap.getMaxTechLevel(playerId);
        int cost = Constants.researchCosts.get(currLevel);
        gameMap.consumeResource(this.playerId, Constants.RESOURCE_TECH, cost);
        gameMap.upgradeMaxTechLevel(playerId);
    }
}
