package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.HashSet;

public class SampleMap {
    private HashMap<Territory, HashSet<Territory>> adjList;

    public SampleMap() {
        adjList = new HashMap<Territory, HashSet<Territory>>();
        Territory Narnia = new Territory("Narnia", 0, 0);
        Territory Midkemia = new Territory("Midkemia", 0, 0);
        Territory Oz = new Territory("Oz", 0, 0);
        Territory Elantris = new Territory("Elantris", 1, 0);
        Territory Scadrial = new Territory("Scadrial", 1, 0);
        Territory Roshar = new Territory("Roshar", 1, 0);
        Territory Gondor = new Territory("Gondor", 2, 0);
        Territory Mordor = new Territory("Mordor", 2, 0);
        Territory Hogwarts = new Territory("Hogwarts", 2, 0);

        HashSet<Territory> NarniaNeighbor = new HashSet<Territory>();
        NarniaNeighbor.add(Midkemia);
        NarniaNeighbor.add(Elantris);
        adjList.put(Narnia, NarniaNeighbor);

        HashSet<Territory> MidkemiaNeighbor = new HashSet<Territory>();
        MidkemiaNeighbor.add(Narnia);
        MidkemiaNeighbor.add(Elantris);
        MidkemiaNeighbor.add(Scadrial);
        MidkemiaNeighbor.add(Oz);
        adjList.put(Midkemia, MidkemiaNeighbor);

        HashSet<Territory> OzNeighbor = new HashSet<Territory>();
        OzNeighbor.add(Midkemia);
        OzNeighbor.add(Scadrial);
        OzNeighbor.add(Mordor);
        OzNeighbor.add(Gondor);
        adjList.put(Oz, OzNeighbor);

        HashSet<Territory> ElantrisNeighbor = new HashSet<Territory>();
        ElantrisNeighbor.add(Narnia);
        ElantrisNeighbor.add(Midkemia);
        ElantrisNeighbor.add(Scadrial);
        ElantrisNeighbor.add(Roshar);
        adjList.put(Elantris, ElantrisNeighbor);

        HashSet<Territory> ScadrialNeighbor = new HashSet<Territory>();
        ScadrialNeighbor.add(Elantris);
        ScadrialNeighbor.add(Midkemia);
        ScadrialNeighbor.add(Oz);
        ScadrialNeighbor.add(Mordor);
        ScadrialNeighbor.add(Hogwarts);
        ScadrialNeighbor.add(Roshar);
        adjList.put(Scadrial, ScadrialNeighbor);

        HashSet<Territory> RosharNeighbor = new HashSet<Territory>();
        RosharNeighbor.add(Elantris);
        RosharNeighbor.add(Scadrial);
        RosharNeighbor.add(Hogwarts);
        adjList.put(Roshar, RosharNeighbor);

        HashSet<Territory> GondorNeighbor = new HashSet<Territory>();
        GondorNeighbor.add(Oz);
        GondorNeighbor.add(Mordor);
        adjList.put(Gondor, GondorNeighbor);

        HashSet<Territory> MordorNeighbor = new HashSet<Territory>();
        MordorNeighbor.add(Gondor);
        MordorNeighbor.add(Oz);
        MordorNeighbor.add(Scadrial);
        MordorNeighbor.add(Hogwarts);
        adjList.put(Mordor, MordorNeighbor);

        HashSet<Territory> HogwartsNeighbor = new HashSet<Territory>();
        HogwartsNeighbor.add(Mordor);
        HogwartsNeighbor.add(Scadrial);
        HogwartsNeighbor.add(Roshar);
        adjList.put(Hogwarts, HogwartsNeighbor);
    }

    public HashMap<Territory, HashSet<Territory>> getAdjList() {
        return adjList;
    }
}
