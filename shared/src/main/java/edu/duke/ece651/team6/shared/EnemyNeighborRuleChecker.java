package edu.duke.ece651.team6.shared;

public class EnemyNeighborRuleChecker extends AttackOrderRuleChecker {

  public EnemyNeighborRuleChecker(OrderRuleChecker next) {
    super(next);
  }

  @Override
  protected String checkMyRule(Order order, GameMap theMap) {
    SimpleMove move = (SimpleMove) order;
    int srcPlayerId = move.src.getOwnerId();
    int destPlayerId = move.dest.getOwnerId();
    if (srcPlayerId == destPlayerId) {
      return "Invalid attack: src and dest territory both belong to player" + srcPlayerId;
    }

    if (!theMap.getNeighborDist(move.src).containsKey(move.dest)) {
      return "Invalid attack: dest " + move.dest.getName() + " is not a neighbor of " + move.src.getName();
    }
    return null;

  }
}
