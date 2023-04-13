package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


public class RoundFightTest {
    @Test
    void testWarZone() {
        Territory territory = new Territory();
        WarZone wz = new RoundFight(territory);
        assertSame(wz.territory, territory);
        assertEquals(0, wz.armies.size());
    }

    @Test
    void testAdd() {
        WarZone wz = new RoundFight(new Territory());
        wz.add(new Army(0, 1));
        wz.add(new Army(3, 2));
        assertEquals(2, wz.armies.size());
        wz.add(new Army(0, 3));
        wz.add(new Army(3, 3));
        assertEquals(2, wz.armies.size());
    }

    @Test
    void testStartWar() {
        Territory territory = new Territory();
        WarZone wz = new RoundFight(territory);
        assertThrows(RuntimeException.class, () -> wz.startWar());
        wz.add(new Army(0, 5));
        wz.startWar();
        assertEquals(5, territory.getNumUnits());
        assertEquals(0, wz.armies.size());
        wz.add(new Army(0, 100000));
        wz.add(new Army(1, 100));
        wz.add(new Army(2, 100));
        wz.add(new Army(3, 100));
        wz.add(new Army(4, 100));
        wz.add(new Army(5, 100));
        wz.add(new Army(6, 100));
        wz.add(new Army(7, 100));
        wz.startWar();
        assertEquals(0, territory.getOwnerId());
    }
}
