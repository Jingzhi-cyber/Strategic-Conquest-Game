package edu.duke.ece651.team6.shared;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

public class PlayerMapInfo implements java.io.Serializable {
    private final HashMap<Territory, HashSet<String>> playerMapInfo;

    /**
     * Construct a PlayerMapInfo with HashMap<Territory, HashSet<String>>
     * @param playerMapInfo
     */
    public PlayerMapInfo(HashMap<Territory, HashSet<String>> playerMapInfo) {
        this.playerMapInfo = playerMapInfo;
    }

    /**
     * Get all the Territories of the player
     * @return Set of the Territories 
     */
    public Set<Territory> getTerritories() {
        return playerMapInfo.keySet();
    }

    /**
     * Get all the neighbors of one Territory 
     * @param t a Territory
     * @return HashSet of Strings that represents the neighbors' name
     */
    public HashSet<String> getTerritoryNeighbors(Territory t) {
        return playerMapInfo.get(t);
    }
}
