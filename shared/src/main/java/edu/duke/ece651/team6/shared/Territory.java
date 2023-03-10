package edu.duke.ece651.team6.shared;


public class Territory implements java.io.Serializable {
    private final String name;
    // FIXME: owner should be a Player class
    private String owner;
    private int numUnits;
    private WarZone warZone;
    private boolean underWar;

    /**
     * Constructs a territory with default name, that does not have owner and any units
     */
    public Territory() {
        this.name = "Default Territory";
        this.owner = null;
        this.numUnits = 0;
        warZone = null;
        underWar = false;
    }

    /**
     * Constructs a territory that has a specified name, with owner and some units
     * @param name the name of territory
     * @param owner the owner of territory
     * @param units the number of units on the territory
     */
    public Territory(String name, String owner, int units) {
        this.name = name;
        if (units < 0) {
            throw new IllegalArgumentException("Territory's unit must be non-negative but is " + units);
        }
        this.owner = owner;
        this.numUnits = units;
        warZone = null;
        underWar = false;
    }

    /**
     * Get the territory's name
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the territory's owner
     * @return the owner
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Get the territory's units
     * @return the number of units
     */
    public int getNumUnits() {
        return this.numUnits;
    }

    /**
     * Dispatch an army that can move or join a war.
     * @param numUnits the number of units in the army
     * @return An army has numUnits Units.
     */
    private Army dispatchArmy(int numUnits) {
        if (numUnits > this.numUnits) {
            throw new IllegalArgumentException("Cannot dispatch required number of units!");
        }
        Army army = new Army(owner, numUnits);
        this.numUnits -= numUnits;
        return army;
    }

    /**
     * Absorb an army. 
     * If the owner of this territory is the same as the owner of army, the numUnits will update;
     * If not, an IllegalArgumentException will be thrown.
     * If this territory has no Unit, the owner of the Army will become this territory's owner.
     * @param army to join this territory
     */
    protected void absorbArmy(Army army) {
        if (numUnits == 0) {
            owner = army.getOwner();
            numUnits = army.getAllUnits();
        } else {
            numUnits += army.getAllUnits();
        }
    }

    /**
     * Attacked by other armies. This action will make this territory has a new warzone.
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
     * Pre: dest territory has the same owner as this territory, numUnits is less than numUnits of this territory.
     * Post: Move action done.
     * @param dest
     * @param numUnits
     */
    public void moveTo(Territory dest, int numUnits) {
        if (!owner.equals(dest.getOwner())) {
            throw new IllegalArgumentException("Cannot move to a different player's territory!");
        }
        dest.absorbArmy(dispatchArmy(numUnits));
    }

    /**
     * Make an Army of size numUnits to attack target.
     * Pre: target is adjacent to this territory, numUnits is less than numUnits of this territory.
     * Post: attack is registered.
     * @param target the territory to attack.
     * @param numUnits number of Units of this attack.
     */
    public void attack(Territory target, int numUnits) {
        if (owner.equals(target.getOwner())) {
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
        return "(name: " + name + ", owner: " + owner.toString() + ", units: " + numUnits + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
