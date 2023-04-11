package edu.duke.ece651.team6.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.Constants;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Result;
import edu.duke.ece651.team6.shared.SampleMap;
import edu.duke.ece651.team6.shared.Territory;

public class TextGameTest {
  private final SocketHandler client = mock(SocketHandler.class);
  private GameBasicSetting setting = null;
  // private GameMap gameMap = null;
  // private MapTextView mapTextView = null;

  private Set<Territory> createTerritories_withoutUnitsPlacement() {
    Set<Territory> set = new HashSet<Territory>() {
      {
        add(new Territory("A", 1));
        add(new Territory("B", 1));
      }
    };
    return set;
  }

  private Set<Territory> createAssignedTerritories_withUnitsPlaced() {
    Set<Territory> set = new HashSet<Territory>() {
      {
        add(new Territory("A", 1, 3));
        add(new Territory("B", 1, 7));
      }
    };
    return set;
  }

  @BeforeEach
  public void setUp() throws IOException, ClassNotFoundException {
    setting = new GameBasicSetting(1, 2, createTerritories_withoutUnitsPlacement(), 10);
    when(this.client.recvGlobalMapInfo())
        .thenReturn(createGlobalMapInfo_withUnitsPlaced(createGameMap_withUnitPlacement()));
    when(this.client.recvGameResult()).thenReturn(new Result());
  }

