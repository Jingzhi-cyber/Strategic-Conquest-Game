package edu.duke.ece651.team6.shared;

import java.util.Map;

public class ResearchCostRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public ResearchCostRuleChecker(OrderRuleChecker next, Map<String, Integer> resourse) {
        super(next);
        this.resource = resourse;
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        int playerId = ((ResearchOrder) order).getPlayerId();
        int currLevel = theMap.getMaxTechLevel(playerId);
        if (currLevel == 6) {
            return "Invalid research: the player has reached maximum technology level";
        }
        int cost = Constants.researchCosts.get(currLevel);
        int currTech = resource.getOrDefault(Constants.RESOURCE_TECH, 0);
        if (cost > currTech) {
            return "Invalid research: the expected cost of this research is: " + cost + " but you have " + currTech + " tech left";
        }
        // theMap.upgradeMaxTechLevel(playerId);
        resource.put(Constants.RESOURCE_TECH, currTech - cost);
        return null;
    }
}
