package edu.duke.ece651.team6.shared;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MinCostPathCalculator {
    private final Map<Territory, Map<Territory, Integer>> weightedAdjList;
    private Set<Territory> visited;
    private int minCost;
    private LinkedList<Territory> path; // the path with minimum cost from src to dest (for debugging)
    
    /**
     * Construct with weightedAdjList
     * @param weightedAdjList
     */
    public MinCostPathCalculator(Map<Territory, Map<Territory, Integer>> weightedAdjList) {
        this.weightedAdjList = weightedAdjList;
        this.visited = new HashSet<>();
        this.minCost = Integer.MAX_VALUE;
        this.path = new LinkedList<>();
    }

    /**
     * Calculate the minimum cost path from src to dest
     * @param src
     * @param dest
     * @return the minimum cost
     */
    public int calculateMinCostPath(Territory src, Territory dest) {
        if (src.equals(dest)) {
            return 0;
        }
        btFunc(src, dest, 0, new LinkedList<>());
        StringBuilder sb = new StringBuilder();
        for (Territory t : path) {
            sb.append(t.getName() + " ");
        }
        System.out.println("Path: " + sb.toString());
        return minCost;
    }

    /**
     * Helper function using backtracking to search for the path with minimum cost
     * @param src
     * @param dest
     * @param cost
     * @param currPath
     */
    private void btFunc(Territory src, Territory dest, int cost, LinkedList<Territory> currPath) {
        visited.add(src);
        currPath.addLast(src);
        if (src.equals(dest)) {
            if (minCost > cost) {
                minCost = cost;
                path.clear();
                for (Territory t : currPath) {
                    path.add(t);
                }
            }
            visited.remove(src);
            currPath.removeLast();
            return;
        }
        Map<Territory, Integer> distToNeighbors = weightedAdjList.get(src);
        for (Territory neighbor : distToNeighbors.keySet()) {
            if (neighbor.getOwnerId() != src.getOwnerId() || visited.contains(neighbor)) {
                continue;
            }
            btFunc(neighbor, dest, cost + distToNeighbors.get(neighbor), currPath);
        }
        visited.remove(src);
        currPath.removeLast();
    }

    /**
     * Calculate the minimum cost path from src to dest that does not care about the Territories' owner
     * @param src
     * @param dest
     * @return the minimum cost
     */
    public int calculateUltimateMinCostPath(Territory src, Territory dest) {
        if (src.equals(dest)) {
            return 0;
        }
        ultimateBtFunc(src, dest, 0, new LinkedList<>());
        StringBuilder sb = new StringBuilder();
        for (Territory t : path) {
            sb.append(t.getName() + " ");
        }
        System.out.println("Path: " + sb.toString());
        return minCost;
    }

    /**
     * Helper function using backtracking to search for the path with minimum cost that does not care about the Territories' owner
     * @param src
     * @param dest
     * @param cost
     * @param currPath
     */
    private void ultimateBtFunc(Territory src, Territory dest, int cost, LinkedList<Territory> currPath) {
        visited.add(src);
        currPath.addLast(src);
        if (src.equals(dest)) {
            if (minCost > cost) {
                minCost = cost;
                path.clear();
                for (Territory t : currPath) {
                    path.add(t);
                }
            }
            visited.remove(src);
            currPath.removeLast();
            return;
        }
        Map<Territory, Integer> distToNeighbors = weightedAdjList.get(src);
        for (Territory neighbor : distToNeighbors.keySet()) {
            if (visited.contains(neighbor)) {
                continue;
            }
            btFunc(neighbor, dest, cost + distToNeighbors.get(neighbor), currPath);
        }
        visited.remove(src);
        currPath.removeLast();
    }

}
