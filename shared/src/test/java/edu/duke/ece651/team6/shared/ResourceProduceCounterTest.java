package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ResourceProduceCounterTest {
    @Test
    public void testBasicFunc() {
        Territory t = new Territory("t", 0);
        Set<Territory> territories = new HashSet<>();
        territories.add(t);
        Set<Integer> playerIds = new HashSet<>();
        playerIds.add(0);
        ResourceProduceCounter resourceProduceCounter = new ResourceProduceCounter(territories, playerIds);
        Map<Integer, Map<String, Integer>> result = resourceProduceCounter.updateAndGetResult();
        assertEquals(5, result.get(0).get(Constants.RESOURCE_FOOD));
        assertEquals(5, result.get(0).get(Constants.RESOURCE_TECH));
    }
}
