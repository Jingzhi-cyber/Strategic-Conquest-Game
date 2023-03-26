package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class MoveOrderTest {
  Territory A = new Territory("A", 1, 3);
  Territory B = new Territory("B", 1, 4);
  Territory C = new Territory("C", 1, 3);

  private MoveOrder createMoveOrder() {
    MoveOrder move = new MoveOrder(A, B, 2);
    return move;
  }

  private GameMap createGameMap() {
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    Set<Territory> setA = new HashSet<Territory>() {
      {
        add(B);
      }
    };
    adjList.put(A, setA);

    Set<Territory> setB = new HashSet<Territory>() {
      {
        add(A);
        add(C);
      }
    };
    adjList.put(B, setB);

    Set<Territory> setC = new HashSet<Territory>() {
      {
        add(B);
      }
    };
    adjList.put(C, setC);
    return new GameMap(adjList);
  }

  @Test
  public void test_toString() {
    MoveOrder move = new MoveOrder(new Territory("A", 1, 3), new Territory("B", 1, 4), 2);
    String expected = "{ from: (name: A, ownerId: 1, units: 3), to: (name: B, ownerId: 1, units: 4), numUnits: 2 }";
    assertEquals(expected, move.toString());
  }

  @Test
  public void test_takeAction() {
    GameMap gameMap = createGameMap();
    MoveOrder move = createMoveOrder();
    move.takeAction(gameMap);
    assertEquals(A, gameMap.getTerritoryByName(A.getName()));
    assertEquals(B, gameMap.getTerritoryByName(B.getName()));
    assertEquals(C, gameMap.getTerritoryByName(C.getName()));
  }

}
