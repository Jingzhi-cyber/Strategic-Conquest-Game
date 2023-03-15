package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Queue;

public class GameMap implements java.io.Serializable {
    /**
     * Use adjacent list to store the graph structure
     * Key - Territory(use Territory.name as hashkey), Value - Set of neighbors of that Territory
     */
    private final HashMap<Territory, HashSet<Territory>> adjList;
    private final HashMap<String, Territory> nameToTerritory;

    /**
     * Construct a GameMap by injecting a adjacent list
     * @param adjList defines the graph structure
     */
    public GameMap(HashMap<Territory, HashSet<Territory>> adjList) {
        this.adjList = adjList;
        this.nameToTerritory = new HashMap<String, Territory>();
        for (Territory t : adjList.keySet()) {
            nameToTerritory.put(t.getName(), t);
        }
    }

    /**
     * Get corresponding Territory to a name
     * @param name
     * @return a Territory
     */
    public Territory getTerritoryByName(String name) {
        return nameToTerritory.get(name);
    }

  /**
   * Get all the territories
   * 
   * @return the Set of all territories
   */
  public Set<Territory> getTerritorySet() {
    return adjList.keySet();
  }

  /**
   * Get the neighbors of a Territory
   * 
   * @param territory a territory
   * @return the Set of all the neighbors of the territory
   */
  public HashSet<Territory> getNeighborSet(Territory territory) {
    for (Territory t : adjList.keySet()) {
      if (t.equals(territory)) {
        return adjList.get(t);
      }
    }
    return new HashSet<Territory>();
  }

  /**
   * Check if there is a path consists of Territories that owned by the same owner
   * of territoryFrom and territoryTo
   * Apply BFS to search the path
   * 
   * @param territoryFrom
   * @param territoryTo
   * @return true if the path exists
   */
  public boolean hasPath(Territory territoryFrom, Territory territoryTo) {
    int ownerId = territoryFrom.getOwnerId();
    HashSet<Territory> visited = new HashSet<Territory>();
    Queue<Territory> q = new LinkedList<Territory>();
    q.add(territoryFrom);
    while (!q.isEmpty()) {
      Territory curr = q.poll();
      visited.add(curr);
      if (curr.equals(territoryTo)) {
        return true;
      }
      for (Territory neighbor : adjList.get(curr)) {
        if (visited.contains(neighbor) || neighbor.getOwnerId() != ownerId) {
          continue;
        }
        q.add(neighbor);
      }
    }
    return false;
  }
}
