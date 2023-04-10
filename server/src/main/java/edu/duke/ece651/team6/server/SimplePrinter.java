package edu.duke.ece651.team6.server;

import java.util.List;
import java.util.Map;

import edu.duke.ece651.team6.shared.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class SimplePrinter extends Application {

    @Override
    public void start(Stage stage) {
        MapGenerator map = new MapGenerator(24);
        Map<Territory, Map<Territory, Double>> m = map.getDistanceMap();
        List<Point2D> points = map.getPoints();
        Pane pane = new Pane();
        pane.setPrefSize(1000, 500);
        PolygonGetter pg = new PolygonGetter();
        for (Territory t : m.keySet()) {
            Polygon p = pg.getPolygon(t);
            pane.getChildren().add(p);
        }
        for (Point2D p : points) {
            Circle point = new Circle(p.x, p.y, 5);
            point.setStroke(Color.BLACK);
            point.setStrokeWidth(1);
            pane.getChildren().add(point);
        }
        pane.setLayoutX(100);
        pane.setLayoutY(100);
        Scene scene = new Scene(pane, 1600, 1000);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
}
