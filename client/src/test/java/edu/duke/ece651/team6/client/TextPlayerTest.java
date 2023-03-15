package edu.duke.ece651.team6.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;

public class TextPlayerTest {
  private final Client client = mock(Client.class);
  private GameBasicSetting setting = null;
  // private MapTextView mapTextView = null;

  @BeforeEach
  public void setUp() {
    HashSet<Territory> set = new HashSet<Territory>() {
      {
        add(new Territory("A", 1));
        add(new Territory("B", 1));
      }
    };
    setting = new GameBasicSetting(1, 3, set, 10);
    // when(client.)
  }

  private TextPlayer createTextPlayer(String inputData, OutputStream bytes) throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader(inputData));
    PrintStream printStream = new PrintStream(bytes, true); // ps is a PrintStream (looks like System.out) which
                                                            // writes its data into bytes instead of to the screen.
    TextPlayer textPlayer = new TextPlayer(client, reader, printStream, setting);
    return textPlayer;
  }

  private GlobalMapInfo createGlobalMapInfo() {
    GlobalMapInfo expectedMap = new GlobalMapInfo();
    PlayerMapInfo playerMapInfo1 = new PlayerMapInfo(1, new HashMap<Territory, HashSet<String>>() {
      {
        put(new Territory("A"), new HashSet<String>() {
          {
            add("neigh1");
            add("neigh2");
          }
        });
      }
    });
    expectedMap.addPlayerMapInfo(playerMapInfo1);
    return expectedMap;
  }

  @Test
  public void test_displayGameSetting() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("Some input", bytes);
    // GameBasicSetting mockSetting = mock(GameBasicSetting.class);
    // when(mockSetting.getPlayerId()).thenReturn(1);
    // when(mockSetting.getNumPlayers()).thenReturn(3);

    // when(mockSetting.getAssignedTerritories().toString()).thenReturn("[A]");
    // when(mockSetting.getRemainingNumUnits()).thenReturn(10);

    player.displayGameSetting("Some message");

    String expected = "From server: Welcome to RISC, you are assigned to be Player 1. \nThere are 3 players in total. These territories are assigned to you: \n[(name: A, ownerId: 1, units: 0), (name: B, ownerId: 1, units: 0)], and you have 10 units in total.\n";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void test_updateAndDisplayTextMap() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("Some input", bytes);
    GlobalMapInfo expectedMap = createGlobalMapInfo();
    // MapTextView mtv = spy(new MapTextView(expectedMap));
    // set up the expected map data
    when(client.recvGlobalMapInfo()).thenReturn(expectedMap);
    // when(new MapTextView(expectedMap)).thenReturn(mtv);
    String expected = "Player1:\n-------------\n0 units in A (next to: neigh2 neigh1)\n\n\n";
    player.updateAndDisplayMapInfo();
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void test_placeUnit() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer("6\n4\n", bytes);

    // String expected1 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the A territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the B territory? (4 remainings)\n";
    // String expected2 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the B territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the A territory? (4 remainings)\n";

    player.placeUnit("place units");

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
    TextPlayer player = createTextPlayer("3\n14\n7\n", bytes);

    // String expected1 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the A territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the B territory? (4 remainings)\n";
    // String expected2 = "From client: Requesting to place units\nPlayer1, how many
    // units to you want to place on the B territory? (10 remainings)\nPlayer1, how
    // many units to you want to place on the A territory? (4 remainings)\n";

    player.placeUnit("place units");

    Collection<Integer> collection = new HashSet<Integer>() {
      {
        add(Integer.valueOf(3));
        add(Integer.valueOf(7));
      }
    };
    assertEquals(new HashSet<Integer>(collection), new HashSet<Integer>(setting.getUnitPlacement().values()));
  }
}
