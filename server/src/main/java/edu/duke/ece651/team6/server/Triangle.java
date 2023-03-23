package edu.duke.ece651.team6.server;

import java.util.HashSet;
import java.util.Set;

/**
 * A 2D triangle, consists of three Point2Ds.
 */
public class Triangle {
    private Point2D p1;
    private Point2D p2;
    private Point2D p3;
    private Point2D c;
    private double r;

    public Triangle(Point2D p1, Point2D p2, Point2D p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        c = new Point2D(0, 0);
        calc();
    }

    public Triangle(Point2D p, Edge e) {
        this.p1 = p;
        this.p2 = e.p1;
        this.p3 = e.p2;
        c = new Point2D(0, 0);
        calc();
    }

    /**
     * Calculate the circumscribed circle of this triangle, including the circle center and radius.
     */
    private void calc() {
        double a1, a2, b1, b2, c1, c2;
        a1 = 2 * (p2.x - p1.x);
        b1 = 2 * (p2.y - p1.y);
        c1 = p2.x * p2.x + p2.y * p2.y - p1.x * p1.x - p1.y * p1.y;
        a2 = 2 * (p3.x - p2.x);
        b2 = 2 * (p3.y - p2.y);
        c2 = p3.x * p3.x + p3.y * p3.y - p2.x * p2.x - p2.y * p2.y;
        c.x = ((b2 * c1) - (b1 * c2)) / ((b2 * a1) - (b1 * a2));
        c.y = ((a1 * c2) - (a2 * c1)) / ((b2 * a1) - (b1 * a2));
        r = c.dist(p1);
    }

    /**
     * To check if a Point2D is inside the circumscribed circle of this triangle.
     * @param point 
     * @return true if its inside.
     */
    public boolean circleContains(Point2D point) {
        if (c.dist(point) - r <= 0) {
            return true;
        }
        return false;
    }

    /**
     * To check if the circumscribed circle of this triangle is on the left of a Point2D.
     * @param point
     * @return true if is on the left.
     */
    public boolean onTheLeft(Point2D point) {
        if (c.x + r < point.x) {
            return true;
        }
        return false;
    }

    /**
     * To check if two triangles have the same Point2D in common.
     * @param t
     * @return
     */
    public boolean overlaps(Triangle t) {
        if (t.overlaps(p1) || t.overlaps(p2) || t.overlaps(p3)) {
            return true;
        }
        return false;
    }

    /**
     * To check if this triangle contains the given Point2D.
     * @param p
     * @return
     */
    public boolean overlaps(Point2D p) {
        if (p1.equals(p) || p2.equals(p) || p3.equals(p)) {
            return true;
        }
        return false;
    }

    /**
     * Get all the edges of this triangle.
     * @return
     */
    Set<Edge> getEdges() {
        Edge e1 = new Edge(p1, p2);
        Edge e2 = new Edge(p2, p3);
        Edge e3 = new Edge(p1, p3);
        Set<Edge> edges = new HashSet<>();
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        return edges;
    }

}
