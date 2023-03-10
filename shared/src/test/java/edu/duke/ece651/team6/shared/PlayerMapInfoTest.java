package edu.duke.ece651.team6.shared;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PlayerMapInfoTest {
    @Test
    public void testBasicFunc() {
        HashMap<Territory, HashSet<Territory>> adjList = new HashMap<Territory, HashSet<Territory>>();
        Territory t1 = new Territory("Hogwarts", "Player1", 5);
        Territory t2 = new Territory("Narnia", "Player2", 3);
        Territory t3 = new Territory("Midkemia", "Player3", 1);
        HashSet<Territory> n1 = new HashSet<Territory>();
        n1.add(t2);
        n1.add(t3);
        adjList.put(t1, n1);
        HashSet<Territory> n2 = new HashSet<Territory>();
        n2.add(t1);
        adjList.put(t2, n2);
        HashSet<Territory> n3 = new HashSet<Territory>();
        n3.add(t1);
        adjList.put(t3, n3);

        HashMap<Territory, HashSet<String>> info = new HashMap<Territory, HashSet<String>>();
        Set<Territory> territories = adjList.keySet();
        for (Territory t : territories) {
            if (t.getOwner().equals("Player1")) {
                HashSet<String> neighbor = new HashSet<String>();
                for (Territory n : adjList.get(t)) {
                    neighbor.add(n.getName());
                }
                info.put(t, neighbor);
            }
        }
        PlayerMapInfo playerMapInfo = new PlayerMapInfo(info);
        for (Territory t : playerMapInfo.getTerritories()) {
            assertEquals("Hogwarts", t.getName());
            assertEquals(true, playerMapInfo.getTerritoryNeighbors(t).contains("Narnia"));
            assertEquals(true, playerMapInfo.getTerritoryNeighbors(t).contains("Midkemia"));
        }
    }
}