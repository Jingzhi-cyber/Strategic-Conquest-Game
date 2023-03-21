package edu.duke.ece651.team6.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Result;
import edu.duke.ece651.team6.shared.Territory;

public class TextPlayerTest {
  private final Client client = mock(Client.class);
  private GameBasicSetting setting = null;
  // private GameMap gameMap = null;
  // private MapTextView mapTextView = null;

  private HashSet<Territory> createTerritories_withoutUnitsPlacement() {
    HashSet<Territory> set = new HashSet<Territory>() {
      {
        add(new Territory("A", 1));
        add(new Territory("B", 1));
      }
    };
    return set;
  }

  private HashSet<Territory> createAssignedTerritories_withUnitsPlaced() {
    HashSet<Territory> set = new HashSet<Territory>() {
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

  private TextPlayer createTextPlayer(String inputData, OutputStream bytes, HashSet<Territory> territories)
      throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader(inputData));
    PrintStream printStream = new PrintStream(bytes, true); // ps is a PrintStream (looks like System.out) which
                                                            // writes its data into bytes instead of to the screen.
    setting = new GameBasicSetting(1, 2, territories, 10);
    // gameMap = createGameMap();
    TextPlayer textPlayer = new TextPlayer(client, reader, printStream, setting);
    return textPlayer;
  }

  private GlobalMapInfo createGlobalMapInfo_withUnitsPlaced(GameMap gameMap) {
    GlobalMapInfo expectedMap = new GlobalMapInfo(gameMap);
    PlayerMapInfo playerMapInfo1 = new PlayerMapInfo(1, new HashMap<Territory, HashSet<String>>() {
      {
        put(new Territory("A", 1, 3), new HashSet<String>() {
          {
            add("B");
            add("C");
          }
        });
        put(new Territory("B", 1, 7), new HashSet<String>() {
          {
            add("A");
            add("D");
          }
        });
      }
    });
    expectedMap.addPlayerMapInfo(playerMapInfo1);

    PlayerMapInfo playerMapInfo2 = new PlayerMapInfo(2, new HashMap<Territory, HashSet<String>>() {
      {
        put(new Territory("C", 2, 6), new HashSet<String>() {
          {
            add("A");
            add("D");
          }
        });
        put(new Territory("D", 2, 4), new HashSet<String>() {
          {
            add("B");
            add("C");
          }
        });
      }
    });

    expectedMap.addPlayerMapInfo(playerMapInfo2);
    return expectedMap;
  }

  private GameMap createGameMap_withMissingSelfNeighbors() {
    HashMap<Territory, HashSet<Territory>> adjList = new HashMap<>();
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
    HashMap<Territory, HashSet<Territory>> adjList = new HashMap<>();
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
    HashMap<Territory, HashSet<Territory>> adjList = new HashMap<>();
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
    TextPlayer player = createTextPlayer("Some input", bytes, createTerritories_withoutUnitsPlacement());
    // GameBasicSetting mockSetting = mock(GameBasicSetting.class);
    // when(mockSetting.getPlayerId()).thenReturn(1);
    // when(mockSetting.getNumPlayers()).thenReturn(3);

    // when(mockSetting.getAssignedTerritories().toString()).thenReturn("[A]");
    // when(mockSetting.getRemainingNumUnits()).thenReturn(10);

    player.displayGameSetting("Some message");

    String expected = "From server: Welcome to RISC, you are assigned to be Player 1. \nThere are 2 players in total. These territories are assigned to you: \n[(name: A, ownerId: 1, units: 0), (name: B, ownerId: 1, units: 0)], and you have 10 units in total.\n";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void test_updateAndDisplayTextMap() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("Some input", bytes, createTerritories_withoutUnitsPlacement());

    // when(new MapTextView(expectedMap)).thenReturn(mtv);
    // String expected = "Player1:\n-------------\n0 units in A (next to: B C)\n\n0
    // units in B (next to: A D)\n\n\n"; // TODO:
    // set
    // can
    // be
    // of
    // random
    // order
    player.updateAndDisplayMapInfo();
    assertTrue(bytes.toString().length() > 0);
  }

