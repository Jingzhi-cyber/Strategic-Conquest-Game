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

/**
 * A class representing a text player in the risc game
 */
public class TextPlayer implements Player {
  private final Client client;
  BufferedReader inputReader;
  final PrintStream out;
  private final GameBasicSetting setting;
  MapTextView mapTextView;
  GameMap gameMap;

  /**
   * Constructs TextPlayer with 4 params
   * 
   * @param client      deals with connection with server
   * @param inputReader
   * @param out
   * @param setting     is game basic setting in the initialization phase
   */
  public TextPlayer(Client client, BufferedReader inputReader, PrintStream out, GameBasicSetting setting)
      throws UnknownHostException, IOException {
    this.client = client;
    this.inputReader = inputReader;
    this.out = out;
    this.setting = setting;
    this.mapTextView = null; // initiate when receiving a global map info
    this.gameMap = null; // TODO: may delete later if it's included in global map info
  }

  /**
   * Place units and send the updated game setting to the server.
   * 
   * @param prompt
   * @throws IOException, ClassNotFoundException
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

  /**
   * Read a territory from user input
   * 
   * @param prompt is for displaying
   * @return Territory is the constructed Territory
   */
  protected Territory readTerritory(String prompt) throws IOException {
    return new Territory(readInputLine(prompt));
  }

  /**
   * @return a string to display available commands
   */
  private String displayAvailableCommands() {
    return "You are the Player" + setting.getPlayerId() + ", what would you like to do?\n(M)ove\n(A)ttack\n(D)one\n";
  }

  /**
   * Get a set of notations of possible commands
   * 
   * @return HashSet<String> with the first letters of available commands
   */
  private HashSet<String> getPossibleCommandChars() {
    return new HashSet<String>() {
      {
        add("M");
        add("A");
        add("D");
      }
    };
  }

  /**
   * Read a command
   * 
   * @param prompt
   * @return a Character of the command
   * @throws IOException
   */
  private Character readCommand(String prompt) throws IOException {
    String s = readInputLine(prompt).toUpperCase();
    if (s.length() != 1 || !getPossibleCommandChars().contains(s)) {
      throw new IllegalArgumentException(
          "Command must be one of " + getPossibleCommandChars().toString() + ", but was " + s);
    }
    return s.charAt(0);
  }

  /**
   * Construct, display and return self-owned territories with serial numbers
   * 
   * @return a HashMap<Integer, Territory> mapping from serial number to the
   *         territory
   */
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

  /**
   * Construct a list of all enemy territories
   * 
   * @return HashSet<Territory> enemyTerritories
   */
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

  /**
   * Construct and display a list of possible enemy territories with serial
   * numbers to attack by checking if the enemy territory is directly connected
   * with the source territory
   * 
   * @param src is the source self-owned territory of the player
   * @return HashMap<Integer, Territory> a map from the serial number to a
   *         territory
   */
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

  /**
   * A method to display self or enemy territories with serial numbers
   * 
   * @param src is the source territory owned by the current player. If it's null,
   *            it will display enemy territories
   * @return HashMap<Integer, Territory> is a mapping from serial number to a
   *         territory
   */
  private HashMap<Integer, Territory> displayOrderedTerritories(Territory src) {
    if (src == null) {
      return displayOrderedTerritoriesSelf();
    } else {
      return displayConnectedOrderedTerritoriesEnemies(src);
    }
  }

  /**
   * A method to find a territory based on the user input
   * 
   * @param src     is the source territory to construct a set of self or enemy
   *                territories
   * @param message is the prompt to the player to input a choice (an integer that
   *                can be mapped to a teritory)
   * @return the resulting Territory
   * @throws IllegalArgumentException if the input choice cannot be mapped to a
   *                                  valid territory
   * @propogates {@link IllegalArgumentException} from readAnInteger
   */
  private Territory findTerritory(Territory src, String message) throws IOException {
    HashMap<Integer, Territory> map = displayOrderedTerritories(src);
    if (map.size() <= 0) {
      throw new IllegalArgumentException("No suitable territory can be found. Please change an action to perform.");
    }
    Integer integer = readAnInteger(message);
    if (!map.containsKey(integer)) {
      throw new IllegalArgumentException("Invalid territory.");
    }
    return map.get(integer);
  }

  /**
   * Constructs a Move order
   * 
   * @return {@link MoveOrder}
   * @throws IOException
   * @propogates {@link IllegalArgumentException} from findTerritory and
   *             readAnInteger
   */
  protected MoveOrder constructMove() throws IOException {
    String message = "Player" + setting.getPlayerId() + ", which territory do you want to move units ";
    Territory src = findTerritory(null, message + "from?");
    Territory dest = findTerritory(null, message + "to?");
    int units = readAnInteger("Player" + setting.getPlayerId() + ", how many units do you want to move?");
    return new MoveOrder(src, dest, units);
  }

  /**
   * Constructs an attack order
   * 
   * @return an {@link AttackOrder}
   * @throws IOException
   * @throws {@link      IllegalArgumentException} from readAnInteger
   */
  protected AttackOrder constructAttack() throws IOException {
    String message = "Player" + setting.getPlayerId() + ", which territory do you want to attack ";
    Territory src = findTerritory(null, message + "from?");
    Territory dest = findTerritory(src, message + "to?");
    int units = readAnInteger("Player" + setting.getPlayerId() + ", how many units do you want to attack?");
    return new AttackOrder(src, dest, units);
  }

  /**
   * Player plays one turn by
   * 1. Display the map (recv a map from server and display it)
   * 2. Display commands (Move, Attack, Done) repeatedly until Done, allowing 0-N
   * orders in each turn
   * 3. Receive user input for one/multiple command(s), and the parameters for
   * each
   * command (refer to Move in Battleship). readACommand, readATerritory(display
   * all territories and number them with A, B, C, ... -> decode them back to a
   * territory and send it back to server) -> MoveFrom, MoveTo
   * 4. Compile up all Moves and all Attacks in a single Commit and send it back
   * to
   * server in some order (List of Movements, List of Attacks?)
   * - 4.1 Rules and checks are encapsulated in List (has 0-N MoveOrder(s); each
   * MoveOrder extends SimpleMove) and List (has 0-N AttackOrder(s); each
   * AttackOrder extends SimpleMove)? after user input and before sending it to
   * server?
   * 
   * - Rules of Move: territories throughout the path from src to dest belong to
   * the
   * current player; Number of units for moving belongs to [0, numberOfUnits(src)]
   * - Rules of Attack: src must belong to the current player; dest must belong to
   * another player; Number of units for moving belongs to [1, numberOfUnits(src)]
   * 
   * 
   * 5. Issue orders: Commit (Has-A List; Has-A List)
   * 
   * @throws IOException, {@link ClassNotFoundException}
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
