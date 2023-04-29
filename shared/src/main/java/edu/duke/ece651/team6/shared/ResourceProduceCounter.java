package edu.duke.ece651.team6.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourceProduceCounter {
    private final Set<Territory> territories;
    private Map<Integer, Map<String, Integer>> resources;

    /**
     * Construct with the Set of territories and the Set of playerIds
     * @param territories
     * @param playerIds
     */
    public ResourceProduceCounter(Set<Territory> territories, Set<Integer> playerIds) {
        this.territories = territories;
        resources = new HashMap<>();
        for (int playerId : playerIds) {
            Map<String, Integer> resource = new HashMap<>();
            resource.put(Constants.RESOURCE_FOOD, 0);
            resource.put(Constants.RESOURCE_TECH, 0);
            resource.put(Constants.RESOURCE_CIVIL, 0);
            resources.put(playerId, resource);
        }
    }

    /**
     * Calculate the resources produced by territories of each player
     * @return resources:
     * playerId -> food: total amount of food
     *          -> technology: total amount of technology 
     *          -> civilization: total amount of civilization
     */
    public Map<Integer, Map<String, Integer>> updateAndGetResult() {
        for (Territory t : territories) {
            Map<String, Integer> resource = resources.getOrDefault(t.getOwnerId(), new HashMap<String, Integer>());
            resource.put(Constants.RESOURCE_FOOD, resource.getOrDefault(Constants.RESOURCE_FOOD, 0) + t.getFood());
            resource.put(Constants.RESOURCE_TECH, resource.getOrDefault(Constants.RESOURCE_TECH, 0) + t.getTechnology());
            resource.put(Constants.RESOURCE_CIVIL, resource.getOrDefault(Constants.RESOURCE_CIVIL, 0) + t.getCivilization());
        }
        return resources;
    } 
}
