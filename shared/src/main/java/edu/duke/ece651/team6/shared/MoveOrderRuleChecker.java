package edu.duke.ece651.team6.shared;

/**
 * This class is for move orders rule checking
 */
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

  /**
   * This method checks current rule.
   *
   * @param order          is an order object
   * @param remainingUnits is the number of remaining units allowed to perform
   *                       actions
   * @param theMap         is the game map that has adjacency information
   * @return a string telling if it obeys my own rule, null if so, a message
   *         string if otherwise.
   */
  @Override
  abstract protected String checkMyRule(Order order, GameMap theMap);
}
