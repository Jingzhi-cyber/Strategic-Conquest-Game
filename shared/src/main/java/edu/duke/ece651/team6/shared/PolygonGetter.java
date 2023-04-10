package edu.duke.ece651.team6.shared;

import edu.duke.ece651.team6.shared.Point2D;
import edu.duke.ece651.team6.shared.Territory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can manage all the polygons in a single game.
 * Please instantiate this class whenever a new game starts.
 */
public class PolygonGetter {

    private final Map<Territory, Polygon> map;

    public PolygonGetter() {
        map = new HashMap<>();
    }

    /**
     * Get the corresponding polygon to the given territory.
     * @param t The territory
     * @return the Polygon
     */
    public Polygon getPolygon(Territory t) {
        if (map.containsKey(t)) {
            return map.get(t);
        }
        List<Point2D> points = t.getPoints();
        Polygon polygon = new Polygon();
        for (Point2D p : points) {
            polygon.getPoints().add(p.x);
            polygon.getPoints().add(p.y);
        }
        int[] color = t.getColor();
        polygon.setFill(Color.rgb(color[0], color[1], color[2]));
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(2);
        map.put(t, polygon);
        return polygon;
    }

    public void updatePolygon(Territory t) {
        if (!map.containsKey(t)) {
            return;
        }
        int[] color = t.getColor();
        map.get(t).setFill(Color.rgb(color[0], color[1], color[2]));
    }
}
