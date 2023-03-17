package edu.duke.ece651.team6.shared;

/**
 * This rule checker checks rules of same_player_path for move orders
 */
public class SamePlayerPathRuleChecker extends MoveOrderRuleChecker {
  /**
   * A constructor that takes a OrderRuleChecker and passes it to the
   * super class's constructor
   * 
   * @param next the next rule of the rule chain
   */
  public SamePlayerPathRuleChecker(MoveOrderRuleChecker checker) {
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
    if (!theMap.hasSamePlayerPath(move.src, move.dest)) {
      return "Invalid move action: there isn't a connected path through src to dest where all territories belong to the same player.";
    }
    return null;
  }

}
