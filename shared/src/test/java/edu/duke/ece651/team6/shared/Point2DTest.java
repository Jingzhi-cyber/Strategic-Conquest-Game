package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.Edge;
import edu.duke.ece651.team6.shared.Point2D;

public class Point2DTest {
    @Test
    void testCloseTo() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(1, 0);
        assertTrue(p1.closeTo(p2, 1));
        assertFalse(p1.closeTo(p2, 0.5));
    }

    @Test
    void testDist() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(1, 1);
        assertEquals(Math.sqrt(2), p1.dist(p2));
    }

    @Test
    void testEquals() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(0.00000001, 0);
        assertEquals(p1, p2);
        assertEquals("(0.0, 0.0)", p1.toString());
        assertNotEquals(p1.hashCode(), p2.hashCode());
        Point2D p3 = new Point2D(1, 0);
        assertNotEquals(p1, p3);
        Edge e = new Edge(p1, p3);
        assertNotEquals(p1, e);
    }

    @Test
    void isOnRect() {
        Point2D p = new Point2D(3.5, 2);
        assertTrue(p.isOnRectangle(4, 2));
        p = new Point2D(3.5, 1);
        assertFalse(p.isOnRectangle(4, 2));
        p = new Point2D(3.5, 3);
        assertFalse(p.isOnRectangle(4, 2));
    }
    
}
