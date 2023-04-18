package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Any;

public class CommitTest {
  Territory t0 = new Territory("0", 1, 30);
  Territory t1 = new Territory("1", 1, 10);
  Territory t2 = new Territory("2", 1, 50);
  Territory t3 = new Territory("3", 2, 20);
  Territory t4 = new Territory("4", 2, 40);
  GameMap gameMap = mock(GameMap.class);
  // GameMap gameMap = new GameMap(new HashMap<>());
  Map<String, Integer> resources = new HashMap<String, Integer>() { {put(Constants.RESOURCE_FOOD, 10);} };
  

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

    when(gameMap.getNeighborDist(t0)).thenReturn(new HashMap<Territory, Integer>() {
      {
        put(t1, 1);
        put(t3, 1);
      }
    });

    when(gameMap.getNeighborDist(t1)).thenReturn(new HashMap<Territory, Integer>() {
      {
        put(t2, 1);
        put(t3, 1);
      }
    });

    when(gameMap.getNeighborDist(t2)).thenReturn(new HashMap<Territory, Integer>() {
      {
        put(t1, 1);
        put(t4, 1);
      }
    });

    when(gameMap.getTerritoryByName("0")).thenReturn(t0);
    when(gameMap.getTerritoryByName("1")).thenReturn(t1);
    when(gameMap.getTerritoryByName("2")).thenReturn(t2);
    when(gameMap.getTerritoryByName("3")).thenReturn(t3);
    when(gameMap.getTerritoryByName("4")).thenReturn(t4);

    Map<String, Integer> mockResources = new HashMap<>();
    mockResources.put(Constants.RESOURCE_FOOD, 1000);
    mockResources.put(Constants.RESOURCE_TECH, 1000);
    when(gameMap.getResourceByPlayerId(any(Integer.class))).thenReturn(mockResources);

