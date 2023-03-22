package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MoveUnitsRuleCheckerTest {
  @Test
  public void test_checkUnits() {
    MoveUnitsRuleChecker checker = new MoveUnitsRuleChecker(null);
    String result = checker.checkMyRule(new MoveOrder(new Territory("A", 1, 3), new Territory("B", 1, 4), 1), null);
    assertNull(result);

    String result_invalid = checker.checkMyRule(new MoveOrder(new Territory("A", 1, 3), new Territory("B", 1, 4), -2),
        null);
    assertNotNull(result_invalid);
  }

}
