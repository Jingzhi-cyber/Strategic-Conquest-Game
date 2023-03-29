package edu.duke.ece651.team6.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A 2D triangle, consists of three Point2Ds.
 */
public class Triangle {
    private Point2D p1;
    private Point2D p2;
    private Point2D p3;
    protected Point2D c;
    protected double r;

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

    public Edge getVoronoiEdge(Edge e) {
        if (c.x <= 0 || c.x >= 1000 || c.y <= 0 || c.y >= 500) {
            return null;
        }
        Point2D mid = e.middle();
        Edge verticalE = new Edge(c, mid);
        List<Point2D> points = new ArrayList<>();
        Point2D p1 = new Edge(new Point2D(0, 0), new Point2D(1000, 0)).getIntersection(verticalE);
        Point2D p2 = new Edge(new Point2D(1000, 500), new Point2D(1000, 0)).getIntersection(verticalE);
        Point2D p3 = new Edge(new Point2D(0, 0), new Point2D(0, 500)).getIntersection(verticalE);
        Point2D p4 = new Edge(new Point2D(1000, 500), new Point2D(0, 500)).getIntersection(verticalE);
        if (p1 != null) {
            points.add(p1);
        }
        if (p2 != null) {
            points.add(p2);
        }
        if (p3 != null) {
            points.add(p3);
        }
        if (p4 != null) {
            points.add(p4);
        }
        Set<Edge> edges = getEdges();
        edges.remove(e);
        List<Edge> edges2 = new ArrayList<>(edges);
        Point2D pt = edges2.get(0).getIntersection(verticalE);
        if (pt == null) {
            pt = edges2.get(1).getIntersection(verticalE);
        }
        if ((mid.x - pt.x) / (points.get(0).x - pt.x) >= 0) {
            return new Edge(points.get(0), c);
        }
        return new Edge(points.get(1), c);
    }

}
