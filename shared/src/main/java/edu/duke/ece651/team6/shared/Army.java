package edu.duke.ece651.team6.shared;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a unit that can move or attack. It consists of a group of Units, and belongs to a Player.
 */
public class Army {
    private final int ownerId;
    private LinkedList<Unit> units;

    public Army(int ownerId, int numUnits) {
        this.ownerId = ownerId;
        if (numUnits <= 0) {
            throw new IllegalArgumentException("Number of units in an army must be greater than 0!");
        }
        units = new LinkedList<>();
        for (int i = 0; i < numUnits; i++) {
            units.add(UnitManager.newUnit());
        }
    }

    public Army(int ownerId) {
        this.ownerId = ownerId;
        units = new LinkedList<>();
    }

    /**
     * Merge two armies which have the same owner.
     * @param army to merge
     */
    public void mergeArmy(Army army) {
        units.addAll(army.getUnitsList());
    }

    public void add(Unit unit) {
        units.add(unit);
    }

    public void sort() {
        units.sort((Unit u1, Unit u2) -> (u1.level() - u2.level()));
    }

    public Unit pollLast() {
        return units.pollLast();
    }

    public void addLast(Unit unit) {
        if (unit == null) {
            return;
        }
        units.addFirst(unit);
    }

    public Unit pollFirst() {
        return units.pollFirst();
    }

    public void addFirst(Unit unit) {
        if (unit == null) {
            return;
        }
        units.addFirst(unit);
    }

    /**
     * TO check if this army has lost.
     * @return true if the army has lost.
     */
    public boolean hasLost() {
        if (units.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Dissolve an army.
     * @return the number of units in this army.
     */
    public List<Unit> getUnitsList() {
        List<Unit> temp = List.copyOf(units);
        units.clear();
        return temp;
    }

    public int getAllUnits() {
        int temp = units.size();
        getUnitsList();
        return temp;
    }

    public int getOwnerId() { return ownerId; }

}
