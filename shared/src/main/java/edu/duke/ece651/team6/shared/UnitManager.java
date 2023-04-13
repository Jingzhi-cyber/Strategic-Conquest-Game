package edu.duke.ece651.team6.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a factory that record details of Unit.
 */
public class UnitManager {

    private static final int[] cost = {0, 3, 11, 30, 55, 90, 140};
    private static final int[] bonus = {0, 1, 3, 5, 8, 11, 15};
    private static final String[] names = {"Ryze", "Viktor", "Talon", "Sylas", "Zed", "Fizz", "Qiyana"};
    
    private UnitManager() {}

    /**
     * Create one unit of level 0.
     * @return
     */
    public static Unit newUnit() {
        return new Unit(0, bonus[0], names[0]);
    }

    /**
     * Create given number of units of level 0.
     * @param num
     * @return
     */
    public static List<Unit> newUnits(int num) {
        List<Unit> units = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            units.add(newUnit());
        }
        return units;
    }

    /**
     * Return the food cost player needs to upgrade a unit.
     * @param now the unit's current level
     * @param target the target level
     * @return
     */
    public static int costToUpgrade(int now, int target) {
        if (target < now) {
            throw new IllegalArgumentException("Can only upgrade from a lower level to a higher one!");
        }
        return cost[target] - cost[now];
    }

    public static void upgrade(Unit unit, int level) {
        if (level < unit.level()) {
            throw new IllegalArgumentException("Can only upgrade from a lower level to a higher one!");
        }
        unit.upgrade(level, bonus[level], names[level]);
    }

}
