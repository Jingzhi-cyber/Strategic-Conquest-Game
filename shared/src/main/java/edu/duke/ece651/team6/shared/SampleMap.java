package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SampleMap {
    private Map<Territory, Set<Territory>> adjList;

    public SampleMap() {
        adjList = new HashMap<Territory, Set<Territory>>();
        Territory Narnia = new Territory("Narnia", 0, 0);
        Territory Midkemia = new Territory("Midkemia", 0, 0);
        Territory Oz = new Territory("Oz", 0, 0);
        Territory Elantris = new Territory("Elantris", 1, 0);
        Territory Scadrial = new Territory("Scadrial", 1, 0);
        Territory Roshar = new Territory("Roshar", 1, 0);
        Territory Gondor = new Territory("Gondor", 2, 0);
        Territory Mordor = new Territory("Mordor", 2, 0);
        Territory Hogwarts = new Territory("Hogwarts", 2, 0);

        Set<Territory> NarniaNeighbor = new HashSet<Territory>();
        NarniaNeighbor.add(Midkemia);
        NarniaNeighbor.add(Elantris);
        adjList.put(Narnia, NarniaNeighbor);

        Set<Territory> MidkemiaNeighbor = new HashSet<Territory>();
        MidkemiaNeighbor.add(Narnia);
        MidkemiaNeighbor.add(Elantris);
        MidkemiaNeighbor.add(Scadrial);
        MidkemiaNeighbor.add(Oz);
        adjList.put(Midkemia, MidkemiaNeighbor);

        Set<Territory> OzNeighbor = new HashSet<Territory>();
        OzNeighbor.add(Midkemia);
        OzNeighbor.add(Scadrial);
        OzNeighbor.add(Mordor);
        OzNeighbor.add(Gondor);
        adjList.put(Oz, OzNeighbor);

        Set<Territory> ElantrisNeighbor = new HashSet<Territory>();
        ElantrisNeighbor.add(Narnia);
        ElantrisNeighbor.add(Midkemia);
        ElantrisNeighbor.add(Scadrial);
        ElantrisNeighbor.add(Roshar);
        adjList.put(Elantris, ElantrisNeighbor);

        Set<Territory> ScadrialNeighbor = new HashSet<Territory>();
        ScadrialNeighbor.add(Elantris);
        ScadrialNeighbor.add(Midkemia);
        ScadrialNeighbor.add(Oz);
        ScadrialNeighbor.add(Mordor);
        ScadrialNeighbor.add(Hogwarts);
        ScadrialNeighbor.add(Roshar);
        adjList.put(Scadrial, ScadrialNeighbor);

        Set<Territory> RosharNeighbor = new HashSet<Territory>();
        RosharNeighbor.add(Elantris);
        RosharNeighbor.add(Scadrial);
        RosharNeighbor.add(Hogwarts);
        adjList.put(Roshar, RosharNeighbor);

        Set<Territory> GondorNeighbor = new HashSet<Territory>();
        GondorNeighbor.add(Oz);
        GondorNeighbor.add(Mordor);
        adjList.put(Gondor, GondorNeighbor);

        Set<Territory> MordorNeighbor = new HashSet<Territory>();
        MordorNeighbor.add(Gondor);
        MordorNeighbor.add(Oz);
        MordorNeighbor.add(Scadrial);
        MordorNeighbor.add(Hogwarts);
        adjList.put(Mordor, MordorNeighbor);

        Set<Territory> HogwartsNeighbor = new HashSet<Territory>();
        HogwartsNeighbor.add(Mordor);
        HogwartsNeighbor.add(Scadrial);
        HogwartsNeighbor.add(Roshar);
        adjList.put(Hogwarts, HogwartsNeighbor);
    }

    public Map<Territory, Set<Territory>> getAdjList() {
        return adjList;
    }
}
