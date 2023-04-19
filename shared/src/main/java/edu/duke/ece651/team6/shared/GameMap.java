package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.Random;

public class GameMap implements java.io.Serializable, Cloneable {
  /**
   * Use adjacent list to store the weighted graph structure Key - Territory(use
   * Territory.name as hashkey) Value - Key - the adjacent Territory Value - the
   * distance to the adjacent Territory
   */
  private Map<Territory, Map<Territory, Integer>> weightedAdjList;
  private Map<String, Territory> nameToTerritory;
  private int territoryNum;
  private Map<Integer, Map<String, Integer>> resources;
  private Map<Integer, Integer> maxTechLevel;

  /**
   * Construct a GameMap by injecting a adjacent list
   * 
   * @param adjList defines the graph structure
   */
  public GameMap(Map<Territory, Set<Territory>> adjList) {
    this.weightedAdjList = generateWeightedAdjList(adjList);
    this.nameToTerritory = new HashMap<String, Territory>();
    for (Territory t : weightedAdjList.keySet()) {
      nameToTerritory.put(t.getName(), t);
    }
    this.territoryNum = this.weightedAdjList.size();
    this.resources = new HashMap<>();
    this.maxTechLevel = new HashMap<>();
  }

  public GameMap(Map<Territory, Map<Territory, Double>> distMap, boolean useDist) {
    Map<Territory, Set<Territory>> adjList = new HashMap<>();
    for (Territory t : distMap.keySet()) {
      Set<Territory> neighbors = new HashSet<>();
      for (Territory neighbor : distMap.get(t).keySet()) {
        neighbors.add(neighbor);
      }
      adjList.put(t, neighbors);
    }
    this.weightedAdjList = generateWeightedAdjList(adjList);
    this.nameToTerritory = new HashMap<String, Territory>();
    for (Territory t : weightedAdjList.keySet()) {
      nameToTerritory.put(t.getName(), t);
    }
    this.territoryNum = this.weightedAdjList.size();
    this.resources = new HashMap<>();
    this.maxTechLevel = new HashMap<>();
  }

  @Override
  public Object clone() {
    GameMap gameMap = new GameMap(new HashMap<Territory, Set<Territory>>());
    Map<String, Territory> nameToClonedTerritory = new HashMap<>();
    for (Territory t : this.weightedAdjList.keySet()) {
      nameToClonedTerritory.put(t.getName(), (Territory) t.clone());
    }
    gameMap.nameToTerritory = nameToClonedTerritory;
    gameMap.territoryNum = gameMap.nameToTerritory.size();
    for (Territory t : this.weightedAdjList.keySet()) {
      Map<Territory, Integer> clonedNeighbors = new HashMap<>();
      for (Territory neighbor : this.weightedAdjList.get(t).keySet()) {
        clonedNeighbors.put(nameToClonedTerritory.get(neighbor.getName()), this.weightedAdjList.get(t).get(neighbor));
      }
      gameMap.weightedAdjList.put(nameToClonedTerritory.get(t.getName()), clonedNeighbors);
    }
    for (int playerId : this.resources.keySet()) {
      gameMap.resources.put(playerId, new HashMap<>());
      for (String resourceType : this.resources.get(playerId).keySet()) {
        gameMap.resources.get(playerId).put(resourceType, this.resources.get(playerId).get(resourceType));
      }
    }
    gameMap.maxTechLevel.putAll(this.maxTechLevel);
    return gameMap;
  }

  /**
   * Get corresponding Territory to a name
   * 
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
    return weightedAdjList.keySet();
  }

  public Set<Territory> getTerritorySetByPlayerId(int playerId) {
    Set<Territory> territories = weightedAdjList.keySet();
    Set<Territory> playerTerritories = new HashSet<Territory>();
    for (Territory t : territories) {
      if (t.getOwnerId() == playerId) {
        playerTerritories.add(t);
      }
    }
    return playerTerritories;
  }

  /**
   * Get number of territories in the map
   * 
   * @return territoryNum
   */
  public int getTerritoryNum() {
    return this.territoryNum;
  }

  /**
   * Get the distance to all neighbors of a Territory
   * 
   * @param territory a territory
   * @return the Map that contains the distance to the neighbors
   */
  public Map<Territory, Integer> getNeighborDist(Territory territory) {
    return weightedAdjList.get(territory);
  }
  
  /**
   * 
   * Finds all enemy territories that are directly connected to the given source
   * territory. The result is a set of all enemy territories that are directly
   * connected to the source territory.
   *
   * @param src the source territory for which to find connected enemy territories
   * @return a set of all enemy territories that are directly connected to the
   *         source territory
   */
  public Set<Territory> getEnemyNeighbors(Territory src) {
    Set<Territory> enemyNeighs = new HashSet<>();
    Set<Territory> allNeighs = getNeighborDist(src).keySet();
    for (Territory neigh : allNeighs) {
      if (neigh.getOwnerId() != src.getOwnerId()) {
        enemyNeighs.add(neigh);
      }
    }
    return enemyNeighs;
  }

  /**
   * 
   * Returns a set of self-owned territories that are reachable from the given
   * source territory via a path owned by the same player. This method uses the
   * hasSamePlayerPath() method to check if a path between the source and each
   * potential territory exists and is owned by the same player as the source. If
   * a reachable territory is found, it is added to the result set.
   *
   * @param src the source territory
   * @return a set of self-owned territories reachable from the source territory
   */
  public Set<Territory> getHasPathSelfTerritories(Territory src) {
    Set<Territory> hasPathSelfTerritories = new HashSet<>();
    Set<Territory> selfTerritories = getTerritorySetByPlayerId(src.getOwnerId());
    for (Territory self : selfTerritories) {
      if (hasSamePlayerPath(src, self)) {
        hasPathSelfTerritories.add(self);
      }
    }
    return hasPathSelfTerritories;

  }

