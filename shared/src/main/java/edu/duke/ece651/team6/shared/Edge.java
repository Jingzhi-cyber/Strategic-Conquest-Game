package edu.duke.ece651.team6.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A line on the 2D plain, consists of two Point2Ds.
 */
public class Edge implements Serializable {
    public Point2D p1;
    public Point2D p2;

    private double k;
    private double b;
    private double x0;
    private boolean vertical;

    public Edge(double x1, double y1, double x2, double y2) {
        this(new Point2D(x1, y1), new Point2D(x2, y2));
    }

    public Edge(Point2D p1, Point2D p2) {
        if (p1.equals(p2)) {
            throw new IllegalArgumentException("Cannot have an edge on the same point.");
        }
        if (Double.compare(p1.x, p2.x) < 0 || Double.compare(p1.x, p2.x) == 0 && Double.compare(p1.y, p2.y) < 0) {
            this.p1 = p1;
            this.p2 = p2;
        } else {
            this.p1 = p2;
            this.p2 = p1;
        }
        if (Math.abs(p1.x - p2.x) < 0.000001) {
            x0 = p1.x;
            k = 100000;
            vertical = true;
        } else {
            k = (p1.y - p2.y) / (p1.x - p2.x);
            b = p1.y - k * p1.x;
            vertical = false;
        }
    }

    public Point2D middle() {
        return new Point2D((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    /**
     * Return the intersection of this Edge and another Edge e. 
     * @param e another Edge
     * @return If the intersection is on this Edge, return the intersection in the form of Point2D.
     * Else return null;
     */
    public Point2D getIntersection(Edge e) {
        if (Math.abs(k - e.k) < 0.000001 || (vertical && e.vertical)) {
            return null;
        }
        double x;
        double y;
        if (vertical) {
            x = x0;
            y = e.k * x + e.b;
        } else if (e.vertical) {
            x = e.x0;
            y = k * x + b;
        } else {
            x = (e.b - b) / (k - e.k);
            y = k * x + b;
        }
        double rate;
        if (vertical) {
            rate = (y - p1.y) / (p2.y - p1.y);
        } else {
            rate = (x - p1.x) / (p2.x - p1.x);
        }
        if (rate >= 0 && rate <= 1) {
            return new Point2D(x, y);
        }
        return null;
    }

    public List<Point2D> getInterWithRectangle(double width, double height) {
        Point2D down = new Edge(0, 0, width, 0).getIntersection(this);
        Point2D right = new Edge(width, 0, width, height).getIntersection(this);
        Point2D up = new Edge(0, height, width, height).getIntersection(this);
        Point2D left = new Edge(0, 0, 0, height).getIntersection(this);
        List<Point2D> res = new ArrayList<>();
        if (down != null) {
            res.add(down);
        }
        if (right != null) {
            res.add(right);
        }
        if (up != null) {
            res.add(up);
        }
        if (left != null) {
            res.add(left);
        }
        return res;
    }

    public Edge getEdgeInsideRectangle(double width, double height) {
        boolean p1inside = p1.isInsideRectangle(width, height);
        boolean p2inside = p2.isInsideRectangle(width, height);
        if (p1inside && p2inside) {
            return this;
        }
        if (!p1inside && !p2inside) {
            return null;
        }
        List<Point2D> points = getInterWithRectangle(width, height);
        for (Point2D p : points) {
            if (this.containsPoint(p)) {
                try {
                    if (!p1inside) {
                        return new Edge(p2, p);
                    } else {
                        return new Edge(p1, p);
                    }
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public boolean containsPoint(Point2D p) {
        if (!vertical && (p.x - p1.x) * (p.x - p2.x) <= 0 || vertical && (p.y - p1.y) * (p.y - p2.y) <= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass().equals(getClass())) {
            Edge edge = (Edge)other;
            if (p1.equals(edge.p1) && p2.equals(edge.p2) || p1.equals(edge.p2) && p2.equals(edge.p1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return p1.toString() + "------" + p2.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
