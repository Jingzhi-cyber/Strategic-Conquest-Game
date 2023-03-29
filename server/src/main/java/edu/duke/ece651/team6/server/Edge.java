package edu.duke.ece651.team6.server;

/**
 * A line on the 2D plain, consists of two Point2Ds.
 */
public class Edge {
    protected Point2D p1;
    protected Point2D p2;

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
