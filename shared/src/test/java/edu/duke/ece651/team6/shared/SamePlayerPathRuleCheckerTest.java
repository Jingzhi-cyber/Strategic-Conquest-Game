package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SamePlayerPathRuleCheckerTest {
  GameMap map = mock(GameMap.class);
  Territory territoryA;
  Territory territoryB;
  Territory territoryC;
  Territory territoryD;

  public SamePlayerPathRuleCheckerTest() {
    this.territoryA = new Territory("A", 1, 3);
    this.territoryB = new Territory("B", 1, 5);
    this.territoryC = new Territory("C", 1, 4);
    this.territoryD = new Territory("D", 2, 4);
  }

  @BeforeEach
  public void setUp() {
    when(map.hasSamePlayerPath(territoryA, territoryB)).thenReturn(true);
    when(map.hasSamePlayerPath(territoryA, territoryC)).thenReturn(true);
    when(map.hasSamePlayerPath(territoryA, territoryD)).thenReturn(false);
    when(map.hasSamePlayerPath(territoryB, territoryC)).thenReturn(true);
  }

  @Test
  public void test_checkOrder() {
    SamePlayerPathRuleChecker checker = new SamePlayerPathRuleChecker(null);

    int[] numUnitsByLevel = { 1, 0, 0, 0, 0, 0, 0 };
    assertNull(checker.checkOrder(new MoveOrder(territoryA, territoryB, numUnitsByLevel), map));
    assertNull(checker.checkOrder(new MoveOrder(territoryB, territoryC, numUnitsByLevel), map));
    assertNull(checker.checkOrder(new MoveOrder(territoryA, territoryC, numUnitsByLevel), map));
    assertNotNull(checker.checkOrder(new MoveOrder(territoryA, territoryD, numUnitsByLevel), map));
  }
}
