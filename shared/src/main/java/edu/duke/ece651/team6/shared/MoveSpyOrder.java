package edu.duke.ece651.team6.shared;

public class MoveSpyOrder extends Order {
    int playerId;
    Territory src;
    Territory dest;
    int num;

    public MoveSpyOrder(int playerId, Territory src, Territory dest, int num) {
        super("MoveSpy");
        this.playerId = playerId;
        this.src = src;
        this.dest = dest;
        this.num = num;
    }

    public void takeAction(GameMap gameMap) {
        Territory from = gameMap.getTerritoryByName(src.getName());
        Territory to = gameMap.getTerritoryByName(dest.getName());
        from.moveSpyTo(playerId, to, num);
        // TODO: calculate cost
        int cost = 1;
        gameMap.consumeResource(playerId, Constants.RESOURCE_FOOD, cost);
      }
}
