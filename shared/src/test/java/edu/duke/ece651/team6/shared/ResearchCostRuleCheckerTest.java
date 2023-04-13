package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ResearchCostRuleCheckerTest {
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

        ResearchOrder researchOrder = new ResearchOrder(0);
        ResearchCostRuleChecker researchCostRuleChecker = new ResearchCostRuleChecker(null, resource);
        researchCostRuleChecker.checkMyRule(researchOrder, gameMap);
        for (int i = 0; i < 4; i++) {
            gameMap.upgradeMaxTechLevel(0);
        }
        assertEquals(5, gameMap.getMaxTechLevel(0));
        researchCostRuleChecker.checkMyRule(researchOrder, gameMap);
    }
}
