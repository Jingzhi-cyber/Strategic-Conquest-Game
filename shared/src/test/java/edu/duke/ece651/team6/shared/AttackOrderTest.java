package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class AttackOrderTest {
  Territory A = new Territory("A", 1, 3);
  Territory B = new Territory("B", 2, 4);
  Territory C = new Territory("C", 2, 3);

  private AttackOrder createAttackOrder() {
    AttackOrder move = new AttackOrder(A, B, 2);
    return move;
  }

  private GameMap createGameMap() {
    Map<Territory, Set<Territory>> adjList = new HashMap<Territory, Set<Territory>>();
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
  public void test_takeAction() {
    GameMap gameMap = createGameMap();
    AttackOrder attack = createAttackOrder();
    attack.takeAction(gameMap);
    assertEquals(A, gameMap.getTerritoryByName(A.getName()));
    assertEquals(B, gameMap.getTerritoryByName(B.getName()));
    assertEquals(C, gameMap.getTerritoryByName(C.getName()));
  }
}
