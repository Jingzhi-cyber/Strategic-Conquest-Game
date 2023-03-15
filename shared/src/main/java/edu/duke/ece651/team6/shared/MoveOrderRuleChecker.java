package edu.duke.ece651.team6.shared;

public abstract class MoveOrderRuleChecker extends OrderRuleChecker {

  /**
   * A constructor that takes a OrderRuleChecker and passes it to the
   * super class's constructor
   * 
   * @param next the next rule of the rule chain
   */
  public MoveOrderRuleChecker(OrderRuleChecker checker) {
    super(checker);
  }

  @Override
  abstract protected String checkMyRule(SimpleMove move, int remainingUnits, GameMap theMap);
}
