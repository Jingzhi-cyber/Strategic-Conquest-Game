package edu.duke.ece651.team6.server;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;

public class RiscMasterTest {
    // @Test
    // public void testCreatePlayerMapInfo() {
    //     RiscMaster rm = new RiscMaster();
    //     HashMap<Territory, HashSet<Territory>> adjList = new HashMap<Territory, HashSet<Territory>>();
    //     Territory t1 = new Territory("Hogwarts", "Player1", 5);
    //     Territory t2 = new Territory("Narnia", "Player2", 3);
    //     Territory t3 = new Territory("Midkemia", "Player3", 1);
    //     HashSet<Territory> n1 = new HashSet<Territory>();
    //     n1.add(t2);
    //     n1.add(t3);
    //     adjList.put(t1, n1);
    //     HashSet<Territory> n2 = new HashSet<Territory>();
    //     n2.add(t1);
    //     adjList.put(t2, n2);
    //     HashSet<Territory> n3 = new HashSet<Territory>();
    //     n3.add(t1);
    //     adjList.put(t3, n3);
    //     GameMap gm = new GameMap(adjList);
    //     rm.setGameMap(gm);
    //     PlayerMapInfo player1MapInfo = rm.createPlayerMapInfo("Player1");
    //     for (Territory t : player1MapInfo.getTerritories()) {
    //         assertEquals("Hogwarts", t.getName());
    //         assertEquals(true, player1MapInfo.getTerritoryNeighbors(t).contains("Narnia"));
    //         assertEquals(true, player1MapInfo.getTerritoryNeighbors(t).contains("Midkemia"));
    //     }
    // }

    // @Test
    // public void testInit() {
    //     RiscMaster rm = new RiscMaster();
    //     assertEquals(true, rm.init());
    // }
}
