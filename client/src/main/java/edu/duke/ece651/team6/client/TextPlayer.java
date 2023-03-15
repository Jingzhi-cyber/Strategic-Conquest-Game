package edu.duke.ece651.team6.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.duke.ece651.team6.shared.AttackOrder;
import edu.duke.ece651.team6.shared.AttackUnitsRuleChecker;
import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.MoveOrder;
import edu.duke.ece651.team6.shared.MoveUnitsRuleChecker;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.SamePlayerPathRuleChecker;
import edu.duke.ece651.team6.shared.Territory;

/* A class representing a player role in the game in text view */
public class TextPlayer implements Player {
  private final Client client;
  BufferedReader inputReader;
  final PrintStream out;
  private final GameBasicSetting setting;
  MapTextView mapTextView;
  GameMap gameMap;

  public TextPlayer(Client client, BufferedReader inputReader, PrintStream out, GameBasicSetting setting)
      throws UnknownHostException, IOException {
    this.client = client;
    this.inputReader = inputReader;
    this.out = out;
    this.setting = setting;
    this.mapTextView = null; // initiate when receiving a global map info
    this.gameMap = null;
  }

  /**
   * Initialized unit placement and send the updated game setting to the server.
   * 
   * @param prompt
   */
  @Override
  public void placeUnit(String prompt) throws IOException, ClassNotFoundException {
    printLine("From client: Requesting to " + prompt);
    HashMap<Territory, Integer> map = new HashMap<Territory, Integer>();
    HashSet<Territory> territories = setting.getAssignedTerritories();
    for (Territory t : territories) {
      Integer i = readNumUnits("Player" + setting.getPlayerId() + ", how many units to you want to place on the "
          + t.getName() + " territory? (" + setting.getRemainingNumUnits() + " remainings)");
      map.put(t, i);
    }

    setting.initializeUnitPlacement(map);
    // send back the GameBasicSetting object with the updated unit placement info.
    client.sendUpdatedGameBasicSetting(setting);

    // recvAndDisplayTextMap();
  }

  protected Territory readTerritory(String prompt) throws IOException {
    return new Territory(readInputLine(prompt));
  }

  private String displayAvailableCommands() {
    return "You are the Player" + setting.getPlayerId() + ", what would you like to do?\n(M)ove\n(A)ttack\n(D)one\n";
  }

  private HashSet<String> getPossibleCommandChars() {
    return new HashSet<String>() {
      {
        add("M");
        add("A");
        add("D");
      }
    };
  }

  private Character readCommand(String prompt) throws IOException {
    String s = readInputLine(prompt).toUpperCase();
    if (s.length() != 1 || !getPossibleCommandChars().contains(s)) {
      throw new IllegalArgumentException(
          "Command must be one of " + getPossibleCommandChars().toString() + ", but was " + s);
    }
    return s.charAt(0);
  }

  private HashMap<Integer, Territory> displayOrderedTerritoriesSelf() {
    int i = 1;
    HashMap<Integer, Territory> map = new HashMap<>();
    for (Territory t : setting.getAssignedTerritories()) {
      printLine(i + ". " + t.getName());
      map.put(i, t);
      i++;
    }
    return map;
  }

  private HashSet<Territory> findEnemyTerritories() {
    HashSet<Territory> set = new HashSet<>();
    HashMap<Integer, PlayerMapInfo> globalInfo = mapTextView.globalMapInfo.getGlobalMap();
    Integer playerId = Integer.valueOf(setting.getPlayerId());
    for (Map.Entry<Integer, PlayerMapInfo> entry : globalInfo.entrySet()) {
      if (!entry.getKey().equals(playerId)) {
        Set<Territory> territories = entry.getValue().getTerritories();
        for (Territory t : territories) {
          set.add(t);
        }
      }
    }
    return set;
  }

  private HashMap<Integer, Territory> displayConnectedOrderedTerritoriesEnemies(Territory src) {
    HashSet<Territory> enemyTerritories = findEnemyTerritories();
    int i = 1;
    HashMap<Integer, Territory> result = new HashMap<>();
    HashSet<Territory> neighs = this.gameMap.getNeighborSet(src);
    for (Territory t : neighs) {
      if (enemyTerritories.contains(t)) {
        printLine(i + ". " + t.getName());
        result.put(i, t);
        i++;
      }
    }
    return result;
  }

  private HashMap<Integer, Territory> displayOrderedTerritories(Territory src) {
    if (src == null) {
      return displayOrderedTerritoriesSelf();
    } else {
      return displayConnectedOrderedTerritoriesEnemies(src);
    }
  }

