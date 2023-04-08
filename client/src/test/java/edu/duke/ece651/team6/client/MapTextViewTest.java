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
    Map<Territory, Map<Territory, Integer>> info = new HashMap<>();
    Territory t1 = new Territory("Hogwarts", 1, 5);
    Territory t2 = new Territory("Narnia", 2, 3);
    Territory t3 = new Territory("Midkemia", 3, 1);
    info.put(t1, new HashMap<>());
    info.get(t1).put(t2, 1);
    info.get(t1).put(t3, 2);

    PlayerMapInfo playerMapInfo = new PlayerMapInfo(1, info);
    PlayerMapInfo emptyPlayerMapInfo = new PlayerMapInfo(5, new HashMap<>());
    GlobalMapInfo globalMapInfo = new GlobalMapInfo();
    globalMapInfo.addPlayerMapInfo(playerMapInfo);
    globalMapInfo.addPlayerMapInfo(emptyPlayerMapInfo);
    String expected = "Player1:\n-------------\nIn Hogwarts: \nnext to: \nNarnia Distance: 1\nMidkemia Distance: 2\n\nlevel 0: 5 units\n";
    for (int level = 1; level < t1.getNumLevels(); level++) {
      expected += "level " + level + ": 0 units\n";
    }
    expected += "Player5:\n-------------\nThis player does not own any territory\n\n";
    MapTextView mtv = new MapTextView(globalMapInfo);
    assertEquals(expected, mtv.display());
  }
}