  @Test
  public void test_placeUnit() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("6\n4\n", bytes, createTerritories_withoutUnitsPlacement());

    // String expected1 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the A territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the B territory? (4 remainings)\n";
    // String expected2 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the B territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the A territory? (4 remainings)\n";

    player.placeUnit();

    Collection<Integer> collection = new HashSet<Integer>() {
      {
        add(Integer.valueOf(6));
        add(Integer.valueOf(4));
      }
    };
    assertEquals(new HashSet<Integer>(collection), new HashSet<Integer>(setting.getUnitPlacement().values()));
  }

  @Test
  public void test_placeUnit_invalid_case() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("3\n14\n7\n", bytes, createTerritories_withoutUnitsPlacement());

    // String expected1 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the A territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the B territory? (4 remainings)\n";
    // String expected2 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the B territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the A territory? (4 remainings)\n";

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
    TextPlayer player = createTextPlayer("M\n1\n2\n1\nD\n", bytes,
        createAssignedTerritories_withUnitsPlaced());
    // player.placeUnit("place units");
    player.playOneTurn();
  }

  @Test
  public void test_playOneTurn_withAttack() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("A\n1\n1\n1\nD\n", bytes,
        createAssignedTerritories_withUnitsPlaced());
    // player.placeUnit("place units");
    player.playOneTurn();
  }

  @Test
  public void test_playOneTurn_withDone() throws IOException, ClassNotFoundException {
    GlobalMapInfo mapInfo2 = createGlobalMapInfo_withUnitsPlaced(createGameMap_withoutUnitPlacement());
    when(client.recvGlobalMapInfo()).thenReturn(mapInfo2);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("D\n", bytes, createAssignedTerritories_withUnitsPlaced());
    // player.placeUnit("place units");
    player.playOneTurn();
  }

  @Test
  public void test_playOneTurn_invalidCase() throws IOException, ClassNotFoundException {
    // Assumptions
    GlobalMapInfo mapInfo = createGlobalMapInfo_withUnitsPlaced(createGameMap_withMissingSelfNeighbors());
    when(client.recvGlobalMapInfo()).thenReturn(mapInfo);

    // catched exception: Invalid move action: there isn't a connected path through
    // src to dest where
    // all territories belong to the same player.
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("M\n1\n2\n1\nD\n", bytes,
        createAssignedTerritories_withUnitsPlaced());
    player.playOneTurn();
    // assertThrows(IllegalArgumentException.class, () -> player.playOneTurn());

    // catched exception: No suitable territory can be found. Please change an
    // action to perform.
    bytes.reset();
    // when(client.recvGameMap()).thenReturn(createGameMap_withMissingSelfNeighbors());
    TextPlayer player2 = createTextPlayer("3\n14\n7\nA\n1\n2\n1\nD\n", bytes,
        createAssignedTerritories_withUnitsPlaced());
    player2.playOneTurn();
    // assertThrows(IllegalArgumentException.class, () -> player2.playOneTurn());

    // catched exception: Invalid territory.
    bytes.reset();
    // GlobalMapInfo mapInfo2 =
    // createGlobalMapInfo_withoutAssignedTerritories(createGameMap_withoutUnitPlacement());
    GlobalMapInfo mapInfo2 = createGlobalMapInfo_withUnitsPlaced(createGameMap_withUnitPlacement());
    when(client.recvGlobalMapInfo()).thenReturn(mapInfo2);
    // when(client.recvGameMap()).thenReturn(createGameMap());

    TextPlayer player3 = createTextPlayer("M\n3\n2\n1\nM\n2\n2\n1\nD\n", bytes,
        createAssignedTerritories_withUnitsPlaced());
    player3.playOneTurn();
    // assertThrows(IllegalArgumentException.class, () -> player3.playOneTurn());

    // catched exception: invalid commit
    bytes.reset();
    TextPlayer player4 = createTextPlayer("A\n1\n1\n1\nM\n1\n2\n2\nM\n1\n2\n3\nD\nA\n1\n1\n1\nM\n1\n2\n1\nD\n", bytes,
        createAssignedTerritories_withUnitsPlaced());
    // player.placeUnit("place units");
    player4.playOneTurn();
  }
}
