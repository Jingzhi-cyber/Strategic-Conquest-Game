package edu.duke.ece651.team6.shared;

/**
 * An abstract rule checker class to check validity of orders
 */
public abstract class OrderRuleChecker {
  private final OrderRuleChecker next;

  /**
   * Construct an OrderRuleChecker object with 1 param
   * 
   * @param next is the next rule in the rule chain
   */
  public OrderRuleChecker(OrderRuleChecker next) {
    this.next = next;
  }

  /**
   * This method checks placement rules as a whole
   * 
   * @param theShip  is the current ship
   * @param theBoard is the current board
   * @return a string telling if it obeys my own rule, null if all of the rules on
   *         the chain pass, or a message string if any of the rules failed
   */
  public String checkOrder(SimpleMove move, int remainingUnits, GameMap theMap) {
    // if we fail our own rule: stop the placement is not legal
    String myRule = checkMyRule(move, remainingUnits, theMap);
    if (myRule != null)
      return myRule;

    // other wise, ask the rest of the chain.
    if (next != null) {
      return next.checkOrder(move, remainingUnits, theMap);
    }

    // if there are no more rules, then the placement is legal
    return null;
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
  protected abstract String checkMyRule(SimpleMove move, int remainingUnits, GameMap theMap);
}
