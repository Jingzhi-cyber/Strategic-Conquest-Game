package edu.duke.ece651.team6.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Territory implements java.io.Serializable {
  private final String name;
  private int ownerId;
  private List<Deque<Unit>> units;
  private int numLevel;
  private WarZone warZone;
  private boolean underWar;
  private int food;
  private int technology;

  public Territory() {
    this("Default Territory");
  }

  /**
   * Constructs a territory with a name, that does not have owner and any units
   */
  public Territory(String name) {
    this.name = name;
    this.ownerId = -1;
    unitsInit();
    warZone = null;
    underWar = false;
    food = 0;
    technology = 0;
  }

  public Territory(String name, int ownerId) {
    this.name = name;
    this.ownerId = ownerId;
    unitsInit();
    warZone = null;
    underWar = false;
    food = 0;
    technology = 0;
  }

  /**
   * Constructs a territory that has a specified name, with owner and some units
   * 
   * @param name  the name of territory
   * @param owner the owner of territory
   * @param units the number of units on the territory
   */
  public Territory(String name, int ownerId, int units) {
    this.name = name;
    if (units < 0) {
      throw new IllegalArgumentException("Territory's unit must be non-negative but is " + units);
    }
    this.ownerId = ownerId;
    unitsInit();
    this.units.get(0).addAll(UnitManager.newUnits(units));
    warZone = null;
    underWar = false;
    food = 0;
    technology = 0;
  }

  private void unitsInit() {
    numLevel = 7;
    units = new ArrayList<>();
    for (int i = 0; i < numLevel; i++) {
      units.add(new LinkedList<>());
    }
  }

  public String getName() {
    return this.name;
  }

  public int getOwnerId() {
    return this.ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public int getNumUnits() {
    return units.get(0).size();
  }

  private Army dispatchArmy(int[] numUnits) {
    Army army = new Army(ownerId);
    for (int i = 0; i < numLevel; i++) {
      for (int j = 0; j < numUnits[i]; j++) {
        army.add(units.get(i).poll());
      }
    }
    return army;
  }

  /**
   * Absorb an army.
   * If the owner of this territory is the same as the owner of army, the numUnits
   * will update;
   * If not, an IllegalArgumentException will be thrown.
   * If this territory has no Unit, the owner of the Army will become this
   * territory's owner.
   * 
   * @param army to join this territory
   */
  protected void absorbArmy(Army army) {
    if (Arrays.equals(getAllUnitsNum(), new int[numLevel])) {
      ownerId = army.getOwnerId();
    }
    for (Unit u : army.getUnitsList()) {
      units.get(u.level()).add(u);
    }
  }

  /**
   * Attacked by other armies. This action will make this territory has a new
   * warzone.
   * 
   * @param army which attackes this territory.
   */
  private void attackedBy(Army army) {
    if (!underWar) {
      underWar = true;
      warZone = new RoundFight(this);
    }
    warZone.add(army);
  }

  /**
   * Move numUnits Units to dest.
   * Pre: dest territory has the same owner as this territory, numUnits is less
   * than numUnits of this territory.
   * Post: Move action done.
   * 
   * @param dest
   * @param numUnits
   */
  public void moveTo(Territory dest, int numUnits) {
    int[] a = new int[numLevel];
    a[0] = numUnits;
    moveTo(dest, a);
  }

  /**
   * Try to move some units to another Territory.
   * 
   * @param dest     the territory to move to.
   * @param numUnits an array, represents how many units of each level are engaged
   *                 in this move.
   */
  public void moveTo(Territory dest, int[] numUnits) {
    for (int i = 0; i < numLevel; i++) {
      if (numUnits[i] > units.get(i).size() || numUnits[i] < 0) {
        throw new IllegalArgumentException("Invalid move number: " + numUnits[i]);
      }
    }
    if (ownerId != dest.getOwnerId()) {
      throw new IllegalArgumentException("Cannot move to a different player's territory!");
    }
    dest.absorbArmy(dispatchArmy(numUnits));
  }

  /**
   * Make an Army of size numUnits to attack target.
   * Pre: target is adjacent to this territory, numUnits is less than numUnits of
   * this territory.
   * Post: attack is registered.
   * 
   * @param target   the territory to attack.
   * @param numUnits number of Units of this attack.
   */
  public void attack(Territory target, int numUnits) {
    int[] a = new int[numLevel];
    a[0] = numUnits;
    attack(target, a);
  }

  /**
   * Make an Army to attack the target.
   * 
   * @param target   the target to attack.
   * @param numUnits an array, indicate the number of each level of units in this
   *                 attack.
   */
  public void attack(Territory target, int[] numUnits) {
    if (ownerId == target.getOwnerId()) {
      throw new IllegalArgumentException("Player cannot attack her own territory!");
    }
    for (int i = 0; i < numLevel; i++) {
      if (numUnits[i] > units.get(i).size() || numUnits[i] < 0) {
        throw new IllegalArgumentException("Invalid attack number: " + numUnits[i]);
      }
    }
    target.attackedBy(dispatchArmy(numUnits));
  }

  private void recover() {
    units.get(0).add(UnitManager.newUnit());
  }

  /**
   * Notify the territory to run the war, update the status of territory.
   * Post: all attacks done.
   */
  public void update() {
    if (underWar) {
      int[] allUnitsNum = getAllUnitsNum();
      if (!Arrays.equals(allUnitsNum, new int[numLevel])) {
        warZone.add(dispatchArmy(allUnitsNum));
      }
      warZone.startWar();
      underWar = false;
      warZone = null;
    }
    recover();
    produceFood();
    produceTechnology();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (o.getClass().equals(getClass())) {
      Territory t = (Territory) o;
      return t.name.equals(name);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "(name: " + name + ", ownerId: " + ownerId + ", units: " + units.get(0).size() + ")";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /* Initialize units information after player gives the information. */
  public void initNumUnits(int units) {
    this.units.get(0).addAll(UnitManager.newUnits(units));
  }

  /* Produce some food resource every round */
  protected void produceFood() {
    food += 10;
  }

  /* Produce some technology resource every round */
  protected void produceTechnology() {
    technology += 10;
  }

  /* Collect food resource of this territory. */
  public int getFood() {
    int temp = food;
    food = 0;
    return temp;
  }

  /* Collect technology resource of this territory. */
  public int getTechnology() {
    int temp = technology;
    technology = 0;
    return temp;
  }

  public int getUnitsNumByLevel(int level) {
    return units.get(level).size();
  }

  public int getAllUnits() {
    return units.get(0).size();
  }

  /**
   * Get how many units of each level are left in this territory.
   * 
   * @return
   */
  public int[] getAllUnitsNum() {
    int[] a = new int[numLevel];
    for (int i = 0; i < numLevel; i++) {
      a[i] = units.get(i).size();
    }
    return a;
  }

  /**
   * Upgrade a unit with level now to level target.
   * 
   * @param now
   * @param target
   */
  public void upgradeOneUnit(int now, int target) {
    if (units.get(now).size() == 0) {
      throw new IllegalArgumentException("No unit with level " + now + ", upgrade failed!");
    }
    if (target < now || now < 0 || target < 0 || now >= numLevel || target >= numLevel) {
      throw new IllegalArgumentException("Invalid level!");
    }
    Unit unit = units.get(now).poll();
    UnitManager.upgrade(unit, target);
    units.get(target).add(unit);
  }

}
