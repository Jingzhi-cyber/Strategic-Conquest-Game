package edu.duke.ece651.team6.shared;

public class GenerateSpyOrder extends Order {
    private int playerId;
    private Territory territory;
    private int nowLevel;
    private int num;
    
    public GenerateSpyOrder(int playerId, Territory territory, int nowLevel, int num) {
        super("GenerateSpy");
        this.playerId = playerId;
        this.territory = territory;
        this.nowLevel = nowLevel;
        this.num = num;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public Territory getTerritory() {
        return this.territory;
    }

    public int getNowLevel() {
        return this.nowLevel;
    }

    public int getNum() {
        return this.num;
    }

    public void takeAction(GameMap gameMap) {
        // TODO: calculate cost
        int cost = 1;
        gameMap.consumeResource(territory.getOwnerId(), Constants.RESOURCE_TECH, cost);
        Territory t = gameMap.getTerritoryByName(territory.getName());
        t.removeUnit(nowLevel, num);
        t.addSpy(playerId, num);
    }

    public String toString() {
        return  "Update unit to spy on Territory: " + territory.getName() + " num of spy: " + this.num;
    }
}
