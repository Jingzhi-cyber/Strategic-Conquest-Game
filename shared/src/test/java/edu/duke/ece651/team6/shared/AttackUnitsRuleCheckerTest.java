package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

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
  }

  @Test
  public void test_checkOrder() {
    AttackUnitsRuleChecker checker = new AttackUnitsRuleChecker(null);

    assertNotNull(checker.checkOrder(new AttackOrder(territoryA, territoryB, 0), map));
    assertNotNull(checker.checkOrder(new AttackOrder(territoryA, territoryB, 4), map));
    assertNull(checker.checkOrder(new AttackOrder(territoryA, territoryB, 3), map));
    assertNotNull(checker.checkOrder(new AttackOrder(territoryC, territoryA, -1), map));
  }

}
