package edu.duke.ece651.team6.shared;

import java.util.Map;

public class AttackCostRuleChecker extends AttackOrderRuleChecker {
    protected Map<String, Integer> resource;

    public AttackCostRuleChecker(OrderRuleChecker next, Map<String, Integer> resourse) {
        super(next);
        this.resource = resourse;
    }

    @Override
    protected String checkMyRule(Order move, GameMap theMap) {
        int cost = CostCalculator.calculateAttackCost((SimpleMove) move, theMap);
        int currFood = resource.getOrDefault(Constants.RESOURCE_FOOD, 0);
        if (cost > currFood) {
            return "Invalid move: the expected cost of this move is: " + cost + " but you have " + currFood + " food left";
        }
        resource.put("food", currFood - cost);
        return null;
    }
}
