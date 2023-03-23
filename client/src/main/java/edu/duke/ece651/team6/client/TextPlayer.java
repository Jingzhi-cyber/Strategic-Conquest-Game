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
import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.Constants;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.MoveOrder;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Result;
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
  private boolean hasLost;
  final Integer playerId;

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
    this.hasLost = false;
    this.playerId = Integer.valueOf(setting.getPlayerId());
  }

  /**
   * Place units and send the updated game setting to the server.
   * 
   * @throws IOException, ClassNotFoundException
   */
  @Override
  public void placeUnit() throws IOException, ClassNotFoundException {
    // printLine("From client: Requesting to " + prompt);
    HashMap<Territory, Integer> map = new HashMap<Territory, Integer>();
    HashSet<Territory> territories = setting.getAssignedTerritories();
    int count = 1;
    for (Territory t : territories) {
      if (count >= territories.size()) {
        map.put(t, setting.getRemainingNumUnits());
        printLine("The remaining " + setting.getRemainingNumUnits()
            + " units have been automatically placed onto territory " + t.getName());
        break;
      }
      count++;
      Integer i = readNumUnits("Player" + this.playerId + ", how many units to you want to place on the "
          + t.getName() + " territory? (" + setting.getRemainingNumUnits() + " remainings)");
      map.put(t, i);
    }

    setting.initializeUnitPlacement(map);
    
    // send back the GameBasicSetting object with the updated unit placement info.
    client.sendUpdatedGameBasicSetting(setting);
    printLine("Units placement information has been successfully sent to the server, waiting for other players...");
  }

  /**
   * @return a string to display available commands
   */
  private String displayAvailableCommands() {
    return "You are the Player" + this.playerId + ", what would you like to do?\n(M)ove\n(A)ttack\n(D)one\n";
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
   * @throws IOException, {@link IllegalArgumentException}
   */
  private Character readCommand(String prompt, HashSet<String> availableCommands) throws IOException {
    String s = readInputLine(prompt).toUpperCase();
    if (s.length() != 1 || !availableCommands.contains(s)) {
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

    PlayerMapInfo playerMapInfo = mapTextView.globalMapInfo.getPlayerMapInfo(this.playerId);

    for (Territory t : playerMapInfo.getTerritories()) {
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
    for (Map.Entry<Integer, PlayerMapInfo> entry : globalInfo.entrySet()) {
      if (!entry.getKey().equals(this.playerId)) {
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
    String message = "Player" + this.playerId + ", which territory do you want to move units ";
    Territory src = findTerritory(null, message + "from?");
    Territory dest = findTerritory(null, message + "to?");
    int units = readAnInteger("Player" + this.playerId + ", how many units do you want to move?");
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
    String message = "Player" + this.playerId + ", which territory do you want to attack ";
    Territory src = findTerritory(null, message + "from?");
    Territory dest = findTerritory(src, message + "to?");
    int units = readAnInteger("Player" + this.playerId + ", how many units do you want to attack?");
    return new AttackOrder(src, dest, units);
  }

  /**
   * Construct a commit
   * 
   * @return Commit
   * @throws IOException, {@link ClassNotFoundException}
   * @catch {@link IllegalArgumentException}
   */
  private Commit constructCommit() throws IOException, ClassNotFoundException {
    Commit commit = new Commit(this.playerId);
    while (true) {
      try {
        Character cmd = readCommand(displayAvailableCommands(), getPossibleCommandChars());
        if (cmd == Character.valueOf('D')) {
          // done
          break;
        } else if (cmd == Character.valueOf('M')) {
          /* move */
          commit.addMove(constructMove(), this.gameMap);
        } else {
          /* attack */
          commit.addAttack(constructAttack(), this.gameMap);
        }
      } catch (IllegalArgumentException e) {
        printLine(e.getMessage());
        continue;
      }
    }
    return commit;
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
  public String playOneTurn() throws IOException, ClassNotFoundException {
    /* -------- 1. Receive and display the map --------- */
    updateAndDisplayMapInfo(); // will update local variables: mapTextView and gameMap

    /*
     * ---------2. Skip constructing a commit, and skip sending it to the
     * server, instead, directly waiting for the game result. ----------
     */
    if (this.hasLost) {
      return receiveGameResult();
    }

    /*
     * -------- 3. Repeatedly display commands (Move, Attack, Done) until Done,
     * allowing 0-N orders in each turn. Repeatedly check validity of a Commit until
     * it's valid ---------
     */
    Commit commit = null;
    while (true) {
      try {
        commit = constructCommit();
        commit.checkUsableUnitsAfterAllOrdersAreCollected();
      } catch (IllegalArgumentException e) {
        printLine(e.getMessage());
        continue;
      }
      break;
    }

    /* -------- 4. Send commit to server --------- */
    this.client.sendCommit(commit);
     printLine("Order information has been successfully submitted to the server, waiting for other players...");

    /*
     * -------- 5. Handle game result of this turn --------
     */
    return receiveGameResult();
  }

  /**
   * In each turn, receive game result, decide whether to exit if having lost the
   * game and return player's choice based on game result
   * 
   * @return String is player's choice based on game status
   * @throws IOException, {@link ClassNotFoundException}
   * @catch {@link IllegalArgumentException}
   */
  private String receiveGameResult() throws IOException, ClassNotFoundException {
    Result result = this.client.recvGameResult(); // this receives a Result object
    if (result.getWinners().size() > 0) {
      printLine("Player " + result.getWinners().toString() + " wins!");
      return Constants.GAME_OVER;
    }
    if (result.getLosers().contains(this.playerId)) {
      this.hasLost = true;
      Character cmd = null;
      while (true) {
        try {
          cmd = readCommand(displayExitInfo(), getExitCommands());
        } catch (IllegalArgumentException e) {
          printLine(e.getMessage());
          continue;
        }
        break; // get the command
      }
      // TODO 1: server receives the exit info: true -> exit
      // TODO 2: server removed the player from list, and send back ack -> safe to
      // exit
      if (cmd == 'E') {
        client.sendExitInfo(Boolean.valueOf(true));
        return Constants.EXIT;
      } else {
        client.sendExitInfo(Boolean.valueOf(false));
      }
    }
    return null;
  }

  /**
   * Get a set of exit commands
   * 
   * @return HashSet<String> of exit related commands
   */
  private HashSet<String> getExitCommands() {
    return new HashSet<String>() {
      {
        add("W");
        add("E");
      }
    };
  }

  /**
   * Display exit message
   * 
   * @return String
   */
  private String displayExitInfo() {
    // TODO set a time clock, if no response, exit the player by server
    return "You have lost, do you want to keep (W)atching or (E)xit the game?";
  }

  /**
   * Play the game by repeatedly calling playOneTurn, and properly handle result
   * of each turn
   * 
   * @throws exceptions, {@link IOException}, {@link UnknownHostException},
   *                     {@link ClassNotFoundException}
   */
  @Override
  public void playGame() throws IOException, UnknownHostException, ClassNotFoundException {
    while (true) {
      String result = playOneTurn(); // Constants.EXIT or Constants.GAME_OVER
      if (result != null) {
        break;
      }
    }
  }

  /**
   * Print a string to the OutputStream of the player
   * 
   * @param str the string to print
   */
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
        "From server: Welcome to RISC, you are assigned to be Player " + this.playerId + ". \n"
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
   * Read an integer
   * 
   * @param prompt
   * @return Integer the read Integer
   * @throws NumberFormatException (can be caught by IllegalArgumentException)
   */
  private Integer readAnInteger(String prompt) throws IOException {
    return Integer.valueOf(Integer.parseInt(readInputLine(prompt)));
  }

  /**
   * Read units for placement
   * 
   * @param prompt
   * @return Integer
   * @throws IOException
   * @catch {@link IllegalArgumentException}
   */
  private Integer readNumUnits(String prompt) throws IOException {
    Integer numUnits = null;
    while (true) {
      try {
        numUnits = readAnInteger(prompt);
        setting.decreaseUnitsBy(numUnits);
      } catch (IllegalArgumentException e) {
        printLine(e.getMessage());
        printLine(prompt);
        continue;
      }
      break;
    }
    return numUnits;
  }

  /**
   * Update and display the global map information
   * 
   * @return {@link GlobalMapInfo}
   * @throws InvalidObjectException
   */
  @Override
  public GlobalMapInfo updateAndDisplayMapInfo() throws IOException, ClassNotFoundException {
    GlobalMapInfo mapInfo = this.client.recvGlobalMapInfo();
    this.mapTextView = new MapTextView(mapInfo);
    this.gameMap = mapInfo.getGameMap();
    printLine(this.mapTextView.display());
    return mapInfo;
  }
}
