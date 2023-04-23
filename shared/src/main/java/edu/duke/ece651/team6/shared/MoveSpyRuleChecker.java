package edu.duke.ece651.team6.shared;

import java.util.Map;

public class MoveSpyRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public MoveSpyRuleChecker(OrderRuleChecker next, Map<String, Integer> resource) {
        super(next);
        this.resource = resource;
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        MoveSpyOrder moveSpy = (MoveSpyOrder) order;
        int playerId = moveSpy.playerId;
        Territory from = moveSpy.src;
        Territory to = moveSpy.dest;
        int num = moveSpy.num;
        int spyNum = from.getSpyNumByPlayerId(playerId);
        int cost = 1;
        if (num > spyNum) {
            return "Invalid MoveSpy Order: only has " + spyNum + "Spies on the territory but requires " + num + "spies";
        }
        if (from.getOwnerId() != playerId) {
            if (!theMap.getNeighborDist(from).keySet().contains(to)) {
                return "Invalid MoveSpy Order: spy is on enemy's territory, can only move to adjacent territories";
            }
        }
        else {
            if (!theMap.hasSamePlayerPath(from, to)) {
                return "Invalid MoveSpy Order: no valid route to move from self territory";
            }
        }
        int currTech = resource.get(Constants.RESOURCE_TECH);
        if (currTech < cost) {
            return "Invalid MoveSpy order: the expected cost of this order is " + cost + " but only have " + currTech + " technology";
        }
        resource.put(Constants.RESOURCE_TECH, currTech - cost);
        Territory src = theMap.getTerritoryByName(from.getName());
        Territory dest = theMap.getTerritoryByName(to.getName());
        src.moveSpyTo(playerId, dest, num);
        return null;
    }
}
