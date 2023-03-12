package edu.duke.ece651.team6.shared;

/**
 * This class represents a unit that can move or attack. It consists of a group of Units, and belongs to a Player.
 */
public class Army {
    private final int ownerId;
    //private final List<Unit> list;
    private int numUnits;

    public Army(int ownerId, int numUnits) {
        this.ownerId = ownerId;
        if (numUnits <= 0) {
            throw new IllegalArgumentException("Number of units in an army must be greater than 0!");
        }
        this.numUnits = numUnits;
    }

    /**
     * Merge two armies which have the same owner.
     * @param army to merge
     */
    public void mergeArmy(Army army) {
        numUnits += army.getAllUnits();
    }

    /**
     * Lose a battle, in this version, the number of units minus 1.
     */
    public void loseOneTurn() {
        if (numUnits == 0) {
            throw new RuntimeException("This army has already lost!");
        }
        numUnits--;
    }

    /**
     * TO check if this army has lost.
     * @return true if the army has lost.
     */
    public boolean hasLost() {
        if (numUnits == 0) {
            return true;
        }
        return false;
    }

    /**
     * Dissolve an army.
     * @return the number of units in this army.
     */
    public int getAllUnits() {
        int temp = numUnits;
        numUnits = 0;
        return temp;
    }

    public int getOwnerId() { return ownerId; }

}
