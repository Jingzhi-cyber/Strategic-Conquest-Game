package edu.duke.ece651.team6.shared;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleMapTest {
    @Test
    public void testBasicFunc() {
        SimpleMap simpleMap = new SimpleMap();
        Map<Territory, Set<Territory>> adjList = new HashMap<>();
        Territory Narnia = new Territory("Narnia", 0, 0);
        Territory Midkemia = new Territory("Midkemia", 0, 0);

        Set<Territory> NarniaNeighbor = new HashSet<Territory>();
        NarniaNeighbor.add(Midkemia);
        adjList.put(Narnia, NarniaNeighbor);

        Set<Territory> MidkemiaNeighbor = new HashSet<Territory>();
        MidkemiaNeighbor.add(Narnia);
        adjList.put(Midkemia, MidkemiaNeighbor);
        assertEquals(adjList, simpleMap.getAdjList());
    }
}
