package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.duke.ece651.team6.server.MongoHero;
import edu.duke.ece651.team6.shared.*;

public class RiscMaster implements Master, Serializable {

  private Server server;
  private final int playerNum;
  private final GameMap gameMap;
  private final int territoryNum;
  private final int availableUnits;
  private final Set<Integer> disconnectedList;

  private final List<PlayerProfile> playerProfiles;
  private final Set<Integer> losers;
  public String status;

  /**
   * Construct a RiscMaster by passing a server - Mainly for testing
   * 
   * @param server
   * @param playerNum
   * @param gameMap
   * @throws IOException
   */
  public RiscMaster(Server server, int playerNum, GameMap gameMap) {
    if (playerNum < 2 || playerNum > 4) {
      throw new IllegalArgumentException(
          "player number needs to be between 2 - 4 but is " + playerNum);
    }
    this.playerNum = playerNum;
    this.server = server;
    this.gameMap = gameMap;
    this.territoryNum = this.gameMap.getTerritoryNum();
    this.availableUnits = Constants.UNITS_PER_PLAYER;
    this.disconnectedList = new HashSet<>();
    this.playerProfiles = new ArrayList<>();
    this.losers = new HashSet<>();
    this.status = "INIT";
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
    SocketManager socketManager = SocketManager.getInstance();
    System.out.println("RiscMaster.server initializing connection to players...");
    List<SocketKey> clientSockets = server.getClientSockets();
    int playerId = 0;
    System.out.println("playerProfiles size: " + playerProfiles.size());
    if (playerProfiles.size() != 0) {
      for (SocketKey key : clientSockets) {
        String username = socketManager.getUsername(key);
        for (PlayerProfile playerProfile : playerProfiles) {
          if (playerProfile.getName().equals(username)) {
            playerProfile.setSocket(key);
          }
        }
      }
    } else {
      for (SocketKey key : clientSockets) {
        PlayerProfile playerProfile = new PlayerProfile(playerId, socketManager.getUsername(key));
        playerId++;
        playerProfile.setSocket(key);
        playerProfiles.add(playerProfile);
      }
    }
    System.out.println("RiscMaster.server initialize player connection finished!");
  }