  private TextGame createTextGame(String inputData, OutputStream bytes, Set<Territory> territories) throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader(inputData));
    PrintStream printStream = new PrintStream(bytes, true); // ps is a PrintStream (looks like System.out) which
                                                            // writes its data into bytes instead of to the screen.
    setting = new GameBasicSetting(1, 2, territories, 10);
    // gameMap = createGameMap();
    TextGame textPlayer = new TextGame(client, reader, printStream, setting);
    return textPlayer;
  }

  private GlobalMapInfo createGlobalMapInfo_withUnitsPlaced(GameMap gameMap) {
    GlobalMapInfo expectedMap = new GlobalMapInfo(gameMap);
    Territory t1 = new Territory("A", 1, 3);
    Territory t2 = new Territory("B", 1, 7);
    Territory t3 = new Territory("C", 2, 6);
    Territory t4 = new Territory("D", 2, 4);
    Map<Territory, Map<Territory, Integer>> info1 = new HashMap<>();
    info1.put(t1, new HashMap<>());
    info1.get(t1).put(t2, 1);
    info1.get(t1).put(t3, 1);
    info1.put(t2, new HashMap<>());
    info1.get(t2).put(t1, 1);
    info1.get(t2).put(t4, 1);
    PlayerMapInfo playerMapInfo1 = new PlayerMapInfo(1, info1);
    expectedMap.addPlayerMapInfo(playerMapInfo1);

    Map<Territory, Map<Territory, Integer>> info2 = new HashMap<>();
    info2.put(t3, new HashMap<>());
    info2.get(t3).put(t1, 1);
    info2.get(t3).put(t4, 1);
    info2.put(t4, new HashMap<>());
    info2.get(t4).put(t2, 1);
    info2.get(t4).put(t3, 1);
    PlayerMapInfo playerMapInfo2 = new PlayerMapInfo(1, info2);
    expectedMap.addPlayerMapInfo(playerMapInfo2);
    return expectedMap;
  }

  private GameMap createGameMap_withMissingSelfNeighbors() {
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    Territory terA = new Territory("A", 1);
    Territory terB = new Territory("B", 1);
    Territory terC = new Territory("C", 2);
    Territory terD = new Territory("D", 2);

    // missing neighbors
    adjList.put(terA, new HashSet<Territory>());

    adjList.put(terB, new HashSet<Territory>() {
      {
        add(terA);
        add(terD);
      }
    });

    adjList.put(terC, new HashSet<Territory>() {
      {
        add(terA);
        add(terD);
      }
    });

    adjList.put(terD, new HashSet<Territory>() {
      {
        add(terB);
        add(terC);
      }
    });
    return new GameMap(adjList);
  }

  private GameMap createGameMap_withoutUnitPlacement() {
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    Territory terA = new Territory("A", 1);
    Territory terB = new Territory("B", 2);
    Territory terC = new Territory("C", 2);
    Territory terD = new Territory("D", 2);

    adjList.put(terA, new HashSet<Territory>() {
      {
        add(terB);
        add(terC);
      }
    });

    adjList.put(terB, new HashSet<Territory>() {
      {
        add(terA);
        add(terD);
      }
    });

    adjList.put(terC, new HashSet<Territory>() {
      {
        add(terA);
        add(terD);
      }
    });

    adjList.put(terD, new HashSet<Territory>() {
      {
        add(terB);
        add(terC);
      }
    });
    return new GameMap(adjList);
  }

  private GameMap createGameMap_withUnitPlacement() {
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    Territory terA = new Territory("A", 1, 3);
    Territory terB = new Territory("B", 2, 7);
    Territory terC = new Territory("C", 2, 6);
    Territory terD = new Territory("D", 2, 4);

    adjList.put(terA, new HashSet<Territory>() {
      {
        add(terB);
        add(terC);
      }
    });

    adjList.put(terB, new HashSet<Territory>() {
      {
        add(terA);
        add(terD);
      }
    });

    adjList.put(terC, new HashSet<Territory>() {
      {
        add(terA);
        add(terD);
      }
    });

    adjList.put(terD, new HashSet<Territory>() {
      {
        add(terB);
        add(terC);
      }
    });
    return new GameMap(adjList);
  }

  @Test
  public void test_displayGameSetting() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player = createTextGame("Some input", bytes, createTerritories_withoutUnitsPlacement());
    // GameBasicSetting mockSetting = mock(GameBasicSetting.class);
    // when(mockSetting.getPlayerId()).thenReturn(1);
    // when(mockSetting.getNumPlayers()).thenReturn(3);

    // when(mockSetting.getAssignedTerritories().toString()).thenReturn("[A]");
    // when(mockSetting.getRemainingNumUnits()).thenReturn(10);

    player.displayGameSetting();

    String expected = "From server: Welcome to RISC, you are assigned to be Player 1. \nThere are 2 players in total. These territories are assigned to you: \n[(name: A, ownerId: 1), (name: B, ownerId: 1)], and you have 10 units in total.\n";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void test_updateAndDisplayTextMap() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player = createTextGame("Some input", bytes, createTerritories_withoutUnitsPlacement());

    // when(new MapTextView(expectedMap)).thenReturn(mtv);
    // String expected = "Player1:\n-------------\n0 units in A (next to: B C)\n\n0
    // units in B (next to: A D)\n\n\n"; // TODO:
    // set
    // can
    // be
    // of
    // random
    // order
    player.refreshMap();
    assertTrue(bytes.toString().length() > 0);
  }

  @Test
  public void test_placeUnit() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player1 = createTextGame("6\n", bytes, createTerritories_withoutUnitsPlacement());

    player1.placeUnit();

    Collection<Integer> collection1 = new HashSet<Integer>() {
      {
        add(Integer.valueOf(6));
        add(Integer.valueOf(4));
      }
    };
    assertEquals(new HashSet<Integer>(collection1), new HashSet<Integer>(setting.getUnitPlacement().values()));

    bytes.reset();

    TextGame player2 = createTextGame("2\n", bytes, createTerritories_withoutUnitsPlacement());

    player2.placeUnit();

    Collection<Integer> collection2 = new HashSet<Integer>() {
      {
        add(Integer.valueOf(2));
        add(Integer.valueOf(8));
      }
    };
    assertEquals(new HashSet<Integer>(collection2), new HashSet<Integer>(setting.getUnitPlacement().values()));

  }

  @Test
  public void test_placeUnit_invalid_case() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player = createTextGame("14\n7\n", bytes, createTerritories_withoutUnitsPlacement());

    player.placeUnit();

    Collection<Integer> collection = new HashSet<Integer>() {
      {
        add(Integer.valueOf(3));
        add(Integer.valueOf(7));
      }
    };
    assertEquals(new HashSet<Integer>(collection), new HashSet<Integer>(setting.getUnitPlacement().values()));
  }

  @Test
  public void test_playOneTurn_withMove() throws IOException, ClassNotFoundException {
    GlobalMapInfo mapInfo = createGlobalMapInfo_withUnitsPlaced(createGameMap_withUnitPlacement());
    when(client.recvGlobalMapInfo()).thenReturn(mapInfo);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player = createTextGame("M\n1\n2\n0\n1\nY\nD\n", bytes, createAssignedTerritories_withUnitsPlaced());
    player.playOneTurn();
  }

  @Test
  public void test_playOneTurn_withAttack() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player = createTextGame("A\n1\n1\n0\n1\nY\nD\n", bytes, createAssignedTerritories_withUnitsPlaced());
    // player.placeUnit("place units");
    player.playOneTurn();
  }

  @Test
  public void test_playOneTurn_withDone() throws IOException, ClassNotFoundException {
    GlobalMapInfo mapInfo2 = createGlobalMapInfo_withUnitsPlaced(createGameMap_withoutUnitPlacement());
    when(client.recvGlobalMapInfo()).thenReturn(mapInfo2);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player = createTextGame("D\n", bytes, createAssignedTerritories_withUnitsPlaced());
    // player.placeUnit("place units");
    player.playOneTurn();
  }

  @Test
  public void test_playerOneTurn_ResearchAndUpgrade() throws IOException, ClassNotFoundException {
    GlobalMapInfo mapInfo2 = createGlobalMapInfo_withUnitsPlaced(createGameMap_withUnitPlacement());
    SampleMap sampleMap = new SampleMap();
    GameMap gm = new GameMap(sampleMap.getAdjList());
    Map<Integer, Map<String, Integer>> resources = new HashMap<>();
    Map<String, Integer> resource = new HashMap<>();
    resource.put(Constants.RESOURCE_FOOD, 100);
    resource.put(Constants.RESOURCE_TECH, 100);
    resources.put(1, resource);
    gm.updateResource(resources);
    gm.initMaxTechLevel();
    GlobalMapInfo mapInfo = new GlobalMapInfo(gm);
    when(client.recvGlobalMapInfo()).thenReturn(mapInfo);

    Map<Territory, Map<Territory, Integer>> info = new HashMap<>();
    Set<Territory> territories = gm.getTerritorySet();
    for (Territory t : territories) {
      if (t.getOwnerId() == 1) {
        info.put(t, gm.getNeighborDist(t));
      }
    }
    PlayerMapInfo playerMapInfo = new PlayerMapInfo(1, info);
    mapInfo.addPlayerMapInfo(playerMapInfo);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextGame player = createTextGame("R\nU\n1\n-1\n0\n-1\n0\n1\n1\nD\n", bytes,
        createAssignedTerritories_withUnitsPlaced());
    // TextPlayer player = createTextPlayer("R\nD\n", bytes,
    // createAssignedTerritories_withUnitsPlaced());
    // player.placeUnit("place units");
    player.playOneTurn();
  }

  // @Test
  // public void test_playOneTurn_invalidCase() throws IOException,
  // ClassNotFoundException {
  // /* Assumptions for case1 and case2 */
  // // GlobalMapInfo mapInfo =
  // createGlobalMapInfo_withUnitsPlaced(createGameMap_withMissingSelfNeighbors());
  // GlobalMapInfo mapInfo =
  // createGlobalMapInfo_withUnitsPlaced(createGameMap_withUnitPlacement());
  // when(client.recvGlobalMapInfo()).thenReturn(mapInfo);

  // ByteArrayOutputStream bytes = new ByteArrayOutputStream();

  // /*
  // * Case 1. catched exception: Invalid move action: there isn't a connected
  // path
  // * through src to dest where all territories belong to the same player.
  // */
  // TextPlayer player = createTextPlayer("M\n1\n2\n0\n1\nY\nD\n", bytes,
  // createAssignedTerritories_withUnitsPlaced());
  // player.playOneTurn();

  // /*
  // * Case 2. catched exception: No suitable territory can be found. Please
  // change
  // * and action to perform.
  // */
  // bytes.reset();
  // TextPlayer player2 = createTextPlayer("3\n14\n7\nA\n1\n2\n0\n1\nY\nD\n",
  // bytes,
  // createAssignedTerritories_withUnitsPlaced());
  // player2.playOneTurn();

  // /* Assumptions for case3 and case4 */
  // GlobalMapInfo mapInfo2 =
  // createGlobalMapInfo_withUnitsPlaced(createGameMap_withUnitPlacement());
  // when(client.recvGlobalMapInfo()).thenReturn(mapInfo2);

  // /* Case 3. catched exception: Invalid territory. */
  // bytes.reset();
  // TextPlayer player3 = createTextPlayer("M\n3\n2\n0\n1\nY\nM\n2\n2\n0\nY\nD\n",
  // bytes,
  // createAssignedTerritories_withUnitsPlaced());
  // player3.playOneTurn();

  // /* Case 4. catched exception: invalid commit */
  // bytes.reset();
  // TextPlayer player4 =
  // createTextPlayer("A\n1\n1\n0\n1\nY\nM\n1\n2\n0\nY\nM\n1\n2\n0\n3\nY\nD\nA\n1\n1\n0\n1\nY\nM\n1\n2\n0\n1\nY\nD\n",
  // bytes,
  // createAssignedTerritories_withUnitsPlaced());
  // player4.playOneTurn();
  // }

  // @Test
  // public void test_checkGameResult() throws IOException, ClassNotFoundException
  // {
  // // Assumptions1
  // when(this.client.recvGameResult()).thenReturn(new Result() {
  // {
  // addLoser(1);
  // }
  // });

  // // Case 1: Exit
  // ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  // TextPlayer player = createTextPlayer("M\n1\n2\n0\n1\nY\nD\nE\n", bytes,
  // createAssignedTerritories_withUnitsPlaced());

  // assertEquals("Exit", player.playOneTurn());

  // // Case 2: Keep watching
  // bytes.reset();
  // // Cover the case when input cmd is invalid (the "Q")
  // TextPlayer player2 = createTextPlayer("M\n1\n2\n1\nD\nQ\nW\n", bytes,
  // createAssignedTerritories_withUnitsPlaced());

  // assertEquals(null, player2.playOneTurn());

  // // Case 3: Some player wins
  // when(this.client.recvGameResult()).thenReturn(new Result() {
  // {
  // addWinner(2); // some other player wins other than player1
  // }
  // });
  // TextPlayer player3 = createTextPlayer("M\n1\n2\n1\nD\n", bytes,
  // createAssignedTerritories_withUnitsPlaced());

  // assertEquals("Game Over", player3.playOneTurn());

  // }

  // @Test
  // public void test_playGame() throws IOException, ClassNotFoundException {
  // // Lose the game and exit
  // when(this.client.recvGameResult()).thenReturn(new Result() {
  // {
  // addLoser(1);
  // }
  // });
  // ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  // TextPlayer player = createTextPlayer("M\n1\n2\n1\nD\nE\n", bytes,
  // createAssignedTerritories_withUnitsPlaced());
  // player.playGame();

  // // Lose the game but keep watching
  // bytes.reset();
  // TextPlayer player2 = createTextPlayer("M\n1\n2\n1\nD\nW\nD\nE\n", bytes,
  // createAssignedTerritories_withUnitsPlaced());
  // player2.playGame();

  // // Winner has created
  // when(this.client.recvGameResult()).thenReturn(new Result() {
  // {
  // addWinner(2); // some other player wins other than player1
  // }
  // });
  // bytes.reset();
  // TextPlayer player3 = createTextPlayer("D\n", bytes,
  // createAssignedTerritories_withUnitsPlaced());
  // player3.playGame();
  // }
}
