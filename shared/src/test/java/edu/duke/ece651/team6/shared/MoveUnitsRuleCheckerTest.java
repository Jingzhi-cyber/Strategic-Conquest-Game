package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class MoveUnitsRuleCheckerTest {
  @Test
  public void test_checkUnits() {
    Territory A = new Territory("A", 1, 3);
    Territory B = new Territory("B", 1, 4);
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    adjList.put(A, new HashSet<>());
    adjList.put(B, new HashSet<>());
    GameMap gameMap = new GameMap(adjList);
    MoveUnitsRuleChecker checker = new MoveUnitsRuleChecker(null);
    int[] numUnitsByLevel = { 1, 0, 0, 0, 0, 0, 0 };
    String result = checker.checkMyRule(new MoveOrder(new Territory("A", 1, 3), new Territory("B", 1, 4), numUnitsByLevel), gameMap);
    assertNull(result);

    numUnitsByLevel[0] = -2;
    String result_invalid = checker.checkMyRule(new MoveOrder(new Territory("A", 1, 3), new Territory("B", 1, 4), numUnitsByLevel),
        gameMap);
    assertNotNull(result_invalid);
  }

}
