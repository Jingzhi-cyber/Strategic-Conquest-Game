package edu.duke.ece651.team6.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.Territory;

public class MapGeneratorTest {
    @Test
    void testGetTheMap() {
        MapGenerator m = new MapGenerator(15, "Hello", "Nihao", "Hi", "yeah");
        int a = 0, b = 0, c = 0, d = 0;
        Map<Territory, Set<Territory>> map = m.getTheMap();
        for (Territory t : map.keySet()) {
            if (t.getName().equals("Hello")) {
                a = 1;
            } else if (t.getName().equals("Nihao")) {
                b = 1;
            } else if (t.getName().equals("Hi")) {
                c = 1;
            } else if (t.getName().equals("yeah")) {
                d = 1;
            }
        }
        assertEquals(1, a);
        assertEquals(1, b);
        assertEquals(1, c);
        assertEquals(1, d);
        assertThrows(RuntimeException.class, () -> new MapGenerator(1000));
        Map<Territory, Map<Territory, Double>> distMap = m.getDistanceMap();
        Set<Territory> set = distMap.keySet();
        for (Territory t1 : set) {
            Map<Territory, Double> dist = distMap.get(t1);
            for (Territory t2 : dist.keySet()) {
                assertEquals(dist.get(t2), distMap.get(t2).get(t1));
            }
        }
    }
}
