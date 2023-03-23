package edu.duke.ece651.team6.shared;

public class Territory implements java.io.Serializable {
  private final String name;
  private int ownerId;
  private int numUnits;
  private WarZone warZone;
  private boolean underWar;

  public Territory() {
    this("Default Territory");
  }

  /**
   * Constructs a territory with a name, that does not have owner and any units
   */
  public Territory(String name) {
    this.name = name;
    this.ownerId = -1;
    this.numUnits = 0;
    warZone = null;
    underWar = false;
  }

  public Territory(String name, int ownerId) {
    this.name = name;
    this.ownerId = ownerId;
    this.numUnits = 0;
    warZone = null;
    underWar = false;
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
    this.numUnits = units;
    warZone = null;
    underWar = false;
  }

  /**
   * Get the territory's name
   * 
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get the territory's owner
   * 
   * @return the owner
   */
  public int getOwnerId() {
    return this.ownerId;
  }

  /**
   * Set the territory's ownerId
   * @param ownerId
   */
  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * Get the territory's units
   * 
   * @return the number of units
   */
  public int getNumUnits() {
    return this.numUnits;
  }

  /**
   * Dispatch an army that can move or join a war.
   * 
   * @param numUnits the number of units in the army
   * @return An army has numUnits Units.
   */
  private Army dispatchArmy(int numUnits) {
    if (numUnits > this.numUnits) {
      throw new IllegalArgumentException("Cannot dispatch required number of units!");
    }
    Army army = new Army(ownerId, numUnits);
    this.numUnits -= numUnits;
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
    if (numUnits == 0) {
      ownerId = army.getOwnerId();
      numUnits = army.getAllUnits();
    } else {
      numUnits += army.getAllUnits();
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
      if (numUnits > 0) {
        warZone.add(dispatchArmy(numUnits));
      }
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
    if (ownerId == target.getOwnerId()) {
      throw new IllegalArgumentException("Player cannot attack her own territory!");
    }
    if (numUnits <= 0) {
      throw new IllegalArgumentException("The numUnits to attack must be positive!");
    }
    target.attackedBy(dispatchArmy(numUnits));
  }

  /**
   * Notify the territory to run the war, update the status of territory.
   * Post: all attacks done.
   */
  public void update() {
    if (underWar) {
      warZone.startWar();
      underWar = false;
      warZone = null;
    }
    this.numUnits++;
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
    return "(name: " + name + ", ownerId: " + ownerId + ", units: " + numUnits + ")";
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
    this.numUnits = units;
  }
}
