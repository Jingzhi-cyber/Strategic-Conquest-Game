package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SrcOwerIdRuleCheckerTest {
  @Test
  public void test_checkOwnerId() {
    SrcOwerIdRuleChecker checker = new SrcOwerIdRuleChecker(null, 2);
    Territory t1 = new Territory("t1", 1, 2);
    Territory t2 = new Territory("t2", 2, 3);

    assertNotNull(checker.checkOrder(new AttackOrder(t1, t2, 2), null));
    assertNull(checker.checkOrder(new AttackOrder(t2, t1, 2), null));
  }

}
