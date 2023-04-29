package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class MoveSpyOrderTest {
    @Test
    public void testBasicFunc() {
        Territory Narnia = new Territory("Narnia", 0, 0);
        Territory Midkemia = new Territory("Midkemia", 0, 0);
        MoveSpyOrder moveSpyOrder = new MoveSpyOrder(0, Narnia, Midkemia, 0);
        SampleMap sampleMap = new SampleMap();
        GameMap gameMap = new GameMap(sampleMap.getAdjList());
        Map<Integer, Map<String, Integer>> resources = new HashMap<>();
        Map<String, Integer> resource = new HashMap<>();
        resource.put(Constants.RESOURCE_FOOD, 10);
        resource.put(Constants.RESOURCE_TECH, 10);
        resources.put(0, resource);
        gameMap.updateResource(resources);
        gameMap.initMaxTechLevel();
        moveSpyOrder.takeAction(gameMap);
    }
}
