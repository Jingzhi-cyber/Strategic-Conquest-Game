package edu.duke.ece651.team6.shared;

import java.util.Map;

public class CloakResearchRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public CloakResearchRuleChecker(OrderRuleChecker next, Map<String, Integer> resource) {
        super(next);
        this.resource = resource;
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        CloakResearchOrder cloak = (CloakResearchOrder) order;
        int playerId = cloak.getPlayerId();
        int techLevel = theMap.getMaxTechLevel(playerId);
        if (techLevel < 3) {
            return "Invalid cloak research: requires minimum techlevel: 3 but currently is: " + techLevel;
        }
        if (!theMap.enableCloakForPlayerId(playerId)) {
            return "Invalid cloak research: this player has already researched cloak!";
        }
        int cost = 1;
        int currTech = resource.get(Constants.RESOURCE_TECH);
        if (currTech < cost) {
            return "Invalid cloak: the expected cost of this cloak is " + cost + " but only have " + currTech + " technology";
        }
        resource.put(Constants.RESOURCE_TECH, currTech - cost);
        return null;
    }
}
