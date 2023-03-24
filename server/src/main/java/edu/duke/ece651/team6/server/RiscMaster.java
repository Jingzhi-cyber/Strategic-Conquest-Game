package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import edu.duke.ece651.team6.shared.PlayerProfile;
import edu.duke.ece651.team6.shared.Result;
import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GameMap;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.PlayerMapInfo;
import edu.duke.ece651.team6.shared.Territory;
import edu.duke.ece651.team6.shared.Constants;
//import edu.duke.ece651.team6.shared.TestMap;

public class RiscMaster implements Master {

  private final Server server;
  private final int playerNum;
  private final GameMap gameMap;
  private final int territoryNum;
  private final int availableUnits;

  private HashMap<Integer, PlayerProfile> playerProfiles;
  private CopyOnWriteArraySet<Integer> connectedPlayers;
  private HashSet<Integer> losers;

  /**
   * Construct a RiscMaster
   * 
   * @param port the port Server listens on
   * @param playerNum number of players
   * @param gameMap map of the game
   * @throws IOException if creating new Server fails
   */
  public RiscMaster(int port, int playerNum, GameMap gameMap) throws IOException {
    if (port <= 0 || port > 65535) {
      throw new IllegalArgumentException("port number needs to be between 1 - 65535 but is " + Integer.toString(port));
    }
    if (playerNum < 2 || playerNum > 4) {
      throw new IllegalArgumentException(
          "player number needs to be between 2 - 4 but is " + Integer.toString(playerNum));
    }
    this.playerNum = playerNum;
    this.server = new Server(port);
    this.gameMap = gameMap;
    this.territoryNum = this.gameMap.getTerritoryNum();
    if (this.territoryNum % this.playerNum != 0) {
      throw new IllegalArgumentException("The number of territories does not work for the number of players! Some territories will not have owner.");
    }
    this.availableUnits = Constants.UNITS_PER_PLAYER;
    this.playerProfiles = new HashMap<Integer, PlayerProfile>();
    this.losers = new HashSet<Integer>();
    this.connectedPlayers = new CopyOnWriteArraySet<Integer>();
  }

