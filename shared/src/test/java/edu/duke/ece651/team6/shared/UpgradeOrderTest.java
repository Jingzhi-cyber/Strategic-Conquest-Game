package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UpgradeOrderTest {
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
        narnia.initNumUnits(10);
        UpgradeOrder upgradeOrder = new UpgradeOrder(narnia, 0, 1, 1);
        assertEquals(narnia, upgradeOrder.getTerritory());
        assertEquals(0, upgradeOrder.getNowLevel());
        assertEquals(1, upgradeOrder.getTargetLevel());
        assertEquals(1, upgradeOrder.getNumUnits());
        upgradeOrder.takeAction(gameMap);
    }
}
