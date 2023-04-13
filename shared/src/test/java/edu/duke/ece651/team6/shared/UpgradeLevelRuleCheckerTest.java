package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UpgradeLevelRuleCheckerTest {
    @Test
    public void testBasicFunc() {
        SampleMap sampleMap = new SampleMap();
        GameMap gameMap = new GameMap(sampleMap.getAdjList());
        Map<Integer, Map<String, Integer>> resources = new HashMap<>();
        Map<String, Integer> resource = new HashMap<>();
        resource.put(Constants.RESOURCE_FOOD, 100);
        resource.put(Constants.RESOURCE_TECH, 100);
        resources.put(0, resource);
        gameMap.updateResource(resources);
        gameMap.initMaxTechLevel();

        Territory narnia = gameMap.getTerritoryByName("Narnia");
        UpgradeOrder invalid1 = new UpgradeOrder(narnia, -1, 1, 1);
        UpgradeOrder invalid2 = new UpgradeOrder(narnia, 0, -1, 1);
        UpgradeOrder invalid3 = new UpgradeOrder(narnia, 2, 1, 1);
        UpgradeOrder valid = new UpgradeOrder(narnia, 0, 1, 1);
        UpgradeLevelRuleChecker upgradeLevelRuleChecker = new UpgradeLevelRuleChecker(null);
        upgradeLevelRuleChecker.checkMyRule(invalid1, gameMap);
        upgradeLevelRuleChecker.checkMyRule(invalid2, gameMap);
        upgradeLevelRuleChecker.checkMyRule(invalid3, gameMap);
        upgradeLevelRuleChecker.checkMyRule(valid, gameMap);
    }
}
