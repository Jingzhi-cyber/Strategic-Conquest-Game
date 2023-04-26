package edu.duke.ece651.team6.shared;

public class CostCalculator {
    private static final int MOVE_CONSTANT = 1;
    private static final int ATTACK_CONSTANT = 1;

    public static int calculateMoveCost(SimpleMove simpleMove, GameMap gameMap) {
        return gameMap.findPathWithLowestCost(simpleMove.src, simpleMove.dest) * simpleMove.getTotalUnits() * MOVE_CONSTANT;
    }

    public static int calculateUltimateMoveCost(SimpleMove simpleMove, GameMap gameMap) {
        return gameMap.findUltimatePathWithLowestCost(simpleMove.src, simpleMove.dest) * simpleMove.getTotalUnits() * MOVE_CONSTANT;
    }

    public static int calculateAttackCost(SimpleMove simpleMove, GameMap gameMap) {
        // TODO: change this function
        return gameMap.getNeighborDist(simpleMove.src).get(simpleMove.dest) * simpleMove.getTotalUnits() * ATTACK_CONSTANT;
    }
}
