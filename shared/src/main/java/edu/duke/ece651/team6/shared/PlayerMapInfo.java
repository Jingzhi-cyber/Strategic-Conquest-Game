package edu.duke.ece651.team6.shared;
import java.util.Map;
import java.util.Set;

public class PlayerMapInfo implements java.io.Serializable {
    private final int playerId;
    private final Map<Territory, Map<Territory, Integer>> playerMap;

    /**
     * Construct a PlayerMapInfo with the player's id and territories owned by the player, and distance to their neighbors
     * @param playerId
     * @param playerMap HashMap - Key: territory Value: its neighbors and the corresponding distance
     */
    public PlayerMapInfo(int playerId, Map<Territory, Map<Territory, Integer>> playerMap) {
        this.playerId = playerId;
        this.playerMap = playerMap;
    }

    /**
     * Get player name
     * @return playerName
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Get all the Territories of the player
     * @return Set of the Territories 
     */
    public Set<Territory> getTerritories() {
        return playerMap.keySet();
    }

    /**
     * Get all the neighbors of one Territory 
     * @param t a Territory
     * @return HashSet of Strings that represents the neighbors' name
     */
    public Map<Territory, Integer> getTerritoryNeighbors(Territory t) {
        return playerMap.get(t);
    }
}
