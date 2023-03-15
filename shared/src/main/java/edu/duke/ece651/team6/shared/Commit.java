package edu.duke.ece651.team6.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/* A structure contains all orders from one player, which will be sent back to server for */
public class Commit implements java.io.Serializable {
  int playerId;
  List<MoveOrder> moves;
  List<AttackOrder> attacks;
  transient ListIterator<MoveOrder> moveIterator;
  transient ListIterator<AttackOrder> attackIterator;

  transient MoveOrderRuleChecker moveChecker;
  transient AttackOrderRuleChecker attackChecker;
  HashMap<Territory, Integer> remainingUnits;

  /**
   * Construct a Commit object with 4 params
   * 
   * @param playerId      is the sender's id
   * @param moveChecker   represents a chain of rules for move order
   * @param attackChecker represents a chain of rules for attack order
   */
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

  /* -------------- For client side usage --------------- */

  /**
   * Check rules for different types of orders
   * 
   * @param checker
   * @param simpleMove is an order
   * @param gameMap
   * @param is         the received map before performing any orders
   * @throws IllegalArgumentException when violating any rules
   */
  private void checkRules(OrderRuleChecker checker, SimpleMove simpleMove, GameMap gameMap) {
    String result = checker.checkMyRule(simpleMove, remainingUnits.get(simpleMove.src), gameMap);
    if (result != null) {
      throw new IllegalArgumentException(result);
    }
    remainingUnits.put(simpleMove.src, remainingUnits.get(simpleMove.src) - simpleMove.numUnits);
  }

  /**
   * Add a Move order into the commit
   * 
   * @param move
   * @param gameMap is for rule checker usage
   * @propogates IllegalArgumentException if rules are violated
   */
  public void addMove(MoveOrder move, GameMap gameMap) {
    checkRules(moveChecker, move, gameMap);
    moves.add(move);
    moveIterator = moves.listIterator();
  }

  /**
   * Add an Attack order into the commit
   * 
   * @param attack
   * @param gameMap is for rule checker usage
   * @propogates IllegalArgumentException if rules are violated
   */
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

  /* ---------------- For server side usage ----------------- */

  /**
   * Perform move orders
   * 
   * @param gameMap is the map where orders are performed on
   */
  public void performMoves(GameMap gameMap) {
    for (MoveOrder move : moves) {
      move.takeAction(gameMap);
    }
  }

  /**
   * Perform attack orders
   * 
   * @param gameMap is the map where orders are performed on
   */
  public void performAttacks(GameMap gameMap) {
    for (AttackOrder attack : attacks) {
      attack.takeAction(gameMap);
    }
  }

  /*
   * public void performAll() {
   * moves.forEach((move) -> move.takeAction());
   * attacks.forEach((attack) -> attack.takeAction());
   * }
   */
}
