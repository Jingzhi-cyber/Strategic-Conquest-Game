package edu.duke.ece651.team6.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class TriangleTest {
    @Test
    void testCircleContains() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(2, 0);
        Point2D p3 = new Point2D(1, Math.sqrt(3));
        Triangle t = new Triangle(p1, p2, p3);
        assertTrue(t.circleContains(new Point2D(1, 0)));
        assertTrue(t.circleContains(new Point2D(1, -1 / Math.sqrt(3))));
        assertFalse(t.circleContains(new Point2D(2.1, 0)));
        p1 = new Point2D(0, 0);
        p2 = new Point2D(2, 0);
        p3 = new Point2D(0, 2);
        t = new Triangle(p1, p2, p3);
        assertTrue(t.circleContains(new Point2D(2, 2)));
        assertTrue(t.circleContains(new Point2D(1 - Math.sqrt(2), 1)));
        assertFalse(t.circleContains(new Point2D(0, 2.00001)));
    }

    @Test
    void testOnTheLeft() {
        Point2D p1 = new Point2D(0, 0);
        Edge e = new Edge(new Point2D(1, 1), new Point2D(1, 0));
        Triangle t = new Triangle(p1, e);
        assertFalse(t.onTheLeft(new Point2D(0.5 + 1 / Math.sqrt(2), 0.5)));
        assertTrue(t.onTheLeft(new Point2D(2, 0)));
    }

    @Test
    void testOverlaps() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(1, 0);
        Point2D p3 = new Point2D(1, 1);
        Triangle t = new Triangle(p1, p2, p3);
        assertTrue(t.overlaps(p3));
        Point2D p4 = new Point2D(10, 5);
        Point2D p5 = new Point2D(3, 7);
        Triangle t2 = new Triangle(p3, p4, p5);
        assertTrue(t2.overlaps(t));
        Point2D p6 = new Point2D(5, 6);
        Triangle t3 = new Triangle(p4, p5, p6);
        assertFalse(t3.overlaps(t));
    }

    @Test
    void testGetEdges() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(1, 0);
        Point2D p3 = new Point2D(1, 1);
        Triangle t = new Triangle(p1, p2, p3);
        Set<Edge> set = t.getEdges();
        Set<Edge> temp = new HashSet<>();
        temp.add(new Edge(p1, p2));
        temp.add(new Edge(p3, p2));
        temp.add(new Edge(p1, p3));
        assertEquals(temp, set);
    }
}
