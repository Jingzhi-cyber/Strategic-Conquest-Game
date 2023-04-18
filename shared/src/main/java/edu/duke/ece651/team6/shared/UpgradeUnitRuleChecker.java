package edu.duke.ece651.team6.shared;

public class UpgradeUnitRuleChecker extends OrderRuleChecker {

    public UpgradeUnitRuleChecker(OrderRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        UpgradeOrder upgrade = (UpgradeOrder) order;
        Territory t = upgrade.getTerritory();
        int nowLevel = upgrade.getNowLevel();
        int targetLevel = upgrade.getTargetLevel();
        int numUnits = upgrade.getNumUnits();
        int nowUnitNum = t.getUnitsNumByLevel(nowLevel);
        if (nowUnitNum < numUnits) {
            return "Invalid upgrade: the upgrade unit num is: " + numUnits + " but the territory only has " + nowUnitNum + " units";
        }
        Territory territory = theMap.getTerritoryByName(t.getName());
        System.out.println("num of target level of units: " + territory.getUnitsNumByLevel(targetLevel));
        for (int i = 0; i < numUnits; i++) {
            territory.upgradeOneUnit(nowLevel, targetLevel);
        }
        System.out.println("num of target level of units after operation: " + territory.getUnitsNumByLevel(targetLevel));
        return null;
    }
}
