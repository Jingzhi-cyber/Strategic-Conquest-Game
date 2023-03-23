package edu.duke.ece651.team6.shared;

import java.util.Deque;
import java.util.LinkedList;

/**
 * An abstract class that can represent a war happens on a territory.
 */
public abstract class WarZone {
    protected final Territory territory;
    protected Deque<Army> armies;

    public WarZone(Territory territory) {
        this.territory = territory;
        armies = new LinkedList<>();
    }

    /**
     * Add an army to the warzone, those have the same owner will be merged.
     * @param army the army to add.
     */
    public void add(Army army) {
        for (Army a : armies) {
            if (a.getOwnerId() == army.getOwnerId()) {
                a.mergeArmy(army);
                return;
            }
        }
        armies.add(army);
    }

    /**
     * Start the war, according to different rules.
     */
    public abstract void startWar();
    
}
