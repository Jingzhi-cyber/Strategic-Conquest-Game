package edu.duke.ece651.team6.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Point2D p4 = new Point2D(0, 0.000000001);
        assertThrows(IllegalArgumentException.class, () -> new Edge(p1, p4));
    }

    @Test
    void testInter() {
        Edge e1 = new Edge(0, 0, 0, 5);
        Edge e2 = new Edge(1, 0, 1, 5);
        assertNull(e1.getIntersection(e2));
        e1 = new Edge(0, 0, 1, 1);
        e2 = new Edge(0, -1, 1, 0);
        assertNull(e1.getIntersection(e2));
        e1 = new Edge(0, 0, 0, 5);
        e2 = new Edge(-1, 1, 5, 1);
        assertEquals(new Point2D(0, 1), e1.getIntersection(e2));
        assertEquals(new Point2D(0, 1), e2.getIntersection(e1));
    }
}
