package edu.duke.ece651.team6.shared;

import java.util.Map;

public class UpgradeCostRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public UpgradeCostRuleChecker(OrderRuleChecker next, Map<String, Integer> resourse) {
        super(next);
        this.resource = resourse;
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        UpgradeOrder upgrade = (UpgradeOrder) order;
        int nowLevel = upgrade.getNowLevel();
        int targetLevel = upgrade.getTargetLevel();
        int numUnits = upgrade.getNumUnits();
        int cost = UnitManager.costToUpgrade(nowLevel, targetLevel) * numUnits;
        int currTech = resource.get(Constants.RESOURCE_TECH);
        if (currTech < cost) {
            return "Invalid Upgrade: the expected cost of this upgrade is " + cost + " but only have " + currTech + " technology";
        }
        resource.put(Constants.RESOURCE_TECH, currTech - cost);
        return null;
    }
}
