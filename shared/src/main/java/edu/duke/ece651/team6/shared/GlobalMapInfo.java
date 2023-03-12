package edu.duke.ece651.team6.shared;
import java.util.*;

public class GlobalMapInfo implements java.io.Serializable {
    private HashMap<Integer, PlayerMapInfo> globalMap;
    
    /**
     * Construct GlobalMapInfo with empty globalMap
     */
    public GlobalMapInfo() {
        this.globalMap = new HashMap<Integer, PlayerMapInfo>();
    }

    /**
     * Add a PlayerMapInfo to globalMap
     * @param playerMapInfo
     */
    public void addPlayerMapInfo(PlayerMapInfo playerMapInfo) {
        this.globalMap.put(playerMapInfo.getPlayerId(), playerMapInfo);
    }

    /**
     * Get Id of players
     * @return Set of player id
     */
    public Set<Integer> getPlayers() {
        return this.globalMap.keySet();
    }

    /**
     * Get PlayerMapInfo according to playerId
     * @param playerId
     * @return PlayerMapInfo
     */
    public PlayerMapInfo getPlayerMapInfo(int playerId) {
        return this.globalMap.get(playerId);
    }
}
