package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MoveUnitsRuleCheckerTest {
  @Test
  public void test_checkUnits() {
    MoveUnitsRuleChecker checker = new MoveUnitsRuleChecker(null);
    int[] numUnitsByLevel = { 1, 0, 0, 0, 0, 0, 0 };
    String result = checker.checkMyRule(new MoveOrder(new Territory("A", 1, 3), new Territory("B", 1, 4), numUnitsByLevel), null);
    assertNull(result);

    numUnitsByLevel[0] = -2;
    String result_invalid = checker.checkMyRule(new MoveOrder(new Territory("A", 1, 3), new Territory("B", 1, 4), numUnitsByLevel),
        null);
    assertNotNull(result_invalid);
  }

}
