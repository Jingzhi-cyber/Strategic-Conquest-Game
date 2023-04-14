package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class AttackUnitsRuleCheckerTest {
  @Test
  public void test_checkUnits() {

  }

  GameMap map = mock(GameMap.class);
  Territory territoryA;
  Territory territoryB;
  Territory territoryC;

  public AttackUnitsRuleCheckerTest() {
    this.territoryA = new Territory("A", 1, 3);
    this.territoryB = new Territory("B", 2, 5);
    this.territoryC = new Territory("C", 2, 4);
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    adjList.put(territoryA, new HashSet<>());
    adjList.put(territoryB, new HashSet<>());
    adjList.put(territoryC, new HashSet<>());
    map = new GameMap(adjList);
  }

  @Test
  public void test_checkOrder() {
    AttackUnitsRuleChecker checker = new AttackUnitsRuleChecker(null);
    AttackUnitsRuleChecker checker2 = new AttackUnitsRuleChecker(null);

    int[] numUnitsByLevel = {0, 0, 0, 0, 0, 0, 0};
    assertNull(checker.checkOrder(new AttackOrder(territoryA, territoryB, numUnitsByLevel), map));
    numUnitsByLevel[0] = 4;
    assertNotNull(checker.checkOrder(new AttackOrder(territoryA, territoryB, numUnitsByLevel), map));
    numUnitsByLevel[0] = 3;
    assertNull(checker.checkOrder(new AttackOrder(territoryA, territoryB, numUnitsByLevel), map));
    numUnitsByLevel[0] = -1;
    assertNotNull(checker2.checkOrder(new AttackOrder(territoryC, territoryA, numUnitsByLevel), map));
  }

}
