package edu.duke.ece651.team6.shared;

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

  @Override
  protected String checkMyRule(SimpleMove move, int remainingUnits, GameMap theMap) {
    if (!theMap.hasSamePlayerPath(move.src, move.dest)) {
      return "Invalid move action: there isn't a connected path through src to dest where all territories belong to the same player.";
    }
    return null;
  }

}
