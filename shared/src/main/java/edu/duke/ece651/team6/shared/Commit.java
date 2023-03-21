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
  // HashMap<String, Integer> remainingUnits;

  /**
   * Construct a Commit object with 4 params
   * 
   * @param playerId      is the sender's id
   * @param moveChecker   represents a chain of rules for move order
   * @param attackChecker represents a chain of rules for attack order
   */
  public Commit(int playerId, MoveOrderRuleChecker moveChecker, AttackOrderRuleChecker attackChecker) {
    this.playerId = playerId;
    this.moves = new ArrayList<MoveOrder>();
    this.attacks = new ArrayList<>();
    this.moveChecker = moveChecker;
    this.attackChecker = attackChecker;
    // this.remainingUnits = remainingUnits;

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
    String result = checker.checkMyRule(simpleMove, gameMap);
    if (result != null) {
      throw new IllegalArgumentException(result);
    }
    // remainingUnits.put(simpleMove.src.getName(),
    // remainingUnits.get(simpleMove.src.getName()) - simpleMove.numUnits);
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

  private String constructPrompt(String orderName, SimpleMove move, int remainingUnits) {
    return orderName + move.toString() + " cannot be performed, with " + remainingUnits + " remaining units";
  }

  public void checkUsableUnitsBeforeSendingToServer() {
    // only check remaining units (not check > 0 here, check it with rule checker)
    HashMap<Territory, Integer> remainingUnits = new HashMap<Territory, Integer>();
    for (MoveOrder move : moves) {
      if (!remainingUnits.containsKey(move.src)) {
        remainingUnits.put(move.src, move.src.getNumUnits());
      }

      if (remainingUnits.get(move.src) < move.numUnits) {
        throw new IllegalArgumentException(constructPrompt("Move", move, remainingUnits.get(move.src)));
      }
      remainingUnits.put(move.src, remainingUnits.get(move.src) - move.numUnits);
      remainingUnits.put(move.dest, remainingUnits.getOrDefault(move.dest, move.dest.getNumUnits()) + move.numUnits);
    }

    for (AttackOrder attack : attacks) {
      if (!remainingUnits.containsKey(attack.src)) {
        remainingUnits.put(attack.src, attack.src.getNumUnits());
      }

      if (remainingUnits.get(attack.src) < attack.numUnits) {
        throw new IllegalArgumentException(constructPrompt("Attack", attack, remainingUnits.get(attack.src)));
      }
      remainingUnits.put(attack.src, remainingUnits.get(attack.src) - attack.numUnits);
      remainingUnits.put(attack.dest,
          remainingUnits.getOrDefault(attack.dest, attack.dest.getNumUnits()) + attack.numUnits);
    }
  }

  /* ---------------- For server side usage ----------------- */

  /**
   * Perform all checkings at one time.
   * - Do not perform any actual action
   * - Used by the server to ensure validity of commit in case of any possible
   * change before data arrives at the server end
   * 
   * @param gameMap is the map where orders are performed on
   */
  public void checkAll(GameMap gameMap) {
    for (MoveOrder move : moves) {
      checkRules(moveChecker, move, gameMap);
    }

    for (AttackOrder attack : attacks) {
      checkRules(attackChecker, attack, gameMap);
    }

    checkUsableUnitsBeforeSendingToServer();
  }

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

}
