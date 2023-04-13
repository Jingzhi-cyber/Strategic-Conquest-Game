package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class AttackCostRuleCheckerTest {
    @Test
    public void testCheckRule() {
        Map<String, Integer> resources = new HashMap<>();
        SampleMap sampleMap = new SampleMap();
        GameMap gameMap = new GameMap(sampleMap.getAdjList());
        Territory Narnia = new Territory("Narnia", 0, 10);
        Territory Elantris = new Territory("Elantris", 0, 8);
        int[] numUnitsByLevel = { 5, 0, 0, 0, 0, 0, 0 };
        SimpleMove simpleMove = new AttackOrder(Narnia, Elantris, numUnitsByLevel);
        AttackCostRuleChecker attackCostRuleChecker = new AttackCostRuleChecker(null, resources);
        assertNotNull(attackCostRuleChecker.checkMyRule(simpleMove, gameMap));
    }
}
