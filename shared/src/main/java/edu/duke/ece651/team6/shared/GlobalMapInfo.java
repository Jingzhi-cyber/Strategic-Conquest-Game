package edu.duke.ece651.team6.shared;

import java.util.*;

public class GlobalMapInfo implements java.io.Serializable {
    private Map<Integer, PlayerMapInfo> globalMap;
    private GameMap gameMap;
    // To tell the newly connected socket playerId.
    public int playerId = -1;
    
    /**
     * Construct GlobalMapInfo with empty globalMap
     */
    public GlobalMapInfo(GameMap gameMap) {
        this.globalMap = new HashMap<>();
        this.gameMap = gameMap;
    }

  /**
   * Construct GlobalMapInfo with empty globalMap
   */
  public GlobalMapInfo() {
    this.globalMap = new HashMap<>();
  }

  /**
   * Add a PlayerMapInfo to globalMap
   * 
   * @param playerMapInfo
   */
  public void addPlayerMapInfo(PlayerMapInfo playerMapInfo) {
    this.globalMap.put(playerMapInfo.getPlayerId(), playerMapInfo);
  }

  /**
   * Get Id of players
   * 
   * @return Set of player id
   */
  public Set<Integer> getPlayers() {
    return this.globalMap.keySet();
  }

  /**
   * Get PlayerMapInfo according to playerId
   * 
   * @param playerId
   * @return PlayerMapInfo
   */
  public PlayerMapInfo getPlayerMapInfo(int playerId) {
    return this.globalMap.get(playerId);
  }

  public Set<String> getTerritoryNames() {
    Set<String> result = new HashSet<String>();
    for (PlayerMapInfo playerMapInfo : globalMap.values()) {
      for (Territory territory : playerMapInfo.getTerritories()) {
        result.add(territory.getName());
      }
    }
    return result;
  }

  public Map<Integer, PlayerMapInfo> getGlobalMap() {
    return this.globalMap;
  }

  /**
   * Get GameMap
   * @return GameMap
   */
  public GameMap getGameMap() {
      return this.gameMap;
  }
}
