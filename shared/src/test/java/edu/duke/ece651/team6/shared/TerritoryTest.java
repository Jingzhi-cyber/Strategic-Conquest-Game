package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class TerritoryTest {
  @Test
  void testBasicFunc() {
    Territory t1 = new Territory();
    assertEquals("Default Territory", t1.getName());
    assertEquals(-1, t1.getOwnerId());
    assertEquals(0, t1.getNumUnits());

    Territory t2 = new Territory("Hogwarts", 0, 5);
    assertEquals("Hogwarts", t2.getName());
    assertEquals(0, t2.getOwnerId());
    assertEquals(5, t2.getNumUnits());

    assertThrows(IllegalArgumentException.class, () -> new Territory("illegal", 0, -1));

    Territory t3 = new Territory("Hogwarts", 0, 6);
    assertEquals(t2, t3);
    assertEquals(t2.hashCode(), t3.hashCode());
    assertNotEquals(t2, "(name: Hogwarts, ownerId: 0, units: 5)");

    Territory t4 = new Territory("Default", 0, 5);
    assertNotEquals(t2, t4);
    assertNotEquals(t2.hashCode(), t4.hashCode());

    assertEquals("(name: Hogwarts, ownerId: 0, units: 5)", t2.toString());

    Territory t5 = new Territory("SetOwnerId");
    t5.setOwnerId(1);
    assertEquals(1, t5.getOwnerId());
  }

  @Test
  void testMoveTo() {
    Territory t1 = new Territory("1", 1, 5);
    Territory t2 = new Territory("2", 2, 5);
    assertThrows(IllegalArgumentException.class, () -> t1.moveTo(t2, 2));
    Territory t3 = new Territory("3", 1, 5);
    assertThrows(IllegalArgumentException.class, () -> t1.moveTo(t3, 6));
    t1.moveTo(t3, 3);
    t3.update();
    assertEquals(2, t1.getNumUnits());
    assertEquals(9, t3.getNumUnits()); // territory.update() increase numUnit by 1

    // new moveTo
    assertThrows(IllegalArgumentException.class, () -> t1.moveTo(t3, new int[]{5, 0, 0, 0, 0, 0, 0}));
    assertThrows(IllegalArgumentException.class, () -> t1.moveTo(t2, new int[]{1, 0, 0, 0, 0, 0, 0}));
    t3.moveTo(t1, new int[]{6, 0, 0, 0, 0, 0, 0});
    assertTrue(Arrays.equals(t1.getAllUnitsNum(), new int[]{8, 0, 0, 0, 0, 0, 0}));
    assertTrue(Arrays.equals(t3.getAllUnitsNum(), new int[]{3, 0, 0, 0, 0, 0, 0}));
  }

  @Test
  void testAttack() {
    Territory t1 = new Territory("1", 1, 100);
    assertThrows(IllegalArgumentException.class, () -> t1.attack(t1, 5));
    Territory t2 = new Territory("2", 2, 1);
    assertThrows(IllegalArgumentException.class, () -> t1.attack(t2, -1));
    t1.attack(t2, 80);
    Territory t3 = new Territory("3", 3, 10);
    t3.attack(t2, 1);
    t2.update();
    assertEquals(1, t2.getOwnerId());
    Territory t4 = new Territory("4", 4, 0);
    t1.attack(t4, 5);
    t4.update();
    assertEquals(1, t4.getOwnerId());

    Territory t5 = new Territory("nihao", 1, 0);
    Territory t6 = new Territory("hello", 2, 10);
    assertThrows(IllegalArgumentException.class, () -> t6.attack(t6, new int[]{1, 0, 0, 0, 0, 0, 0}));
    assertThrows(IllegalArgumentException.class, () -> t6.attack(t5, new int[]{5, 1, 0, 0, 0, 0, 0}));
    t6.attack(t5, new int[]{5, 0, 0, 0, 0, 0, 0});
    t5.update();
    assertTrue(Arrays.equals(t5.getAllUnitsNum(), new int[]{6, 0, 0, 0, 0, 0, 0}));
    assertEquals(2, t5.getOwnerId());
  }

  @Test
  public void testUpgrade() {
    Territory t1 = new Territory("hello", 1, 5);
    assertThrows(IllegalArgumentException.class, () -> t1.upgradeOneUnit(1, 3));
    t1.upgradeOneUnit(0, 3);
    assertTrue(Arrays.equals(t1.getAllUnitsNum(), new int[]{4, 0, 0, 1, 0, 0, 0}));
    assertThrows(IllegalArgumentException.class, () -> t1.upgradeOneUnit(3, 2));
  }

  @Test
  public void test_initNumUnits() {
    Territory t = new Territory("A", 1);
    t.initNumUnits(5);
    assertEquals(5, t.getNumUnits());
  }
}
