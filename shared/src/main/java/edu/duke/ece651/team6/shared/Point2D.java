package edu.duke.ece651.team6.shared;

import java.io.Serializable;

/**
 * This class represents a point on a 2D plain, has x and y coordinates.
 */
public class Point2D implements Serializable {
    public double x;
    public double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the distance to another Point2D.
     * @param other another Point2D to calculate the distance.
     * @return  the distance, in double.
     */
    public double dist(Point2D other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));        
    }

    /**
     * Tell if two Point2Ds are too close to each other.
     * @param other another Point2D.
     * @param dist  the distance of two Point2D, less than which would be considered too close.
     * @return true if too close/.
     */
    public boolean closeTo(Point2D other, double dist) {
        return dist(other) <= dist;
    }

    public boolean isInsideRectangle(double width, double height) {
        if (x > -0.000001 && x - width < 0.000001 && y > -0.000001 && y - height < 0.000001) {
            return true;
        }
        return false;
    }

    public boolean isOnRectangle(double width, double height) {
        if (Math.abs(x) < 0.000001 || Math.abs(x - width) < 0.000001 || Math.abs(y) < 0.000001 || Math.abs(y - height) < 0.000001) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass().equals(getClass())) {
            Point2D p = (Point2D)other;
            if (dist(p) < 0.00001) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
