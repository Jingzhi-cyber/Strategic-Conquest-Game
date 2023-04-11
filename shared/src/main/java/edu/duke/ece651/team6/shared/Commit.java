package edu.duke.ece651.team6.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.StringBinding;

/* A structure contains all orders from one player, which will be sent back to server for */
public class Commit implements java.io.Serializable {
  int playerId;
  GameMap gameMap;
  Map<String, Integer> resource;
  GameMap gameMapBak;
  Map<String, Integer> resourceBak;
  List<MoveOrder> moves;
  List<AttackOrder> attacks;
  ResearchOrder research;
  List<UpgradeOrder> upgrades;

  OrderRuleChecker moveChecker;
  OrderRuleChecker attackChecker;
  OrderRuleChecker researchChecker;
  OrderRuleChecker upgradeChecker;

  /**
   * Construct a Commit object with 4 params
   * 
   * @param playerId      is the sender's id
   * @param moveChecker   represents a chain of rules for move order
   * @param attackChecker represents a chain of rules for attack order
   */
  public Commit(int playerId, GameMap gameMap, Map<String, Integer> resource) {
    this.playerId = playerId;
    this.gameMap = gameMap;
    this.resource = resource;
    this.gameMapBak = (GameMap) this.gameMap.clone();
    this.resourceBak = new HashMap<>();
    this.resourceBak.putAll(this.resource);
    this.moves = new ArrayList<>();
    this.attacks = new ArrayList<>();
    this.research = null;
    this.upgrades = new ArrayList<>();
    this.moveChecker = new SrcOwerIdRuleChecker(
        new SamePlayerPathRuleChecker(new MoveCostRuleChecker(new MoveUnitsRuleChecker(null), resource)), playerId);
    this.attackChecker = new SrcOwerIdRuleChecker(
        new EnemyNeighborRuleChecker(new AttackCostRuleChecker(new AttackUnitsRuleChecker(null), resource)), playerId);
    this.researchChecker = new ResearchCostRuleChecker(null, resource);
    this.upgradeChecker = new UpgradeLevelRuleChecker(
        new UpgradeUnitRuleChecker(new UpgradeCostRuleChecker(null, resource)));
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
  private void checkRules(OrderRuleChecker checker, Order order, GameMap gameMap) {
    String result = checker.checkOrder(order, gameMap);
    if (result != null) {
      this.gameMap = (GameMap) this.gameMapBak.clone();
      this.resource.clear();
      this.resource.putAll(this.resourceBak);
      throw new IllegalArgumentException(result);
    }
    this.gameMapBak = (GameMap) this.gameMap.clone();
    this.resourceBak.clear();
    this.resourceBak.putAll(this.resource);
  }

  /**
   * Add a Move order into the commit
   * 
   * @param move
   * @param gameMap is for rule checker usage
   * @propogates IllegalArgumentException if rules are violated
   */
  public void addMove(MoveOrder move) {
    checkRules(moveChecker, move, this.gameMap);
    moves.add(move);
  }

  /**
   * Add an Attack order into the commit
   * 
   * @param attack
   * @propogates IllegalArgumentException if rules are violated
   */
  public void addAttack(AttackOrder attack) {
    checkRules(attackChecker, attack, this.gameMap);
    attacks.add(attack);
  }

  /**
   * Add a Research order into the commit
   * 
   * @param research
   * @propogates IllegalArgumentException if rules are violated
   */
  public void addResearch(ResearchOrder research) {
    if (this.research != null) {
      throw new IllegalArgumentException("Invalid research: can only have one research order in one turn");
    }
    checkRules(researchChecker, research, this.gameMap);
    this.research = research;
  }

  /**
   * Add an Upgrade order into the commit
   * 
   * @param upgrade
   * @propogates IllegalArgumentException if rules are violated
   */
  public void addUpgrade(UpgradeOrder upgrade) {
    checkRules(upgradeChecker, upgrade, this.gameMap);
    upgrades.add(upgrade);
  }

  // private String constructPrompt(String orderName, SimpleMove move, int
  // remainingUnits) {
  // return orderName + move.toString() + " cannot be performed, with " +
  // remainingUnits + " remaining units";
  // }

  /*
   * Deprecated check method Now all the rule checks are based on the cloned
   * GameMap, the results of moves and attacks will be reflected immediately after
   * it is added
   */
  // public void checkUsableUnitsAfterAllOrdersAreCollected() {
  // // only check remaining units (not check > 0 here, check it with rule
  // checker)
  // Map<Territory, Integer> remainingUnits = new HashMap<Territory, Integer>();
  // for (MoveOrder move : moves) {
  // if (!remainingUnits.containsKey(move.src)) {
  // remainingUnits.put(move.src, move.src.getNumUnits());
  // }

  // if (remainingUnits.get(move.src) < move.numUnits) {
  // throw new IllegalArgumentException(constructPrompt("Move", move,
  // remainingUnits.get(move.src)));
  // }
  // remainingUnits.put(move.src, remainingUnits.get(move.src) - move.numUnits);
  // remainingUnits.put(move.dest, remainingUnits.getOrDefault(move.dest,
  // move.dest.getNumUnits()) + move.numUnits);
  // }

  // for (AttackOrder attack : attacks) {
  // if (!remainingUnits.containsKey(attack.src)) {
  // remainingUnits.put(attack.src, attack.src.getNumUnits());
  // }

  // if (remainingUnits.get(attack.src) < attack.numUnits) {
  // throw new IllegalArgumentException(constructPrompt("Attack", attack,
  // remainingUnits.get(attack.src)));
  // }
  // remainingUnits.put(attack.src, remainingUnits.get(attack.src) -
  // attack.numUnits);
  // // fixed a bug here: incorrect: as units attacked out are not reflected on
  // the
  // // dest units (are only updated after a war)
  // // remainingUnits.put(attack.dest,remainingUnits.getOrDefault(attack.dest,
  // // attack.dest.getNumUnits()) + attack.numUnits);
  // }
  // }

  /* ---------------- For server side usage ----------------- */

  /**
   * Perform all checkings at one time. - Do not perform any actual action - Used
   * by the server to ensure validity of commit in case of any possible change
   * before data arrives at the server end
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

    if (research != null) {
      checkRules(researchChecker, research, gameMap);
    }

    for (UpgradeOrder upgrade : upgrades) {
      checkRules(upgradeChecker, upgrade, gameMap);
    }

    // checkUsableUnitsAfterAllOrdersAreCollected();
  }

  /**
   * Perform move orders
   * 
   * @param gameMap is the map where orders are performed on
   */
  public void performMoves(GameMap gameMap) {
    for (MoveOrder move : moves) {
      System.out.println(move.toString());
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
      System.out.println(attack.toString());
      attack.takeAction(gameMap);
    }
  }

  /**
   * Perform research order
   * 
   * @param gameMap
   */
  public void performResearch(GameMap gameMap) {
    if (research != null) {
      research.takeAction(gameMap);
    }
  }

  /**
   * Perform upgrade order
   * 
   * @param gameMap
   */
  public void performUpgrade(GameMap gameMap) {
    for (UpgradeOrder upgrade : upgrades) {
      upgrade.takeAction(gameMap);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (MoveOrder o : moves) {
      builder.append(o.toString() + " (Cost: " + CostCalculator.calculateMoveCost(o, gameMap) + ")" + "\n");
    }

    for (AttackOrder o : attacks) {
      builder.append(o.toString() + " (Cost: " + CostCalculator.calculateAttackCost(o, gameMap) + ")" + "\n");
    }

    if (research != null) {
      builder.append(research.toString() + " (Cost: " + Constants.researchCosts.get(gameMap.getMaxTechLevel(playerId))
          + ")" + "\n");
    }

    for (UpgradeOrder o : upgrades) {
      builder.append(o.toString() + " (Cost: "
          + UnitManager.costToUpgrade(o.getNowLevel(), o.getTargetLevel()) * o.getNumUnits() + ")" + "\n");
    }
    return builder.toString();
  }

}
