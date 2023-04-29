package edu.duke.ece651.team6.shared;

import java.util.Map;

public class GenerateSpyRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public GenerateSpyRuleChecker(OrderRuleChecker next, Map<String, Integer> resource) {
        super(next);
        this.resource = resource;
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        GenerateSpyOrder generateSpy = (GenerateSpyOrder) order;
        Territory t = generateSpy.getTerritory();
        int nowLevel = generateSpy.getNowLevel();
        int num = generateSpy.getNum();
        int currNum = t.getUnitsNumByLevel(nowLevel);
        if (nowLevel == 0) {
            return "Invalid generate spy order: must generate spy from at least level 1";
        }
        if (num > currNum) {
            return "Invalid generate spy order: the territory only has " + currNum + "units at level " + nowLevel + "but requires " + num + "units";
        }
        int cost = 1;
        int currTech = resource.get(Constants.RESOURCE_TECH);
        if (currTech < cost) {
            return "Invalid generate spy order: the expected cost of this order is " + cost + " but only have " + currTech + " technology";
        }
        resource.put(Constants.RESOURCE_TECH, currTech - cost);
        Territory territory = theMap.getTerritoryByName(t.getName());
        territory.removeUnit(nowLevel, num);
        territory.addSpy(generateSpy.getPlayerId(), num);
        return null;
    }
}
