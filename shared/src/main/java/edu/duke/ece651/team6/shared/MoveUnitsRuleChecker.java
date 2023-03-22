package edu.duke.ece651.team6.shared;

/**
 * This rule checker checks rules of units for move orders
 */
public class MoveUnitsRuleChecker extends MoveOrderRuleChecker {

  /**
   * A constructor that takes a OrderRuleChecker and passes it to the
   * super class's constructor
   * 
   * @param next the next rule of the rule chain
   */
  public MoveUnitsRuleChecker(OrderRuleChecker checker) {
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
    if (move.numUnits < 0) { // || move.numUnits > move.src.getNumUnits() it could be valid if more units are
                             // moved here
      return "Units for move order must be non-negative but was " + move.numUnits;
    }
    return null;
  }
}
