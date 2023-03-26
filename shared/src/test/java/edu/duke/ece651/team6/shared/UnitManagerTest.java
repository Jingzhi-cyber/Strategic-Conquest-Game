package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UnitManagerTest {
    @Test
    public void testBasicFunc() {
        assertEquals("{name: Ryze, level: 0, bonus: 0}", UnitManager.newUnit().toString());
        assertEquals(6, UnitManager.newUnits(6).size());
        assertThrows(IllegalArgumentException.class, () -> UnitManager.costToUpgrade(3, 1));
        assertEquals(0, UnitManager.costToUpgrade(4, 4));
        assertThrows(IndexOutOfBoundsException.class, () -> UnitManager.costToUpgrade(0, 7));
        Unit unit = UnitManager.newUnit();
        UnitManager.upgrade(unit, 4);
        assertEquals("{name: Zed, level: 4, bonus: 8}", unit.toString());
        assertThrows(IllegalArgumentException.class, () -> UnitManager.upgrade(unit, 2));
    }
}
