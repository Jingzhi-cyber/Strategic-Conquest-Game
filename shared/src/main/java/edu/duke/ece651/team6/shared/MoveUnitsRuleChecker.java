package edu.duke.ece651.team6.shared;

public class MoveUnitsRuleChecker extends MoveOrderRuleChecker {

  /**
   * A constructor that takes a OrderRuleChecker and passes it to the
   * super class's constructor
   * 
   * @param next the next rule of the rule chain
   */
  public MoveUnitsRuleChecker(MoveOrderRuleChecker checker) {
    super(checker);
  }

  @Override
  protected String checkMyRule(SimpleMove move, int remainingUnits, GameMap theMap) {
    if (move.numUnits < 0 || move.numUnits > remainingUnits) {
      return "Invalid unit number.";
    }
    return null;
  }
}
