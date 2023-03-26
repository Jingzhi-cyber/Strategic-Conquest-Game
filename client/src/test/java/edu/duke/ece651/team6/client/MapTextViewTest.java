package edu.duke.ece651.team6.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;

public class MapTextViewTest {
  @Test
  public void testBasicFunc() {
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    Territory t1 = new Territory("Hogwarts", 1, 5);
    Territory t2 = new Territory("Narnia", 2, 3);
    Territory t3 = new Territory("Midkemia", 3, 1);
    Set<Territory> n1 = new HashSet<>();
    n1.add(t2);
    n1.add(t3);
    adjList.put(t1, n1);
    Set<Territory> n2 = new HashSet<>();
    n2.add(t1);
    adjList.put(t2, n2);
    Set<Territory> n3 = new HashSet<>();
    n3.add(t1);
    adjList.put(t3, n3);

    Map<Territory, Set<String>> info = new HashMap<>();
    Set<Territory> territories = adjList.keySet();
    for (Territory t : territories) {
      if (t.getOwnerId() == 1) {
        Set<String> neighbor = new HashSet<String>();
        for (Territory n : adjList.get(t)) {
          neighbor.add(n.getName());
        }
        info.put(t, neighbor);
      }
    }
    PlayerMapInfo playerMapInfo = new PlayerMapInfo(1, info);
    PlayerMapInfo emptyPlayerMapInfo = new PlayerMapInfo(5, new HashMap<>());
    GlobalMapInfo globalMapInfo = new GlobalMapInfo();
    globalMapInfo.addPlayerMapInfo(playerMapInfo);
    globalMapInfo.addPlayerMapInfo(emptyPlayerMapInfo);
    String expected = "Player1:\n-------------\n5 units in Hogwarts (next to: Narnia Midkemia)\n\n";
    expected += "Player5:\n-------------\nThis player does not own any territory\n\n";
    MapTextView mtv = new MapTextView(globalMapInfo);
    assertEquals(expected, mtv.display());
  }
}
