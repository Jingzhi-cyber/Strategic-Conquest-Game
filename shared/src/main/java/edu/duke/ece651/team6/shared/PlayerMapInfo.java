package edu.duke.ece651.team6.shared;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

public class PlayerMapInfo implements java.io.Serializable {
    private final int playerId;
    private final HashMap<Territory, HashSet<String>> playerMap;

    /**
     * Construct a PlayerMapInfo with the player's id and territories owned by the player, and their neighbors
     * @param playerId
     * @param playerMap HashMap - Key: territory Value: the name of its neighbors
     */
    public PlayerMapInfo(int playerId, HashMap<Territory, HashSet<String>> playerMap) {
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
    public HashSet<String> getTerritoryNeighbors(Territory t) {
        return playerMap.get(t);
    }
}
