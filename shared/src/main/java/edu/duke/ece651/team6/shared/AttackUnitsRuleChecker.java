package edu.duke.ece651.team6.shared;

public class AttackUnitsRuleChecker extends AttackOrderRuleChecker {
  public AttackUnitsRuleChecker(AttackOrderRuleChecker checker) {
    super(checker);
  }

  @Override
  protected String checkMyRule(SimpleMove move, int remainingUnits, GameMap theMap) {
    if (move.numUnits <= 0 || move.numUnits > remainingUnits) {
      return "Invalid number of units: " + move.numUnits;
    }
    return null;
  }

}
