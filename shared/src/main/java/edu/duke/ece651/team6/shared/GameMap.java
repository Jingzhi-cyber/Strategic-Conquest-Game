package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GameMap {
    /**
     * Use adjacent list to store the graph structure
     * Key - Territory(use Territory.name as hashkey), Value - Set of neighbors of that Territory
     */
    private final HashMap<Territory, HashSet<Territory>> adjList;

    /**
     * Construct a GameMap by injecting a adjacent list
     * @param adjList defines the graph structure
     */
    public GameMap(HashMap<Territory, HashSet<Territory>> adjList) {
        this.adjList = adjList;
    }

    /**
     * Get all the territories
     * @return the Set of all territories
     */
    public Set<Territory> getTerritorySet() {
        return adjList.keySet();
    }

    /**
     * Get the neighbors of a Territory
     * @param territoryName the name of territory
     * @return the Set of all the neighbors of the territory
     */
    public HashSet<Territory> getNeighborSet(String territoryName) {
        for (Territory t : adjList.keySet()) {
            if (t.getName().equals(territoryName)) {
                return adjList.get(t);
            }
        }
        return new HashSet<Territory>();
    }

}