  private Territory findTerritory(Territory src, String message) throws IOException {
    HashMap<Integer, Territory> map = displayOrderedTerritories(src);
    if (map.size() <= 0) {
      throw new IllegalArgumentException("No suitable territory can be found.");
    }
    Integer integer = readAnInteger(message);
    if (!map.containsKey(integer)) {
      throw new IllegalArgumentException("Invalid territory.");
    }
    return map.get(integer);
  }

  protected MoveOrder constructMove() throws IOException {
    String message = "Player" + setting.getPlayerId() + ", which territory do you want to move units ";
    Territory src = findTerritory(null, message + "from?");
    Territory dest = findTerritory(null, message + "to?");
    int units = readAnInteger("Player" + setting.getPlayerId() + ", how many units do you want to move?");
    return new MoveOrder(src, dest, units);
  }

  protected AttackOrder constructAttack() throws IOException {
    String message = "Player" + setting.getPlayerId() + ", which territory do you want to attack ";
    Territory src = findTerritory(null, message + "from?");
    Territory dest = findTerritory(src, message + "to?");
    int units = readAnInteger("Player" + setting.getPlayerId() + ", how many units do you want to attack?");
    return new AttackOrder(src, dest, units);
  }

  /**
   * Player plays one turn by
   */
  @Override
  public void playOneTurn() throws IOException, ClassNotFoundException {
    /* -------- 1. Display the map --------- */
    updateAndDisplayMapInfo();

    /*
     * -------- 2. Repeatedly display commands (Move, Attack, Done) until Done,
     * allowing 0-N orders in each turn ---------
     */
    HashMap<Territory, Integer> remainingUnits = new HashMap<Territory, Integer>();
    for (Territory t : setting.getAssignedTerritories()) {
      remainingUnits.put(t, t.getNumUnits());
    }
    Commit commit = new Commit(setting.getPlayerId(), new SamePlayerPathRuleChecker(new MoveUnitsRuleChecker(null)),
        new AttackUnitsRuleChecker(null), remainingUnits);
    while (true) {
      Character cmd = null;
      try {
        cmd = readCommand(displayAvailableCommands());
      } catch (IllegalArgumentException e) {
        printLine(e.getMessage());
        continue;
      }
      if (cmd == Character.valueOf('D')) {
        // done
        break;
      } else if (cmd == Character.valueOf('M')) {
        // move
        commit.addMove(constructMove(), this.gameMap);
      } else {
        // attack
        commit.addAttack(constructAttack(), this.gameMap);
      }
    }

    /* -------- 3. Send commit to server --------- */
    this.client.sendCommit(commit);

    // TODO: listen to server to see if orders are valid?
  }

  private void printLine(String str) {
    out.println(str);
  }

  /**
   * A method to request game setting information from the server,
   * information including:
   * 1. The number of players in the game
   * 2. How many territories does the player have, and what are they
   * 3. How many units does the player have (each player has the same amount of
   * units)
   * 
   * @param message from client end, e.g., "game setting"
   */
  public void displayGameSetting(String message) {
    printLine(
        "From server: Welcome to RISC, you are assigned to be Player " + setting.getPlayerId() + ". \n"
            + "There are " + setting.getNumPlayers() + " players in total. These territories are assigned to you: \n"
            + Arrays.toString(setting.getAssignedTerritories().toArray()) + ", and you have "
            + setting.getRemainingNumUnits()
            + " units in total.");
  }

  /**
   * Read a line
   * 
   * @param prompt is the message to display
   * @return a string of the input line
   * @throws IOException
   */
  protected String readInputLine(String prompt) throws IOException {
    printLine(prompt);
    return inputReader.readLine();
  }

  /**
   * @throws NumberFormatException (can be caught by IllegalArgumentException)
   */
  private Integer readAnInteger(String prompt) throws IOException {
    return Integer.valueOf(Integer.parseInt(readInputLine(prompt)));
  }

  private Integer readNumUnits(String prompt) throws IOException {
    Integer numUnits = null;
    while (true) {
      try {
        numUnits = readAnInteger(prompt);
        setting.decreaseUnitsBy(numUnits);
      } catch (IllegalArgumentException e) {
        printLine(prompt);
        continue;
      }
      break;
    }
    return numUnits;
  }

  /**
   * @throws InvalidObjectException
   */
  @Override
  public void updateAndDisplayMapInfo() throws IOException, ClassNotFoundException {
    GlobalMapInfo mapInfo = this.client.recvGlobalMapInfo();
    this.mapTextView = new MapTextView(mapInfo);
    this.gameMap = this.client.recvGameMap();
    printLine(this.mapTextView.display());
  }
}
