package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class GameMap {
  /**
   * Use adjacent list to store the graph structure
   * Key - Territory(use Territory.name as hashkey), Value - Set of neighbors of
   * that Territory
   */
  private final HashMap<Territory, HashSet<Territory>> adjList;

  /**
   * Construct a GameMap by injecting a adjacent list
   * 
   * @param adjList defines the graph structure
   */
  public GameMap(HashMap<Territory, HashSet<Territory>> adjList) {
    this.adjList = adjList;
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
   * Check if 2 territories are connected through a path along which all
   * territories belong to the player who owns the src territory.
   * 
   * @param src
   * @param dest
   * @return boolean
   */
  public boolean hasSamePlayerPath(Territory src, Territory dest) {
    // Use a set to keep track of visited territories
    HashSet<Territory> visited = new HashSet<>();
    visited.add(src);

    // Use a queue for breadth-first search
    Queue<Territory> queue = new LinkedList<>();
    queue.add(src);

    // Use a flag to indicate whether all territories along the path have the same
    // player id
    int playerId = src.getOwnerId();

    // Breadth-first search
    while (!queue.isEmpty()) {
      Territory curr = queue.poll();
      for (Territory neighbor : adjList.get(curr)) {
        if (neighbor.equals(dest)) {
          return true;
        }
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          if (neighbor.getOwnerId() == playerId) {
            queue.add(neighbor);
          }
        }
      }
    }

    // If dest is not reached, there is no path
    return false;
  }

}
