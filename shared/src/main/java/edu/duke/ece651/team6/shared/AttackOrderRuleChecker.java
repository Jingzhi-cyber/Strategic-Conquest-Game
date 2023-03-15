package edu.duke.ece651.team6.shared;

public abstract class AttackOrderRuleChecker extends OrderRuleChecker {

  public AttackOrderRuleChecker(OrderRuleChecker next) {
    super(next);
  }

  @Override
  abstract protected String checkMyRule(SimpleMove move, int remainingUnits, GameMap theMap);

}
