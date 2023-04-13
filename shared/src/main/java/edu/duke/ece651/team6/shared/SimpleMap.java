package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleMap {
    private Map<Territory, Set<Territory>> adjList;

    public SimpleMap() {
        adjList = new HashMap<>();
        Territory Narnia = new Territory("Narnia", 0, 2);
        Territory Midkemia = new Territory("Midkemia", 1, 2);

        Set<Territory> NarniaNeighbor = new HashSet<>();
        NarniaNeighbor.add(Midkemia);
        adjList.put(Narnia, NarniaNeighbor);

        Set<Territory> MidkemiaNeighbor = new HashSet<>();
        MidkemiaNeighbor.add(Narnia);
        adjList.put(Midkemia, MidkemiaNeighbor);
    }

    public Map<Territory, Set<Territory>> getAdjList() {
        return adjList;
    }
}