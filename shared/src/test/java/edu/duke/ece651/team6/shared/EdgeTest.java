package edu.duke.ece651.team6.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.Edge;
import edu.duke.ece651.team6.shared.Point2D;

import java.util.List;

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
        Point2D p5 = new Point2D(2, 2);
        assertEquals(new Point2D(1, 1), new Edge(p1, p5).middle());
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
        e1 = new Edge(0, 0, 1, 1);
        e2 = new Edge(1, 0, 0, 1);
        assertEquals(new Point2D(0.5, 0.5), e1.getIntersection(e2));
        e2 = new Edge(2, 0, 2, 10);
        assertNull(e1.getIntersection(e2));
    }

    @Test
    void testInterRect() {
        Edge e1 = new Edge(0, 1, 1, 1);
        List<Point2D> points = e1.getInterWithRectangle(4, 2);
        assertEquals(2, points.size());
        assertEquals(new Point2D(4, 1), points.get(0));
        assertEquals(new Point2D(0, 1), points.get(1));
        e1 = new Edge(2, 0.1, 2, 0.5);
        points = e1.getInterWithRectangle(4, 2);
        assertEquals(new Point2D(2, 0), points.get(0));
        assertEquals(new Point2D(2, 2), points.get(1));
    }

    @Test
    void testInside() {
        Edge e = new Edge(1, 1, 2, 1);
        assertEquals(e, e.getEdgeInsideRectangle(4, 2));
        e = new Edge(1, -1, 2, -1);
        assertNull(e.getEdgeInsideRectangle(4, 2));
        e = new Edge(1, -1, 1, 1);
        assertEquals(new Edge(1, 0, 1, 1), e.getEdgeInsideRectangle(4, 2));
        e = new Edge(1, 1, 1, 5);
        assertEquals(new Edge(1, 1, 1, 2), e.getEdgeInsideRectangle(4, 2));
        e = new Edge(1, 2, 1, 3);
        assertNull(e.getEdgeInsideRectangle(4, 2));
    }
}
