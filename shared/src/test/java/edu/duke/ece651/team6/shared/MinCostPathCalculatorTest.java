package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MinCostPathCalculatorTest {
    @Test
    public void testCalculateMinCostPath() {
        SampleMap sampleMap = new SampleMap();
        GameMap gameMap = new GameMap(sampleMap.getAdjList());
        Territory Elantris = new Territory("Elantris", 1, 6);
        Territory Scadrial = new Territory("Scadrial", 1, 6);
        Territory Roshar = new Territory("Roshar", 1, 6);
        assertEquals(0, gameMap.findPathWithLowestCost(Elantris, Elantris));
        assertNotEquals(0, gameMap.findPathWithLowestCost(Scadrial, Roshar));
        assertNotEquals(0, gameMap.findPathWithLowestCost(Elantris, Roshar));
    }
}
