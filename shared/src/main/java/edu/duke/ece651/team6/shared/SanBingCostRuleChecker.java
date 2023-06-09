package edu.duke.ece651.team6.shared;

import java.util.Map;

public class SanBingCostRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public SanBingCostRuleChecker(OrderRuleChecker next, Map<String, Integer> resourse) {
        super(next);
        this.resource = resourse;
    }

    @Override
    protected String checkMyRule(Order move, GameMap theMap) {
        int cost = 1;
        int currFood = resource.getOrDefault(Constants.RESOURCE_FOOD, 0);
        if (cost > currFood) {
            return "Invalid move: the expected cost of San Bing is: " + cost + " but you have " + currFood + " food left";
        }
        resource.put(Constants.RESOURCE_FOOD, currFood - cost);
        return null;
    }
}
