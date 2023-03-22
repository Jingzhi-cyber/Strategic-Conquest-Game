package edu.duke.ece651.team6.shared;

public class SrcOwerIdRuleChecker extends OrderRuleChecker {

  protected final int playerId;

  public SrcOwerIdRuleChecker(OrderRuleChecker next, int playerId) {
    super(next);
    this.playerId = playerId;
  }

  @Override
  protected String checkMyRule(SimpleMove move, GameMap theMap) {
    if (move.src.getOwnerId() != this.playerId) {
      return "Invalid order: you are player " + this.playerId
          + ", and you cannot perform the action on player " + move.src.getOwnerId() + "'s territory";
    }
    return null;
  }

}
