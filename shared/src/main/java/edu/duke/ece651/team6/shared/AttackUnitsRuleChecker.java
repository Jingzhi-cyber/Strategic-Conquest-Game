package edu.duke.ece651.team6.shared;

/**
 * This rule checker checks rules of units for attack orders
 */
public class AttackUnitsRuleChecker extends AttackOrderRuleChecker {
  /**
   * Construct an AttackUnitsRuleChecker object with 1 param
   * 
   * @param checker is the next rule in the rule chain
   */
  public AttackUnitsRuleChecker(OrderRuleChecker checker) {
    super(checker);
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
  protected String checkMyRule(SimpleMove move, GameMap theMap) {
    if (move.numUnits <= 0) { // || move.numUnits > move.src.getNumUnits() could be valid if some units are
                              // moved here
      return "Units for attack order must be postive, but was " + move.numUnits;
    }
    return null;
  }
}
