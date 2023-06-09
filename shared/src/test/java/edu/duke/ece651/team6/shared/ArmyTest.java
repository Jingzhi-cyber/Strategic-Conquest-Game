package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class ArmyTest {
    @Test
    void testArmy() {
        assertThrows(IllegalArgumentException.class, () -> new Army(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Army(0, -1));
        Army army = new Army(0, 5);
        assertSame(0, army.getOwnerId());
    }

    @Test
    void testGetAllUnits() {
        Army army = new Army(0, 2);
        assertEquals(2, army.getAllUnits());
        assertTrue(army.hasLost());
        Army army2 = new Army(0, 10);
        assertEquals(10, army2.getUnitsList().size());
        assertTrue(army2.hasLost());
    }

    @Test
    void testGetPlayerID() {
        Army army = new Army(0, 1);
        assertEquals(0, army.getOwnerId());
    }

    @Test
    void testHasLost() {
        Army army = new Army(0, 1);
        assertFalse(army.hasLost());
        army.pollFirst();
        assertTrue(army.hasLost());
    }

    @Test
    void testLoss() {
        Army army = new Army(0, 1);
        assertDoesNotThrow(() -> army.pollFirst());
        assertNull(army.pollFirst());
    }

    @Test
    void testMergeArmy() {
        Army army1 = new Army(0, 1);
        Army army2 = new Army(0, 1);
        army1.pollFirst();
        assertTrue(army1.hasLost());
        army1.mergeArmy(army2);
        assertFalse(army1.hasLost());
    }
}