  /**
   * Construct a RiscMaster by passing a server - Mainly for testing
   * 
   * @param server
   * @param playerNum
   * @param gameMap
   * @throws IOException
   */
  public RiscMaster(Server server, int playerNum, GameMap gameMap) throws IOException {
    if (playerNum < 2 || playerNum > 4) {
      throw new IllegalArgumentException(
          "player number needs to be between 2 - 4 but is " + Integer.toString(playerNum));
    }
    this.playerNum = playerNum;
    this.server = server;
    this.gameMap = gameMap;
    this.territoryNum = this.gameMap.getTerritoryNum();
    this.availableUnits = Constants.UNITS_PER_PLAYER;
    this.playerProfiles = new HashMap<Integer, PlayerProfile>();
    this.losers = new HashSet<Integer>();
    this.connectedPlayers = new CopyOnWriteArraySet<Integer>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init() throws IOException {
    /**
     * Wait for all players connecting to Master.
     * For each player connects to Master:
     * - Generate an unique player id to constuct a PlayerProfile, set the
     * corresponding socket, update playerProfiles
     */
    System.out.println("RiscMaster.server initializing connection to players...");
    server.acceptMultiPlayers(playerNum);
    ArrayList<Socket> clientSockets = server.getClientSockets();
    int playerId = 0;
    for (Socket socket : clientSockets) {
      PlayerProfile playerProfile = new PlayerProfile(playerId);
      playerProfile.setSocket(socket);
      playerProfiles.put(playerId, playerProfile);
      connectedPlayers.add(playerId);
      playerId++;
    }
    System.out.println("RiscMaster.server initialize player connection finished!");
  }

  /**
   * Set up the GameMap before starting the game
   *  - Send assigned Territories to each player
   *  - Receive players' placement of units to their territories
   */
  public void setUpGameBasicSettings() {
    /**
     * Randomly assign same amount of Territories to players
     * Send GameBasicSetting including territories and total units available for
     * placement to players
     */
    LinkedList<HashSet<Territory>> assignedTerritoryList = generateRandomTerritoryAssignment();


    for (int playerId : connectedPlayers) {
      HashSet<Territory> assignedTerritories = assignedTerritoryList.poll();
      for (Territory t : assignedTerritories) {
        t.setOwnerId(playerId);
      }
      GameBasicSetting gameBasicSetting = new GameBasicSetting(playerId, playerNum, assignedTerritories, availableUnits);
      safeSendObjectToPlayer(playerId, gameBasicSetting, "GameBasicSetting");
    }

    /**
     * Receive unit placement from player
     * Check if that placement is valid (already checked in client side)
     */
    for (int playerId : connectedPlayers) {
      Object o = safeRecvObjectFromPlayer(playerId, "GameBasicSetting");
      if (o != null) {
        GameBasicSetting gameBasicSetting = (GameBasicSetting) o;
        assignUnitPlacements(gameBasicSetting);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean playOneTurn() throws IOException {
    if (connectedPlayers.size() == 0) {
      System.out.println("No players connected - Game Ends");
      return true;
    }
    printGameMap();
    /**
     * Generate GlobalMapInfo. This should be the same for all players.
     */
    GlobalMapInfo globalMapInfo = createGlobalMapInfo();

    /**
     * Send GlobalMapInfo to all players.
     */
    for (int playerId : connectedPlayers) {
      safeSendObjectToPlayer(playerId, globalMapInfo, "GlobalMapInfo");
    }

    /**
     * Receive commit from each player, check if that is valid (already checked in
     * client side)
     * Execute the commits
     */
    ArrayList<Object> objects = new ArrayList<Object>();
    for (int playerId : connectedPlayers) {
      if (losers.contains(playerId)) {
        continue;
      }
      Object o = safeRecvObjectFromPlayer(playerId, "Commit");
      if (o != null) {
        objects.add(o);
      }
    }

    ArrayList<Commit> commits = new ArrayList<Commit>();
    for (Object o : objects) {
      Commit commit = (Commit) o;
      // double-check the commit on Master side
      try {
        commit.checkAll(gameMap);
      } catch (IllegalArgumentException e) {
        System.out.println("Receive Commit that is invalid, drop that commit");
        continue;
      }
      commits.add(commit);
    }
    executeCommits(commits);

    /**
     * Check if anyone wins.
     * Check if anyone loses.
     */
    Result result = checkResult();
    for (int playerId : connectedPlayers) {
      safeSendObjectToPlayer(playerId, result, "Result");
    }

    if (!result.getLosers().isEmpty()) {
      for (int loserId : result.getLosers()) {
        losers.add(loserId);
      }
    }

    for (int playerId : losers) {
      if (!connectedPlayers.contains(playerId)) {
        continue;
      }
      Boolean isExit = (Boolean) safeRecvObjectFromPlayer(playerId, "isExit");
      if (isExit != null && isExit) {
        connectedPlayers.remove(playerId);
        try {
          server.closeClientSocket(playerProfiles.get(playerId).getSocket());
        } catch (IOException e) { // handle error occurs when closeClientSocket()
          System.out.println("server.closeClientSocket() error: " + e.getMessage());
        }
      } 
    }

    if (!result.getWinners().isEmpty()) {
      return true;
    }

    return false;
  }

  /**
   * Finish the game and close the sockets
   * 
   * @throws IOException
   */
  public void finish() throws IOException {
    for (int playerId : connectedPlayers) {
      server.closeClientSocket(playerProfiles.get(playerId).getSocket());
    }
    server.closeServerSocket();
    System.out.println("Game Ends.");
  }

  /**
   * Send an object to a player specified by playerId
   * 'safe' means java.io error are handled, especially 
   * if a player disconnects, the exceptions are caught,
   * playerId is removed from this.connectedPlayers, and
   * the socket to the player is closed
   * 
   * @param playerId
   * @param object
   * @param objectName
   */
  private void safeSendObjectToPlayer(int playerId, Object object, String objectName) {
    Socket socket = playerProfiles.get(playerId).getSocket();
    System.out.println("Send " + objectName + " to playerId: " + playerId + " ...");
    try {
      server.sendObject(socket, object);
    } catch (IOException e1) { // network error when sending e.g. the player disconnected
      System.out.println("Send " + objectName + " to playerId: " + playerId + " error: " + e1.getMessage());
      connectedPlayers.remove(playerId);
      try {
        server.closeClientSocket(socket);
      } catch (IOException e2) { // handle error occurs when closeClientSocket()
        System.out.println("server.closeClientSocket() error: " + e2.getMessage());
      }
    }
    System.out.println("Send " + objectName + " to playerId: " + playerId + " success!");
  }

  /**
   * Receive an object from a player specified by playerId
   * 'safe' means java.io error are handled, especially 
   * if a player disconnects, the exceptions are caught,
   * playerId is removed from this.connectedPlayers, and
   * the socket to the player is closed
   * 
   * @param playerId
   * @param objectName
   * @return received object
   */
  private Object safeRecvObjectFromPlayer(int playerId, String objectName) {
    Socket socket = playerProfiles.get(playerId).getSocket();
    Object object = null;
    try {
      object = server.recvObject(socket);
      System.out.println("Receive " + objectName + " from playerId: " + playerId + " success!");
    } catch (IOException e1) { // network error when sending e.g. the player disconnected
      System.out.println("Receive " + objectName + " from playerId: " + playerId + " error: " + e1.getMessage());
      connectedPlayers.remove(playerId);
      try {
        server.closeClientSocket(socket);
      } catch (IOException e2) { // handle error occurs when closeClientSocket()
        System.out.println("server.closeClientSocket() error: " + e2.getMessage());
      }
    } catch (ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }
    return object;
  }

  /**
   * Create GlobalMapInfo according to gameMap
   * 
   * @return GlobalMapInfo
   */
  private GlobalMapInfo createGlobalMapInfo() {
    GlobalMapInfo globalMapInfo = new GlobalMapInfo(this.gameMap);
    for (int id : playerProfiles.keySet()) {
      globalMapInfo.addPlayerMapInfo(createPlayerMapInfo(id));
    }
    return globalMapInfo;
  }

  /**
   * Randomly divide territories into parts with equal number of territories corresponding to playerNum 
   * @return LinkedList that contains those parts
   */
  private LinkedList<HashSet<Territory>> generateRandomTerritoryAssignment() {
    LinkedList<Territory> territories = new LinkedList<Territory>(gameMap.getTerritorySet());
    Collections.shuffle(territories);
    int territoryNumPerPlayer = territoryNum / playerNum;
    LinkedList<HashSet<Territory>> assignedTerritoryList = new LinkedList<HashSet<Territory>>();
    for (int i = 0; i < playerNum; ++i) {
      HashSet<Territory> assignedTerritories = new HashSet<Territory>();
      for (int j = i * territoryNumPerPlayer; j < (i + 1) * territoryNumPerPlayer; ++j) {
        assignedTerritories.add(territories.get(j));
      }
      assignedTerritoryList.add(assignedTerritories);
    }
    return assignedTerritoryList;
  }

  /**
   * Assign Unit placement from player to each territory
   * 
   * @param gameBasicSetting
   */
  private void assignUnitPlacements(GameBasicSetting gameBasicSetting) {
    HashMap<Territory, Integer> unitPlacement = gameBasicSetting.getUnitPlacement();
    for (Territory t : unitPlacement.keySet()) {
      Territory territory = gameMap.getTerritoryByName(t.getName());
      territory.initNumUnits(unitPlacement.get(t));
    }
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
   * Execute Commits and update territories
   * @param commits
   */
  private void executeCommits(ArrayList<Commit> commits) {
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
    for (int playerId : playerProfiles.keySet()) {
      territoryCnt.put(playerId, 0);
    }
    for (Territory t : territories) {
      int ownerId = t.getOwnerId();
      if (!territoryCnt.containsKey(ownerId)) {
        continue;
      }
      int count = territoryCnt.get(ownerId);
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
    System.out.println("---------------Master Side Game Map Begins---------------");
    Set<Territory> territories = gameMap.getTerritorySet();
    for (Territory t : territories) {
      System.out.println("name: " + t.getName() + " ownerId: " + t.getOwnerId() + " numUnits: " + t.getNumUnits());
    }
    System.out.println("---------------Master Side Game Map Ends---------------");
  }
}
