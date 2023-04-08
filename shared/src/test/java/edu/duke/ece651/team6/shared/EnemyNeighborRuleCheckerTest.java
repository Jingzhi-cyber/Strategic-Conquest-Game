package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnemyNeighborRuleCheckerTest {

  GameMap gameMap = mock(GameMap.class);
  Territory t1 = new Territory("t1", 1, 6);
  Territory t2 = new Territory("t2", 2, 4);
  Territory t3 = new Territory("t3", 2, 3);

  @BeforeEach
  public void setUp() {
    when(gameMap.getNeighborDist(t1)).thenReturn(new HashMap<Territory, Integer>() {
      {
        put(t2, 1);
      }
    });
  }

  @Test
  public void test_enemyNeighbor() {
    OrderRuleChecker checker = new EnemyNeighborRuleChecker(null);
    int[] numUnitsByLevel = {2, 0, 0, 0, 0, 0, 0};
    AttackOrder validAttack = new AttackOrder(t1, t2, numUnitsByLevel);
    AttackOrder invalidAttack = new AttackOrder(t1, t3, numUnitsByLevel);
    AttackOrder invalidAttack2 = new AttackOrder(t1, t1, numUnitsByLevel);
    assertNull(checker.checkOrder(validAttack, gameMap));
    assertNotNull(checker.checkOrder(invalidAttack, gameMap));
    assertNotNull(checker.checkOrder(invalidAttack2, gameMap));
  }

}