    when(gameMap.clone()).thenReturn(new GameMap(new HashMap<>()));
  }

  List<MoveOrder> createValidMoves_forPlayer1() {
    List<MoveOrder> result = new ArrayList<MoveOrder>() {
      {
        int[] numUnitsByLevel = {1, 0, 0, 0, 0, 0, 0};
        add(new MoveOrder(t1, t1, numUnitsByLevel)); // player1 move to same territory
        add(new MoveOrder(t1, t2, numUnitsByLevel)); // player1 move to other self-owned territory
        add(new MoveOrder(t2, t1, numUnitsByLevel)); // player1 move to other self-owned territory
      }
    };
    return result;
  }

  List<MoveOrder> createInvalidMoves_forPlayer1() {
    List<MoveOrder> result = new ArrayList<MoveOrder>() {
      {
        int[] numUnitsByLevel = {3, 0, 0, 0, 0, 0, 0};
        add(new MoveOrder(t3, t2, numUnitsByLevel)); // player1 tries to move from other player's territory
        add(new MoveOrder(t1, t3, numUnitsByLevel)); // player1 tries to move to other player's territory
        add(new MoveOrder(t1, t4, numUnitsByLevel)); // player1 tries to move to other player's other territory
      }
    };
    return result;
  }

  List<MoveOrder> createInvalidMoves_forPlayer2() {
    List<MoveOrder> result = new ArrayList<MoveOrder>() {
      {
        int[] numUnitsByLevel = {4, 0, 0, 0, 0, 0, 0};
        add(new MoveOrder(t4, t3, numUnitsByLevel)); // player2 tries to move between 2 territories that are not connected
      }
    };
    return result;
  }

  List<AttackOrder> createValidAttacks_forPlayer1() {
    List<AttackOrder> result = new ArrayList<AttackOrder>() {
      {
        int[] numUnitsByLevel = {1, 0, 0, 0, 0, 0, 0};
        add(new AttackOrder(t1, t3, numUnitsByLevel)); // player1 attacks a neighboring enemy
        add(new AttackOrder(t2, t4, numUnitsByLevel)); // player1 attacks another neighboring enemy
        add(new AttackOrder(t0, t3, numUnitsByLevel)); // player1 attacks another neighboring enemy from an unused src territory
      }
    };
    return result;
  }

  List<AttackOrder> createInvalidAttacks_forPlayer1() {
    List<AttackOrder> result = new ArrayList<AttackOrder>() {
      {
        int[] numUnitsByLevel = {1, 0, 0, 0, 0, 0, 0};
        add(new AttackOrder(t3, t4, numUnitsByLevel)); // player1 tries to attack from a territory that belongs to player2
        add(new AttackOrder(t1, t2, numUnitsByLevel)); // player1 tries to attack to a territory that belongs to themselves
        add(new AttackOrder(t1, t4, numUnitsByLevel)); // player1 tries to attack to a territory that is not a neighbor to themselves
        numUnitsByLevel[0] = -1;
        add(new AttackOrder(t1, t3, numUnitsByLevel)); // player1 tries to use more than available to attack a territory
      }
    };
    return result;
  }

  Commit createValidCommit_forPlayer1() {
    Commit commit1 = new Commit(1, gameMap, resources);
    List<MoveOrder> validMoves1 = createValidMoves_forPlayer1();
    for (MoveOrder move : validMoves1) {
      commit1.addMove(move);
    }

    List<AttackOrder> validAttacks1 = createValidAttacks_forPlayer1();
    for (AttackOrder attack : validAttacks1) {
      commit1.addAttack(attack);
    }
    return commit1;

  }

  @Test
  public void test_checkValidCases() {
    createValidCommit_forPlayer1();
  }

  @Test
  public void test_checkInvalidCases() {
    Commit commit1 = new Commit(1, gameMap, resources);
    Commit commit2 = new Commit(2, gameMap, resources);

    // moves for player1
    List<MoveOrder> invalidMoves1 = createInvalidMoves_forPlayer1();
    for (MoveOrder move : invalidMoves1) {
      assertThrows(Exception.class, () -> commit1.addMove(move));
    }

    // moves for player2
    List<MoveOrder> invalidMoves2 = createInvalidMoves_forPlayer2();
    for (MoveOrder move : invalidMoves2) {
      assertThrows(IllegalArgumentException.class, () -> commit2.addMove(move));
    }

    // attacks for player1
    List<AttackOrder> invalidAttacks1 = createInvalidAttacks_forPlayer1();
    for (AttackOrder attack : invalidAttacks1) {
      assertThrows(Exception.class, () -> commit1.addAttack(attack));
    }
  }

  // @Test
  // public void test_checkUsableUnitsAfterAllOrdersAreCollected_validCase() {
  //   Commit commit1 = createValidCommit_forPlayer1();

  //   commit1.checkUsableUnitsAfterAllOrdersAreCollected();

  // }

  // @Test
  // public void test_checkUsableUnitsAfterAllOrdersAreCollected_invalidCase() {
  //   Commit commit1 = createValidCommit_forPlayer1();
  //   commit1.addMove(new MoveOrder(t2, t1, 5), gameMap); // this move order alone is valid, but it's invalid after
  //                                                       // previous move orders have been added

  //   assertThrows(IllegalArgumentException.class, () -> commit1.checkUsableUnitsAfterAllOrdersAreCollected());

  //   Commit commit2 = createValidCommit_forPlayer1();
  //   commit2.addAttack(new AttackOrder(t2, t4, 3), gameMap);
  //   assertThrows(IllegalArgumentException.class, () -> commit2.checkUsableUnitsAfterAllOrdersAreCollected());
  // }

  @Test
  public void test_checkAll() {
    Commit commit1 = createValidCommit_forPlayer1();
    commit1.checkAll(gameMap);
  }

  @Test
  public void test_performMoves() {
    Commit commit1 = createValidCommit_forPlayer1();
    commit1.performMoves(gameMap);
    // assertEquals(3, t1.getNumUnits());
    // assertEquals(3, t2.getNumUnits());
  }

  @Test
  public void test_performAttacks() {
    Commit commit1 = createValidCommit_forPlayer1();
    commit1.performMoves(gameMap);
    commit1.performAttacks(gameMap);
    // assertEquals(1, t0.getNumUnits());
    // assertEquals(2, t1.getNumUnits());
    // assertEquals(2, t2.getNumUnits());
  }

  @Test
  public void test_researchAndUpgrade() {
    SampleMap sampleMap = new SampleMap();
    GameMap gm = new GameMap(sampleMap.getAdjList());
    Map<Integer, Map<String, Integer>> resources = new HashMap<>();
    Map<String, Integer> resource = new HashMap<>();
    resource.put(Constants.RESOURCE_FOOD, 100);
    resource.put(Constants.RESOURCE_TECH, 100);
    resources.put(0, resource);
    gm.updateResource(resources);
    gm.initMaxTechLevel();
    Territory Narnia = gm.getTerritoryByName("Narnia");
    Narnia.initNumUnits(10);

    Commit commit = new Commit(0, gm, resource);
    commit.addResearch(new ResearchOrder(0));
    assertThrows(IllegalArgumentException.class, () -> commit.addResearch(new ResearchOrder(0)));
    commit.addUpgrade(new UpgradeOrder(Narnia, 0, 1, 1));
    commit.performResearch(gm);
    commit.performUpgrade(gm);
  }
}
