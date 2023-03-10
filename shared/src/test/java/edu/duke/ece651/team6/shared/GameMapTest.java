package edu.duke.ece651.team6.shared;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.Territory;

public class GameMapTest {
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
        GameMap gm = new GameMap(adjList);

        Set<Territory> territories = gm.getTerritorySet();
        assertEquals(true, territories.contains(t1));
        assertEquals(true, territories.contains(t2));
        assertEquals(true, territories.contains(t3));

        HashSet<Territory> neighbor1 = gm.getNeighborSet("Hogwarts");
        assertEquals(true, neighbor1.contains(t2));
        assertEquals(true, neighbor1.contains(t3));

        HashSet<Territory> emptyNeighbor = gm.getNeighborSet("nonExist");
        assertEquals(true, emptyNeighbor.isEmpty());
    }
}
