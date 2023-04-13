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
   * @param order          is an order object

   * @param remainingUnits is the number of remaining units allowed to perform
   *                       actions
   * @param theMap         is the game map that has adjacency information
   * @return a string telling if it obeys my own rule, null if so, a message
   *         string if otherwise.
   */
  @Override
  protected String checkMyRule(Order order, GameMap theMap) {
    SimpleMove move = (SimpleMove) order;
    Territory src = theMap.getTerritoryByName(move.src.getName());
    Territory dest = theMap.getTerritoryByName(move.dest.getName());
    try {
      src.attack(dest, move.numUnitsByLevel);
    } catch (IllegalArgumentException e) {
      return e.getMessage();
    }
    return null;
  }
}
