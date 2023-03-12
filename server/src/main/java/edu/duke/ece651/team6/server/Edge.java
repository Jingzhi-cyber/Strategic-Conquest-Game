package edu.duke.ece651.team6.server;

/**
 * A line on the 2D plain, consists of two Point2Ds.
 */
public class Edge {
    protected Point2D p1;
    protected Point2D p2;

    public Edge(Point2D p1, Point2D p2) {
        if (Double.compare(p1.x, p2.x) < 0 || Double.compare(p1.x, p2.x) == 0 && Double.compare(p1.y, p2.y) < 0) {
            this.p1 = p1;
            this.p2 = p2;
        } else {
            this.p1 = p2;
            this.p2 = p1;
        }
        
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
