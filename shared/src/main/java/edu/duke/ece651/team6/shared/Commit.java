package edu.duke.ece651.team6.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class Commit {
  int playerId;
  List<MoveOrder> moves;
  List<AttackOrder> attacks;
  ListIterator<MoveOrder> moveIterator;
  ListIterator<AttackOrder> attackIterator;

  MoveOrderRuleChecker moveChecker;
  AttackOrderRuleChecker attackChecker;
  HashMap<Territory, Integer> remainingUnits;

  public Commit(int playerId, MoveOrderRuleChecker moveChecker, AttackOrderRuleChecker attackChecker,
      HashMap<Territory, Integer> remainingUnits) {
    this.playerId = playerId;
    this.moves = new ArrayList<MoveOrder>();
    this.attacks = new ArrayList<>();
    this.moveChecker = moveChecker;
    this.attackChecker = attackChecker;
    this.remainingUnits = remainingUnits;

    this.moveIterator = moves.listIterator();
    this.attackIterator = attacks.listIterator();
  }

  /* For client side usage */
  private void checkRules(OrderRuleChecker checker, SimpleMove simpleMove, GameMap gameMap) {
    String result = checker.checkMyRule(simpleMove, remainingUnits.get(simpleMove.src), gameMap);
    if (result != null) {
      throw new IllegalArgumentException(result);
    }
    remainingUnits.put(simpleMove.src, remainingUnits.get(simpleMove.src) - simpleMove.numUnits);
  }

  public void addMove(MoveOrder move, GameMap gameMap) {
    checkRules(moveChecker, move, gameMap);
    moves.add(move);
    moveIterator = moves.listIterator();
  }

  public void addAttack(AttackOrder attack, GameMap gameMap) {
    checkRules(attackChecker, attack, gameMap);
    attacks.add(attack);
    attackIterator = attacks.listIterator();
  }

  /*
   * private void performAMove() {
   * if (moveIterator.hasNext()) {
   * moveIterator.next().takeAction();
   * }
   * }
   */

  /* For server side usage */
  public void performMoves(GameMap gameMap) {
    while (moveIterator.hasNext()) {
      moveIterator.next().takeAction(gameMap);
    }
  }

  public void performAttacks(GameMap gameMap) {
    while (attackIterator.hasNext()) {
      attackIterator.next().takeAction(gameMap);
    }
  }

  /*
   * public void performAll() {
   * moves.forEach((move) -> move.takeAction());
   * attacks.forEach((attack) -> attack.takeAction());
   * }
   */
}
