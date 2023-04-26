package edu.duke.ece651.team6.shared;


public class SanBingOrder extends SimpleMove {
    /**
     * Constructs a simple move
     *
     * @param src
     * @param dest
     * @param numUnitsByLevel
     */
    public SanBingOrder(Territory src, Territory dest, int[] numUnitsByLevel) {
        super("San Bing", src, dest, numUnitsByLevel);
    }

    @Override
    public void takeAction(GameMap gameMap) {
        Territory from = gameMap.getTerritoryByName(src.getName());
        Territory to = gameMap.getTerritoryByName(dest.getName());
        from.attack(to, numUnitsByLevel);
        int cost = CostCalculator.calculateMoveCost(this, gameMap);
        gameMap.consumeResource(from.getOwnerId(), Constants.RESOURCE_FOOD, cost);
    }
}