  /**
   * Initialize maximum technology level for all players (start for 1)
   */
  public void initMaxTechLevel() {
    Set<Integer> playerIds = new HashSet<>();
    for (Territory t : this.weightedAdjList.keySet()) {
      playerIds.add(t.getOwnerId());
    }
    for (int playerId : playerIds) {
      this.maxTechLevel.put(playerId, 1);
    }
  }

  /**
   * Upgrade the max tech level by 1 for player with playerId
   * 
   * @param playerId
   */
  public void upgradeMaxTechLevel(int playerId) {
    this.maxTechLevel.put(playerId, this.maxTechLevel.get(playerId) + 1);
  }

  /**
   * Get the max tech level for player with playerId
   * 
   * @param playerId
   * @return max tech level
   */
  public int getMaxTechLevel(int playerId) {
    return this.maxTechLevel.get(playerId);
  }

  /**
   * Check if there is a path consists of Territories that owned by the same owner
   * of territoryFrom and territoryTo Apply BFS to search the path
   * 
   * @param territoryFrom
   * @param territoryTo
   * @return true if the path exists
   */
  public boolean hasSamePlayerPath(Territory src, Territory dest) {
    if (src.equals(dest)) {
      return true;
    }
    Set<Territory> visited = new HashSet<>();
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
      for (Territory neighbor : weightedAdjList.get(curr).keySet()) {
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

  /**
   * Convert an adjacent list to a weighted adjacent list The distance between two
   * Territories are either 1 or 2 distance(t1, t2) = distance(t2, t1)
   * 
   * @param adjList
   * @return weightedAdjList
   */
  private Map<Territory, Map<Territory, Integer>> generateWeightedAdjList(Map<Territory, Set<Territory>> adjList) {
    Map<Territory, Map<Territory, Integer>> weightedAdjList = new HashMap<>();
    for (Territory t : adjList.keySet()) {
      weightedAdjList.put(t, new HashMap<>());
    }
    for (Territory t : adjList.keySet()) {
      Set<Territory> neighbors = adjList.get(t);
      for (Territory neighbor : neighbors) {
        if (weightedAdjList.get(t).get(neighbor) != null) {
          continue;
        }
        Random random = new Random();
        int dist = random.nextInt(2) + 1;
        weightedAdjList.get(t).put(neighbor, dist);
        weightedAdjList.get(neighbor).put(t, dist);
      }
    }
    return weightedAdjList;
  }

  /**
   * Calculate the minimum cost of the path between src and dest
   * 
   * @param src
   * @param dest
   * @return minimum cost
   */
  public int findPathWithLowestCost(Territory src, Territory dest) {
    MinCostPathCalculator minCostPathCalculator = new MinCostPathCalculator(weightedAdjList);
    return minCostPathCalculator.calculateMinCostPath(src, dest);
  }

  /**
   * Update players' resource
   * 
   * @param newResources
   */
  public void updateResource(Map<Integer, Map<String, Integer>> newResources) {
    if (this.resources.size() == 0) {
      this.resources = newResources;
      return;
    }
    for (int playerId : resources.keySet()) {
      Map<String, Integer> resource = resources.get(playerId);
      Map<String, Integer> newResource = newResources.get(playerId);
      resource.put(Constants.RESOURCE_FOOD,
          resource.get(Constants.RESOURCE_FOOD) + newResource.get(Constants.RESOURCE_FOOD));
      resource.put(Constants.RESOURCE_TECH,
          resource.get(Constants.RESOURCE_TECH) + newResource.get(Constants.RESOURCE_TECH));
    }
  }

  /**
   * Deduct a specific resource by cost
   * 
   * @param playerId
   * @param category
   * @param cost
   */
  public void consumeResource(int playerId, String category, int cost) {
    this.resources.getOrDefault(playerId, new HashMap<>()).put(category,
        this.resources.getOrDefault(playerId, new HashMap<>()).getOrDefault(category, 0) - cost);
  }

  /**
   * Get resource by playerId
   * 
   * @param playerId
   * @return resource of a player
   */
  public Map<String, Integer> getResourceByPlayerId(int playerId) {
    Map<String, Integer> defaultVal = new HashMap<>();
    defaultVal.put(Constants.RESOURCE_FOOD, 0);
    defaultVal.put(Constants.RESOURCE_TECH, 0);
    return this.resources.getOrDefault(playerId, defaultVal);
  }

  /**
   * Get the visible set of territories to a player
   * 
   * @param playerId
   * @return visible set of territories
   */
  public Set<Territory> getVisibleTerritoriesByPlayerId(int playerId) {
    Set<Territory> visibleTerritories = new HashSet<Territory>();
    /**
     * TODO: implement the visible rules:
     * 
     * 1. Territories are visible if owned by that player 2. Territories are visible
     * if it is a neighbor of territory that owned by that player 3. Territories are
     * visible if it has at least 1 spy owned by that player on it 4. Territories
     * that are cloaked by the owner without spies is invisible 5. Other territories
     * should be invisible.
     * 
     */

    return visibleTerritories;
  }

  /**
   * Get a set of territories that a player has spies on, including self-owned
   * territories as long as their spies are on them
   * 
   * @param playerId the player who requests a set of their spying territories
   */
  public Set<Territory> getSpyingTerritoriesOfAPlayer(int playerId) {
    Set<Territory> spyingTerritories = new HashSet<>();

    // TODO: implement rules:
    // return a set of territories that the player (with playerId) has spies on (1,
    // 2, .. )
    // no need to filter out self-owned territories

    return spyingTerritories;
  } 
}
