package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.duke.ece651.team6.shared.PlayerProfile;
import edu.duke.ece651.team6.shared.Result;
import edu.duke.ece651.team6.shared.SampleMap;
import edu.duke.ece651.team6.shared.SimpleMap;
import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;
//import edu.duke.ece651.team6.shared.TestMap;

public class RiscMaster implements Master {

  private final Server server;
  private final int serverPort;
  private final int playerNum;
  private final int territoryNum;
  private final int availableUnits;
  private HashMap<Integer, PlayerProfile> playerProfiles;
  private GameMap gameMap;
  private HashSet<Integer> losers;

  public RiscMaster(int port, int playerNum) throws IOException {
    if (port <= 0 || port > 65535) {
      throw new IllegalArgumentException("port number needs to be between 1 - 65535 but is " + Integer.toString(port));
    }
    this.serverPort = port;
    if (playerNum < 2 || playerNum > 4) {
      throw new IllegalArgumentException(
          "player number needs to be between 2 - 4 but is " + Integer.toString(playerNum));
    }
    this.playerNum = playerNum;
    this.server = new Server(serverPort);
    // this.territoryNum = 24;
    this.territoryNum = 2;
    this.availableUnits = 48;
    this.playerProfiles = new HashMap<Integer, PlayerProfile>();
    this.losers = new HashSet<Integer>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean init() throws IOException {
    /**
     * Wait for all players connecting to Master.
     * For each player connects to Master:
     * - Generate an unique player id to constuct a PlayerProfile, set the
     * corresponding socket, update playerProfiles
     */
    System.out.println("RiscMaster.server initializing connection to players...");
    server.acceptMultiPlayers(playerNum);
    ArrayList<Socket> clientSockets = server.getClientSockets();
    int ownerId = 0;
    for (Socket sock : clientSockets) {
      PlayerProfile playerProfile = new PlayerProfile(ownerId);
      playerProfile.setSocket(sock);
      playerProfiles.put(ownerId, playerProfile);
      ownerId++;
    }
    System.out.println("RiscMaster.server initialize player connection finished!");
    return true;
  }

  public void prepare() {
    /**
     * Generate a random GameMap according to playerNum
     */
    // MapGenerator mapGenerator = new MapGenerator(24);
    // this.gameMap = new GameMap(mapGenerator.getTheMap());

    // Testing: Use SampleMap
    // SampleMap sampleMap = new SampleMap();
    // GameMap gm = new GameMap(sampleMap.getAdjList());
    // this.gameMap = gm;

    // Testing: Use SimpleMap
    SimpleMap simpleMap = new SimpleMap();
    GameMap gm = new GameMap(simpleMap.getAdjList());
    this.gameMap = gm;

    /**
     * Randomly assign same amount of Territories to players
     * Send GameBasicSetting including territories and total units available for
     * placement to players
     */
    ArrayList<Territory> territories = new ArrayList<Territory>(gameMap.getTerritorySet());
    Collections.shuffle(territories);
    int territoryNumPerPlayer = territoryNum / playerNum;
    ArrayList<HashSet<Territory>> assignedTerritoryList = new ArrayList<HashSet<Territory>>();
    for (int i = 0; i < playerNum; ++i) {
      HashSet<Territory> assignedTerritories = new HashSet<Territory>();
      for (int j = i * territoryNumPerPlayer; j < (i + 1) * territoryNumPerPlayer; ++j) {
        assignedTerritories.add(territories.get(j));
      }
      assignedTerritoryList.add(assignedTerritories);
    }

    int curr = 0;
    for (int ownerId : playerProfiles.keySet()) {
      HashSet<Territory> assignedTerritories = assignedTerritoryList.get(curr);
      curr++;
      for (Territory t : assignedTerritories) {
        t.setOwnerId(ownerId);
      }
      GameBasicSetting gameBasicSetting = new GameBasicSetting(ownerId, playerNum, assignedTerritories, availableUnits);
      Socket sock = playerProfiles.get(ownerId).getSocket();
      System.out.println("Sending GameBasicSetting to playerId: " + ownerId + " ...");
      try {
        server.sendObject(sock, gameBasicSetting);
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
      System.out.println("Send GameBasicSetting to playerId: " + ownerId + " finished!");
    }

    /**
     * Receive unit placement from player
     * Check if that placement is valid (already checked in client side)
     */
    System.out.println("Receiving GameBasicSetting from players...");
    ArrayList<Object> objects = server.recvObjectFromALL();
    System.out.println("Receive GameBasicSetting from players finished!");

    // If valid, execute that placement
    for (Object o : objects) {
      GameBasicSetting gameBasicSetting = (GameBasicSetting) o;
      HashMap<Territory, Integer> unitPlacement = gameBasicSetting.getUnitPlacement();
      for (Territory t : unitPlacement.keySet()) {
        Territory territory = gameMap.getTerritoryByName(t.getName());
        territory.initNumUnits(unitPlacement.get(t));
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean playOneTurn() throws IOException {
    printGameMap();
    /**
     * Generate GlobalMapInfo. This should be the same for all players.
     */
    GlobalMapInfo globalMapInfo = new GlobalMapInfo(this.gameMap);
    for (int id : playerProfiles.keySet()) {
      globalMapInfo.addPlayerMapInfo(createPlayerMapInfo(id));
    }

    /**
     * Send GlobalMapInfo to all players.
     */
    System.out.println("Sending GlobalMapInfo to players...");
    server.sendObjectToAll(globalMapInfo);
    System.out.println("Send GlobalMapInfo to players finished!");

    /**
     * Receive commit from each player, check if that is valid (already checked in
     * client side)
     * Execute the commits
     */
    System.out.println("Receiving Commit from players...");
    ArrayList<Object> objects = new ArrayList<Object>();
    for (int playerId : playerProfiles.keySet()) {
      if (losers.contains(playerId)) {
        continue;
      }
      try {
        objects.add(server.recvObject(playerProfiles.get(playerId).getSocket()));
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
    System.out.println("Receive Commit from players finished!");

    ArrayList<Commit> commits = new ArrayList<Commit>();
    for (Object o : objects) {
      Commit commit = (Commit) o;
      commits.add(commit);
    }
    for (Commit c : commits) {
      c.performMoves(this.gameMap);
    }
    for (Commit c : commits) {
      c.performAttacks(this.gameMap);
    }
    Set<Territory> territories = this.gameMap.getTerritorySet();
    for (Territory territory : territories) {
      territory.update();
    }
    System.out.println("Commits execution finished!");

    /**
     * Check if anyone wins.
     * Check if anyone loses.
     */
    Result result = checkResult();
    server.sendObjectToAll(result);
    if (!result.getWinners().isEmpty()) {
      for (int winnerId : result.getWinners()) {
        System.out.println("Winner! PlayerId: " + winnerId);
      }
      return true;
    }

    if (!result.getLosers().isEmpty()) {
      for (int loserId : result.getLosers()) {
        System.out.println("Loser! PlayerId: " + loserId);
        losers.add(loserId);
      }
    }
    return false;
  }

  /**
   * Finish the game and close the sockets
   * 
   * @throws IOException
   */
  public void finish() throws IOException {
    for (int playerId : playerProfiles.keySet()) {
      server.closeClientSocket(playerProfiles.get(playerId).getSocket());
    }
    server.closeServerSocket();
  }

  /**
   * Create GameMap info according to a specific player
   * 
   * @param player
   * @return player's territory1 - territory1's neighbors' names
   *         player's territory2 - territory2's neighbors' names
   */
  private PlayerMapInfo createPlayerMapInfo(int playerId) {
    HashMap<Territory, HashSet<String>> info = new HashMap<Territory, HashSet<String>>();
    Set<Territory> territories = gameMap.getTerritorySet();
    for (Territory t : territories) {
      if (t.getOwnerId() == playerId) {
        HashSet<String> neighbor = new HashSet<String>();
        for (Territory n : gameMap.getNeighborSet(t)) {
          neighbor.add(n.getName());
        }
        info.put(t, neighbor);
      }
    }

    PlayerMapInfo playerMapInfo = new PlayerMapInfo(playerId, info);
    return playerMapInfo;
  }

  /**
   * Check if any player wins or loses after every turn
   * 
   * @return Result
   */
  private Result checkResult() {
    Result result = new Result();
    HashMap<Integer, Integer> territoryCnt = new HashMap<Integer, Integer>();
    Set<Territory> territories = gameMap.getTerritorySet();
    for (Territory t : territories) {
      int ownerId = t.getOwnerId();
      Integer count = territoryCnt.containsKey(ownerId) ? territoryCnt.get(ownerId) : 0;
      territoryCnt.put(ownerId, count + 1);
    }
    for (int ownerId : territoryCnt.keySet()) {
      int ownedTerritoryCnt = territoryCnt.get(ownerId);
      if (ownedTerritoryCnt == territoryNum) {
        result.addWinner(ownerId);
      } else if (ownedTerritoryCnt == 0) {
        result.addLoser(ownerId);
      }
    }
    return result;
  }

  /**
   * Debug method, print info of every Territory to stdout
   */
  private void printGameMap() {
    Set<Territory> territories = gameMap.getTerritorySet();
    for (Territory t : territories) {
      System.out.println("name: " + t.getName() + " ownerId: " + t.getOwnerId() + " numUnits: " + t.getNumUnits());
    }
  }

  public static void main(String[] args) throws IOException, UnknownHostException, ClassNotFoundException {

  }
}
