package edu.duke.ece651.team6.shared;

public class UpgradeLevelRuleChecker extends OrderRuleChecker {

    public UpgradeLevelRuleChecker(OrderRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        UpgradeOrder upgrade = (UpgradeOrder) order;
        int playerId = upgrade.getTerritory().getOwnerId();
        int nowLevel = upgrade.getNowLevel();
        int targetLevel = upgrade.getTargetLevel();
        int maxLevel = 6;
        int currTechLevel = theMap.getMaxTechLevel(playerId);
        if (nowLevel < 0 || nowLevel > maxLevel) {
            return "invalid upgrade: illegal nowLevel: " + nowLevel;
        }
        if (targetLevel < 0 || targetLevel > maxLevel) {
            return "invalid upgrade: illegal targetLevel: " + targetLevel;
        }
        if (nowLevel > maxLevel) {
            return "invalid upgrade: target level must be greater than current level";
        }
        if (targetLevel > currTechLevel) {
            return "invalid upgrade: target level is greater than current tech level: " + currTechLevel;
        }
        return null;
    }
}
