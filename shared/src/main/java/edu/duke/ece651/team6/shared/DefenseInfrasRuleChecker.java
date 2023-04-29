package edu.duke.ece651.team6.shared;

import java.util.Map;

public class DefenseInfrasRuleChecker extends OrderRuleChecker {
    protected Map<String, Integer> resource;

    public DefenseInfrasRuleChecker(OrderRuleChecker next, Map<String, Integer> resourse) {
        super(next);
        this.resource = resourse;
    }

    @Override
    protected String checkMyRule(Order order, GameMap theMap) {
        int cost = 1;
        int currTech = resource.getOrDefault(Constants.RESOURCE_TECH, 0);
        if (cost > currTech) {
            return "Invalid DefenseInfras: the expected cost of this order is: " + cost + " but you have " + currTech + " tech left";
        }
        resource.put(Constants.RESOURCE_TECH, currTech - cost);
        return null;
    }
}
