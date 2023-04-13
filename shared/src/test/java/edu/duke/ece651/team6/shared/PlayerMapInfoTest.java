package edu.duke.ece651.team6.shared;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerMapInfoTest {
    @Test
    public void testBasicFunc() {
        Map<Territory, Map<Territory, Integer>> adjList = new HashMap<>();
        Territory t1 = new Territory("Hogwarts", 1, 5);
        Territory t2 = new Territory("Narnia", 2, 3);
        Territory t3 = new Territory("Midkemia", 3, 1);
        Map<Territory, Integer> n1 = new HashMap<>();
        n1.put(t2, 1);
        n1.put(t3, 1);
        adjList.put(t1, n1);
        Map<Territory, Integer> n2 = new HashMap<>();
        n2.put(t1, 1);
        adjList.put(t2, n2);
        Map<Territory, Integer> n3 = new HashMap<>();
        n3.put(t1, 1);
        adjList.put(t3, n3);

        Map<Territory, Map<Territory, Integer>> info = new HashMap<>();
        for (Territory t : adjList.keySet()) {
            if (t.getOwnerId() == 1) {
                info.put(t, adjList.get(t));
            }
        }
        PlayerMapInfo playerMapInfo = new PlayerMapInfo(1, info);
        assertEquals(1, playerMapInfo.getPlayerId());
        for (Territory t : playerMapInfo.getTerritories()) {
            assertEquals("Hogwarts", t.getName());
            assertEquals(true, playerMapInfo.getTerritoryNeighbors(t).containsKey(t2));
            assertEquals(true, playerMapInfo.getTerritoryNeighbors(t).containsKey(t3));
        }
    }
}
