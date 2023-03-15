package edu.duke.ece651.team6.shared;

/**
 * This class is for attack orders rule checking
 */
public abstract class AttackOrderRuleChecker extends OrderRuleChecker {

  /**
   * A constructor that takes a OrderRuleChecker and passes it to the
   * super class's constructor
   * 
   * @param next the next rule of the rule chain
   */
  public AttackOrderRuleChecker(OrderRuleChecker next) {
    super(next);
  }

  /**
   * This method checks current rule.
   *
   * @param move           is a simple move object that can be moveOrder or
   *                       attackOrder
   * @param remainingUnits is the number of remaining units allowed to perform
   *                       actions
   * @param theMap         is the game map that has adjacency information
   * @return a string telling if it obeys my own rule, null if so, a message
   *         string if otherwise.
   */
  @Override
  abstract protected String checkMyRule(SimpleMove move, int remainingUnits, GameMap theMap);

}
