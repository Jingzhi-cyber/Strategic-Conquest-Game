package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ResearchOrderTest {
    @Test
    public void testBasicFunc() {
        ResearchOrder researchOrder = new ResearchOrder(0);
        assertEquals(0, researchOrder.getPlayerId());
        SampleMap sampleMap = new SampleMap();
        GameMap gameMap = new GameMap(sampleMap.getAdjList());
        Map<Integer, Map<String, Integer>> resources = new HashMap<>();
        Map<String, Integer> resource = new HashMap<>();
        resource.put(Constants.RESOURCE_FOOD, 10);
        resource.put(Constants.RESOURCE_TECH, 10);
        resources.put(0, resource);
        gameMap.updateResource(resources);
        gameMap.initMaxTechLevel();
        researchOrder.takeAction(gameMap);
    }
}
