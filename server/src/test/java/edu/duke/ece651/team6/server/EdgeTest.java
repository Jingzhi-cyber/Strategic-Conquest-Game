package edu.duke.ece651.team6.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class EdgeTest {
    @Test
    void testEquals() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(1, 3);
        Point2D p3 = new Point2D(1, 5);
        Edge e1 = new Edge(p1, p2);
        Edge e2 = new Edge(p2, p1);
        Edge e3 = new Edge(p1, p3);
        assertEquals(e1, e2);
        assertNotEquals(e1, p1);
        assertNotEquals(e1, e3);
        assertEquals("(0.0, 0.0)------(1.0, 3.0)", e1.toString());
        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
