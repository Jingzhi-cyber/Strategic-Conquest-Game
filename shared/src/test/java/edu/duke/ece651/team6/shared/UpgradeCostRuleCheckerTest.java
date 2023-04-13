package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UpgradeCostRuleCheckerTest {
    @Test
    public void testBasicFunc() {
        SampleMap sampleMap = new SampleMap();
        GameMap gameMap = new GameMap(sampleMap.getAdjList());
        Map<Integer, Map<String, Integer>> resources = new HashMap<>();
        Map<String, Integer> resource = new HashMap<>();
        resource.put(Constants.RESOURCE_FOOD, 100);
        resource.put(Constants.RESOURCE_TECH, 10);
        resources.put(0, resource);
        gameMap.updateResource(resources);
        gameMap.initMaxTechLevel();

        Territory narnia = gameMap.getTerritoryByName("Narnia");
        UpgradeOrder invalid1 = new UpgradeOrder(narnia, 0, 1, 100);
        UpgradeOrder valid = new UpgradeOrder(narnia, 0, 1, 1);
        UpgradeCostRuleChecker upgradeCostRuleChecker = new UpgradeCostRuleChecker(null, resource);
        upgradeCostRuleChecker.checkMyRule(invalid1, gameMap);
        upgradeCostRuleChecker.checkMyRule(valid, gameMap);
    }
}