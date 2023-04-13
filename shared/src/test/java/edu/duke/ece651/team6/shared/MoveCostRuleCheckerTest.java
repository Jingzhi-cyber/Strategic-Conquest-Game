package edu.duke.ece651.team6.shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import java.util.HashMap;
import java.util.Map;

public class MoveCostRuleCheckerTest {
    @Test
    public void testCheckRule() {
        Map<String, Integer> resources = new HashMap<>();
        SampleMap sampleMap = new SampleMap();
        GameMap gameMap = new GameMap(sampleMap.getAdjList());
        Territory Narnia = new Territory("Narnia", 0, 10);
        Territory Oz = new Territory("Oz", 0, 8);
        int[] numUnitsByLevel = { 5, 0, 0, 0, 0, 0, 0 };
        SimpleMove simpleMove = new MoveOrder(Narnia, Oz, numUnitsByLevel);
        MoveCostRuleChecker moveCostRuleChecker = new MoveCostRuleChecker(null, resources);
        assertNotNull(moveCostRuleChecker.checkMyRule(simpleMove, gameMap));
    }
}
