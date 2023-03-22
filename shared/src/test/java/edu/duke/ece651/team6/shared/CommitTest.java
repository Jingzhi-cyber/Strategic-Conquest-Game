package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommitTest {
  Territory t0 = new Territory("0", 1, 3);
  Territory t1 = new Territory("1", 1, 1);
  Territory t2 = new Territory("2", 1, 5);
  Territory t3 = new Territory("3", 2, 2);
  Territory t4 = new Territory("4", 2, 4);
  GameMap gameMap = mock(GameMap.class);

  @BeforeEach
  public void setUp() {
    /* t1 and t2 belong to player1, and they are connected */
    when(gameMap.hasSamePlayerPath(t1, t1)).thenReturn(true);
    when(gameMap.hasSamePlayerPath(t1, t2)).thenReturn(true);
    when(gameMap.hasSamePlayerPath(t2, t1)).thenReturn(true);
    when(gameMap.hasSamePlayerPath(t2, t2)).thenReturn(true);

    /* t3 and t4 belong to player2, but they are not connected */
    when(gameMap.hasSamePlayerPath(t3, t3)).thenReturn(true);
    when(gameMap.hasSamePlayerPath(t3, t4)).thenReturn(false);
    when(gameMap.hasSamePlayerPath(t4, t4)).thenReturn(true);
    when(gameMap.hasSamePlayerPath(t4, t3)).thenReturn(false);

    when(gameMap.hasSamePlayerPath(t1, t3)).thenReturn(false);
    when(gameMap.hasSamePlayerPath(t1, t4)).thenReturn(false);
    when(gameMap.hasSamePlayerPath(t2, t3)).thenReturn(false);
    when(gameMap.hasSamePlayerPath(t2, t4)).thenReturn(false);

    when(gameMap.hasSamePlayerPath(t3, t1)).thenReturn(false);
    when(gameMap.hasSamePlayerPath(t3, t2)).thenReturn(false);
    when(gameMap.hasSamePlayerPath(t4, t1)).thenReturn(false);
    when(gameMap.hasSamePlayerPath(t4, t2)).thenReturn(false);

    when(gameMap.getNeighborSet(t0)).thenReturn(new HashSet<Territory>() {
      {
        add(t1);
        add(t3);
      }
    });

    when(gameMap.getNeighborSet(t1)).thenReturn(new HashSet<Territory>() {
      {
        add(t2);
        add(t3);
      }
    });

    when(gameMap.getNeighborSet(t2)).thenReturn(new HashSet<Territory>() {
      {
        add(t1);
        add(t4);
      }
    });

    when(gameMap.getTerritoryByName("0")).thenReturn(t0);
    when(gameMap.getTerritoryByName("1")).thenReturn(t1);
    when(gameMap.getTerritoryByName("2")).thenReturn(t2);
    when(gameMap.getTerritoryByName("3")).thenReturn(t3);
    when(gameMap.getTerritoryByName("4")).thenReturn(t4);
  }

  List<MoveOrder> createValidMoves_forPlayer1() {
    List<MoveOrder> result = new ArrayList<MoveOrder>() {
      {
        add(new MoveOrder(t1, t1, 1)); // player1 move to same territory
        add(new MoveOrder(t1, t2, 1)); // player1 move to other self-owned territory
        add(new MoveOrder(t2, t1, 3)); // player1 move to other self-owned territory
      }
    };
    return result;
  }

  List<MoveOrder> createInvalidMoves_forPlayer1() {
    List<MoveOrder> result = new ArrayList<MoveOrder>() {
      {
        add(new MoveOrder(t3, t2, 1)); // player1 tries to move from other player's territory
        add(new MoveOrder(t1, t3, 1)); // player1 tries to move to other player's territory
        add(new MoveOrder(t1, t4, 1)); // player1 tries to move to other player's other territory
      }
    };
    return result;
  }

  List<MoveOrder> createInvalidMoves_forPlayer2() {
    List<MoveOrder> result = new ArrayList<MoveOrder>() {
      {
        add(new MoveOrder(t4, t3, 4)); // player2 tries to move between 2 territories that are not connected
      }
    };
    return result;
  }

  List<AttackOrder> createValidAttacks_forPlayer1() {
    List<AttackOrder> result = new ArrayList<AttackOrder>() {
      {
        add(new AttackOrder(t1, t3, 1)); // player1 attacks a neighboring enemy
        add(new AttackOrder(t2, t4, 1)); // player1 attacks another neighboring enemy
        add(new AttackOrder(t0, t3, 2)); // player1 attacks another neighboring enemy from an unused src territory
      }
    };
    return result;
  }

  List<AttackOrder> createInvalidAttacks_forPlayer1() {
    List<AttackOrder> result = new ArrayList<AttackOrder>() {
      {
        add(new AttackOrder(t3, t4, 1)); // player1 tries to attack from a territory that belongs to player2
        add(new AttackOrder(t1, t2, 1)); // player1 tries to attack to a territory that belongs to themselves
        add(new AttackOrder(t1, t4, 1)); // player1 tries to attack to a territory that is not a neighbor to themselves
        add(new AttackOrder(t1, t3, -1)); // player1 tries to use more than available to attack a territory
      }
    };
    return result;
  }

  Commit createValidCommit_forPlayer1() {
    Commit commit1 = new Commit(1);
    List<MoveOrder> validMoves1 = createValidMoves_forPlayer1();
    for (MoveOrder move : validMoves1) {
      commit1.addMove(move, gameMap);
    }

    List<AttackOrder> validAttacks1 = createValidAttacks_forPlayer1();
    for (AttackOrder attack : validAttacks1) {
      commit1.addAttack(attack, gameMap);
    }
    return commit1;

  }

  @Test
  public void test_checkValidCases() {
    createValidCommit_forPlayer1();
  }

  @Test
  public void test_checkInvalidCases() {
    Commit commit1 = new Commit(1);
    Commit commit2 = new Commit(2);

    // moves for player1
    List<MoveOrder> invalidMoves1 = createInvalidMoves_forPlayer1();
    for (MoveOrder move : invalidMoves1) {
      assertThrows(IllegalArgumentException.class, () -> commit1.addMove(move, gameMap));
    }

    // moves for player2
    List<MoveOrder> invalidMoves2 = createInvalidMoves_forPlayer2();
    for (MoveOrder move : invalidMoves2) {
      assertThrows(IllegalArgumentException.class, () -> commit2.addMove(move, gameMap));
    }

    // attacks for player1
    List<AttackOrder> invalidAttacks1 = createInvalidAttacks_forPlayer1();
    for (AttackOrder attack : invalidAttacks1) {
      assertThrows(IllegalArgumentException.class, () -> commit1.addAttack(attack, gameMap));
    }
  }

  @Test
  public void test_checkUsableUnitsAfterAllOrdersAreCollected_validCase() {
    Commit commit1 = createValidCommit_forPlayer1();

    commit1.checkUsableUnitsAfterAllOrdersAreCollected();

  }

  @Test
  public void test_checkUsableUnitsAfterAllOrdersAreCollected_invalidCase() {
    Commit commit1 = createValidCommit_forPlayer1();
    commit1.addMove(new MoveOrder(t2, t1, 5), gameMap); // this move order alone is valid, but it's invalid after
                                                        // previous move orders have been added

    assertThrows(IllegalArgumentException.class, () -> commit1.checkUsableUnitsAfterAllOrdersAreCollected());

    Commit commit2 = createValidCommit_forPlayer1();
    commit2.addAttack(new AttackOrder(t2, t4, 3), gameMap);
    assertThrows(IllegalArgumentException.class, () -> commit2.checkUsableUnitsAfterAllOrdersAreCollected());
  }

  @Test
  public void test_checkAll() {
    Commit commit1 = createValidCommit_forPlayer1();
    commit1.checkAll(gameMap);
  }

  @Test
  public void test_performMoves() {
    Commit commit1 = createValidCommit_forPlayer1();
    commit1.performMoves(gameMap);
    assertEquals(3, t1.getNumUnits());
    assertEquals(3, t2.getNumUnits());
  }

  @Test
  public void test_performAttacks() {
    Commit commit1 = createValidCommit_forPlayer1();
    commit1.performMoves(gameMap);
    commit1.performAttacks(gameMap);
    assertEquals(1, t0.getNumUnits());
    assertEquals(2, t1.getNumUnits());
    assertEquals(2, t2.getNumUnits());
  }
}
