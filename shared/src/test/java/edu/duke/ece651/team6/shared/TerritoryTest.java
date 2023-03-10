package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TerritoryTest {
    @Test
    void testBasicFunc() {
        Territory t1 = new Territory();
        assertEquals("Default Territory", t1.getName());
        assertEquals(null, t1.getOwner());
        assertEquals(0, t1.getUnits());

        Territory t2 = new Territory("Hogwarts", "Player1", 5);
        assertEquals("Hogwarts", t2.getName());
        assertEquals("Player1", t2.getOwner());
        assertEquals(5, t2.getUnits());

        assertThrows(IllegalArgumentException.class, () -> new Territory("illegal", "player", -1));

        Territory t3 = new Territory("Hogwarts", "Player2", 6);
        assertEquals(t2, t3);
        assertEquals(t2.hashCode(), t3.hashCode());
        assertNotEquals(t2, "(name: Hogwarts, owner: Player1, units: 5)");

        Territory t4 = new Territory("Default", "Player1", 5);
        assertNotEquals(t2, t4);
        assertNotEquals(t2.hashCode(), t4.hashCode());

        assertEquals("(name: Hogwarts, owner: Player1, units: 5)", t2.toString());
    }
}
