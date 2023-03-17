package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class GlobalMapInfoTest {

  @Test
  public void testBasicFunc() {

    Territory t1 = new Territory("Hogwarts", 1, 5);
    Territory t2 = new Territory("Narnia", 2, 3);
    Territory t3 = new Territory("Midkemia", 3, 1);

    HashMap<Territory, HashSet<String>> info1 = new HashMap<Territory, HashSet<String>>();
    info1.put(t1, new HashSet<String>() {
      {
        add("Narnia");
        add("Midkemia");
      }
    });

    HashMap<Territory, HashSet<String>> info2 = new HashMap<Territory, HashSet<String>>();
    info2.put(t2, new HashSet<String>() {
      {
        add("Hogwarts");
        add("Midkemia");
      }
    });

    HashMap<Territory, HashSet<String>> info3 = new HashMap<Territory, HashSet<String>>();
    info3.put(t3, new HashSet<String>() {
      {
        add("Hogwarts");
        add("Narnia");
      }
    });

    PlayerMapInfo playerMapInfo1 = new PlayerMapInfo(1, info1);
    PlayerMapInfo playerMapInfo2 = new PlayerMapInfo(2, info2);
    PlayerMapInfo playerMapInfo3 = new PlayerMapInfo(3, info3);
    GlobalMapInfo globalMapInfo = new GlobalMapInfo();
    globalMapInfo.addPlayerMapInfo(playerMapInfo1);
    globalMapInfo.addPlayerMapInfo(playerMapInfo2);
    globalMapInfo.addPlayerMapInfo(playerMapInfo3);

    assertEquals(true, globalMapInfo.getPlayers().contains(1));
    PlayerMapInfo newInfo = globalMapInfo.getPlayerMapInfo(1);
    for (Territory t : newInfo.getTerritories()) {
      assertEquals("Hogwarts", t.getName());
      assertEquals(true, playerMapInfo1.getTerritoryNeighbors(t).contains("Narnia"));
      assertEquals(true, playerMapInfo1.getTerritoryNeighbors(t).contains("Midkemia"));
    }

    HashSet<String> names = new HashSet<String>() {
      {
        add("Hogwarts");
        add("Narnia");
        add("Midkemia");
      }
    };

    // System.out.println(names);
    // System.out.println(globalMapInfo.getTerritoryNames());
    assertEquals(names, globalMapInfo.getTerritoryNames());
  }
}