  /**
   * Set up the GameMap before starting the game
   * - Send assigned Territories to each player
   * - Receive players' placement of units to their territories
   */
  public void setUpGameBasicSettings() {
    /**
     * Randomly assign same amount of Territories to players
     * Send GameBasicSetting including territories and total units available for
     * placement to players
     */
    LinkedList<Set<Territory>> assignedTerritoryList = new LinkedList<>(generateRandomTerritoryAssignment());
    Map<Integer, Set<Territory>> assignedTerritoryMap = new HashMap<>();
    for (int playerId = 0; playerId < playerNum; playerId++) {
      Set<Territory> assignedTerritories = assignedTerritoryList.poll();
      assignedTerritoryMap.put(playerId, assignedTerritories);
      for (Territory t : assignedTerritories) {
        t.setOwnerId(playerId);
      }
    }
    for (int playerId = 0; playerId < playerNum; playerId++) {
      Set<Territory> assignedTerritories = assignedTerritoryMap.get(playerId);
      GameBasicSetting gameBasicSetting = new GameBasicSetting(playerId, playerNum, this.gameMap, assignedTerritories,
          availableUnits);
      safeSendObjectToPlayer(playerId, gameBasicSetting, "GameBasicSetting");
    }
    gameMap.initMaxTechLevel();

    /**
     * Receive unit placement from player
     * Check if that placement is valid (already checked in client side)
     */
    for (int playerId = 0; playerId < playerNum; playerId++) {
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
    printGameMap();

    /**
     * Calculate Resources for each player and update playerProfile
     */
    Set<Integer> playerIds = new HashSet<>();
    for (PlayerProfile playerProfile : playerProfiles) {
      playerIds.add(playerProfile.getId());
    }
    ResourceProduceCounter resourceProduceCounter = new ResourceProduceCounter(this.gameMap.getTerritorySet(),
        playerIds);
    this.gameMap.updateResource(resourceProduceCounter.updateAndGetResult());

    /**
     * Generate GlobalMapInfo. This should be the same for all players.
     */
    GlobalMapInfo globalMapInfo = createGlobalMapInfo();

    /**
     * Send GlobalMapInfo to all players.
     */
    disconnectedList.clear();
    for (int playerId = 0; playerId < playerNum; playerId++) {
      globalMapInfo.playerId = playerId;
      if (!safeSendObjectToPlayer(playerId, globalMapInfo, "GlobalMapInfo")) {
        disconnectedList.add(playerId);
      }
    }
    if (disconnectedList.size() == playerNum) {
      System.out.println("No players connected - Game Ends");
      return true;
    }

    /**
     * Receive commit from each player, check if that is valid (already checked in
     * client side)
     * Execute the commits
     */
    List<Object> objects = new ArrayList<>();
    for (int playerId = 0; playerId < playerNum; playerId++) {
      if (disconnectedList.contains(playerId) || losers.contains(playerId)) {
        continue;
      }
      Object o = safeRecvObjectFromPlayer(playerId, "Commit");
      if (o != null) {
        objects.add(o);
      } else {
        disconnectedList.add(playerId);
      }
    }
    if (disconnectedList.size() == playerNum) {
      System.out.println("No players connected - Game Ends");
      return true;
    }

    List<Commit> commits = new ArrayList<Commit>();
    for (Object o : objects) {
      Commit commit = (Commit) o;
      // // double-check the commit on Master side
      // try {
      // commit.checkAll(gameMap);
      // } catch (IllegalArgumentException e) {
      // System.out.println("Receive Commit that is invalid, drop that commit");
      // System.out.println(e.getMessage());
      // continue;
      // }
      commits.add(commit);
    }
    executeCommits(commits);

    /**
     * Check if anyone wins.
     * Check if anyone loses.
     */
    Result result = checkResult();
    for (int playerId = 0; playerId < playerNum; playerId++) {
      if (disconnectedList.contains(playerId)) {
        continue;
      }
      if (!safeSendObjectToPlayer(playerId, result, "Result")) {
        disconnectedList.add(playerId);
      }
    }
    if (disconnectedList.size() == playerNum) {
      System.out.println("No players connected - Game Ends");
      return true;
    }

    losers.addAll(result.getLosers());

    // For UIGame, does not receive isExit from player
    // This is not compatible with text based game logic now
    
    // for (int playerId : losers) {
    //   Boolean isExit = (Boolean) safeRecvObjectFromPlayer(playerId, "isExit");
    //   if (isExit != null && isExit) {
    //     try {
    //       server.closeClientSocket(playerProfiles.get(playerId).getSocket());
    //     } catch (IOException e) { // handle error occurs when closeClientSocket()
    //       System.out.println("server.closeClientSocket() error: " + e.getMessage());
    //     }
    //   }
    // }

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
    for (int playerId = 0; playerId < playerNum; playerId++) {
      server.closeClientSocket(playerProfiles.get(playerId).getSocket());
    }
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
  private boolean safeSendObjectToPlayer(int playerId, Object object, String objectName) {
    SocketKey socket = playerProfiles.get(playerId).getSocket();
    System.out.println("Send " + objectName + " to playerId: " + playerId + " ...");
    try {
      server.sendObject(socket, object);
    } catch (IOException e1) { // network error when sending e.g. the player disconnected
      System.out.println("Send " + objectName + " to playerId: " + playerId + " error: " + e1.getMessage());
      // connectedPlayers.remove(playerId);
      try {
        server.closeClientSocket(socket);
      } catch (IOException e2) { // handle error occurs when closeClientSocket()
        System.out.println("server.closeClientSocket() error: " + e2.getMessage());
      }
      return false;
    }
    System.out.println("Send " + objectName + " to playerId: " + playerId + " success!");
    return true;
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
    SocketKey socket = playerProfiles.get(playerId).getSocket();
    Object object = null;
    try {
      object = server.recvObject(socket);
      System.out.println("Receive " + objectName + " from playerId: " + playerId + " success!");
    } catch (IOException e1) { // network error when sending e.g. the player disconnected
      System.out.println("Receive " + objectName + " from playerId: " + playerId + " error: " + e1.getMessage());
      // connectedPlayers.remove(playerId);
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
    Set<Integer> playerIds = new HashSet<>();
    for (PlayerProfile playerProfile : playerProfiles) {
      playerIds.add(playerProfile.getId());
    }
    for (int id : playerIds) {
      globalMapInfo.addPlayerMapInfo(createPlayerMapInfo(id));
    }
    return globalMapInfo;
  }

  /**
   * Randomly divide territories into parts with equal number of territories
   * corresponding to playerNum
   * 
   * @return LinkedList that contains those parts
   */
  private List<Set<Territory>> generateRandomTerritoryAssignment() {
    List<Territory> territories = new LinkedList<Territory>(gameMap.getTerritorySet());
    Collections.shuffle(territories);
    int territoryNumPerPlayer = territoryNum / playerNum;
    List<Set<Territory>> assignedTerritoryList = new LinkedList<>();
    for (int i = 0; i < playerNum; ++i) {
      Set<Territory> assignedTerritories = new HashSet<>();
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
    Map<Territory, Integer> unitPlacement = gameBasicSetting.getUnitPlacement();
    for (Territory t : unitPlacement.keySet()) {
      Territory territory = gameMap.getTerritoryByName(t.getName());
      territory.initNumUnits(unitPlacement.get(t));
    }
  }

  /**
   * Create GameMap info according to a specific player
   * 
   * @param playerId
   * @return player's territory1 - territory1's neighbors' names
   *         player's territory2 - territory2's neighbors' names
   */
  private PlayerMapInfo createPlayerMapInfo(int playerId) {
    Map<Territory, Map<Territory, Integer>> info = new HashMap<>();
    Set<Territory> territories = gameMap.getTerritorySet();
    for (Territory t : territories) {
      if (t.getOwnerId() == playerId) {
        info.put(t, gameMap.getNeighborDist(t));
      }
    }
    return new PlayerMapInfo(playerId, info);
  }

  /**
   * Execute Commits and update territories
   * 
   * @param commits
   */
  private void executeCommits(List<Commit> commits) {
    for (Commit c : commits) {
      c.performUpgrade(gameMap);
    }
    for (Commit c : commits) {
      c.performResearch(this.gameMap);
    }
    for (Commit c : commits) {
      c.performMoves(this.gameMap);
    }
    for (Commit c : commits) {
      c.performCloakTerritory(this.gameMap);
    }
    for (Commit c : commits) {
      c.performGenerateSpyOrder(this.gameMap);
    }
    for (Commit c : commits) {
      c.performMoveSpyOrder(this.gameMap);
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
    Map<Integer, Integer> territoryCnt = new HashMap<>();
    Set<Territory> territories = gameMap.getTerritorySet();
    for (int playerId = 0; playerId < playerNum; playerId++) {
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

  public void setServer(Server server) {
    this.server = server;
  }
}
