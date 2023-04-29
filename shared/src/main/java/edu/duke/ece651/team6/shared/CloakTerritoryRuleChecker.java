package edu.duke.ece651.team6.shared;

import java.util.Map;

public class CloakTerritoryRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public CloakTerritoryRuleChecker(OrderRuleChecker next, Map<String, Integer> resource) {
        super(next);
        this.resource = resource;
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        CloakTerritoryOrder cloak = (CloakTerritoryOrder) order;
        int playerId = cloak.getTerritory().getOwnerId();
        if (!theMap.isEnabledCloakOfPlayerId(playerId)) {
            return "Invalid cloak: this player has not researched cloak";
        }
        int cost = 1;
        int currTech = resource.get(Constants.RESOURCE_TECH);
        if (currTech < cost) {
            return "Invalid cloak: the expected cost of this cloak is " + cost + " but only have " + currTech + " technology";
        }
        resource.put(Constants.RESOURCE_TECH, currTech - cost);
        Territory t = theMap.getTerritoryByName(cloak.getTerritory().getName());
        t.setCloakedTurn(3);
        return null;
    }

}
