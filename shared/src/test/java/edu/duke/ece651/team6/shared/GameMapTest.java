package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class GameMapTest {
  @Test
  public void testBasicFunc() {
    HashMap<Territory, HashSet<Territory>> adjList = new HashMap<Territory, HashSet<Territory>>();
    Territory t1 = new Territory("Hogwarts", 1, 5);
    Territory t2 = new Territory("Narnia", 2, 3);
    Territory t3 = new Territory("Midkemia", 3, 1);
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

    HashSet<Territory> neighbor1 = gm.getNeighborSet(t1);
    assertEquals(true, neighbor1.contains(t2));
    assertEquals(true, neighbor1.contains(t3));

    HashSet<Territory> emptyNeighbor = gm.getNeighborSet(new Territory("not exist", 3, 1));
    assertEquals(true, emptyNeighbor.isEmpty());

    Territory hogwarts = gm.getTerritoryByName("Hogwarts");
    assertEquals(t1, hogwarts);
    
    assertEquals(3, gm.getTerritoryNum());
  }

  @Test
  public void testHasPath() {
    SampleMap sampleMap = new SampleMap();
    GameMap gm = new GameMap(sampleMap.getAdjList());

    Territory Narnia = new Territory("Narnia", 0, 10);
    Territory Midkemia = new Territory("Midkemia", 0, 12);
    Territory Oz = new Territory("Oz", 0, 8);
    Territory Elantris = new Territory("Elantris", 1, 6);
    Territory Scadrial = new Territory("Scadrial", 1, 5);
    Territory Roshar = new Territory("Roshar", 1, 3);
    Territory Gondor = new Territory("Gondor", 2, 13);
    Territory Mordor = new Territory("Mordor", 2, 14);
    Territory Hogwarts = new Territory("Hogwarts", 2, 3);

    assertEquals(true, gm.hasSamePlayerPath(Narnia, Oz));
    assertEquals(false, gm.hasSamePlayerPath(Narnia, Hogwarts));
    assertEquals(false, gm.hasSamePlayerPath(Elantris, Gondor));
    assertEquals(true, gm.hasSamePlayerPath(Elantris, Elantris));
  }
}
