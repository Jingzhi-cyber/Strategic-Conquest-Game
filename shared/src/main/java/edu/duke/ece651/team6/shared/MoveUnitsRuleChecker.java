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
  protected String checkMyRule(Order order, GameMap theMap) {
    SimpleMove move = (SimpleMove) order;
    Territory src = move.src;
    Territory dest = move.dest;
    try {
      src.moveTo(dest, move.numUnitsByLevel);
    } catch (IllegalArgumentException e) {
      return e.getMessage();
    }
    return null;
  }
}
