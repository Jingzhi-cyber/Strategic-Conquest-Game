package edu.duke.ece651.team6.shared;

public class SuperShieldOrder extends Order {

    private final Territory src;

    public SuperShieldOrder(Territory src) {
        super("Super Shield");
        this.src = src;
    }

    public Territory getTerritory() {
        return this.src;
    }

    public void takeAction(GameMap gameMap) {
        int cost = 1;
        gameMap.consumeResource(src.getOwnerId(), Constants.RESOURCE_TECH, cost);
        Territory from = gameMap.getTerritoryByName(src.getName());
        from.addSuperShield();
    }

    @Override
    public String toString() {
        return getName() + "{ Super Shield: " + src.toString() + "}";
    }
}
