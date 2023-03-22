package edu.duke.ece651.team6.shared;

public class EnemyNeighborRuleChecker extends AttackOrderRuleChecker {

  public EnemyNeighborRuleChecker(OrderRuleChecker next) {
    super(next);
  }

  @Override
  protected String checkMyRule(SimpleMove move, GameMap theMap) {
    int srcPlayerId = move.src.getOwnerId();
    int destPlayerId = move.dest.getOwnerId();
    if (srcPlayerId == destPlayerId) {
      return "Invalid attack: src and dest territory both belong to player" + srcPlayerId;
    }

    if (!theMap.getNeighborSet(move.src).contains(move.dest)) {
      return "Invalid: attack: dest " + move.dest.getName() + " is not a neighbor of " + move.src.getName();
    }
    return null;

  }
}
