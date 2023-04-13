package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class UnitTest {
    @Test
    public void testUnit() {
        Unit unit = new Unit("hello");
        assertEquals(0, unit.level());
        assertEquals(0, unit.bonus());
        assertEquals("{name: hello, level: 0, bonus: 0}", unit.toString());
        unit.upgrade(1, 20, "hero");
        assertEquals(1, unit.level());
        assertEquals(20, unit.bonus());
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        Unit unit = new Unit(0, 1, "name");
        Unit cloned = (Unit) unit.clone();
        assertEquals(unit.bonus(), cloned.bonus());
        assertEquals(unit.level(), cloned.level());
    }
}
