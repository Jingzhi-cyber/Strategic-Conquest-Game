package edu.duke.ece651.team6.server;

import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class SimplePrinter extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MapGenerator map = new MapGenerator(24);
        Set<Edge> edges = map.getVoronoi();
        List<Point2D> points = map.getPoints();
        Pane pane = new Pane();
        pane.setPrefSize(1000, 500);
        for (Edge e : edges) {
            Line line = new Line(e.p1.x, e.p1.y, e.p2.x, e.p2.y);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1);
            pane.getChildren().add(line);
        }
        for (Point2D p : points) {
            Circle point = new Circle(p.x, p.y, 5);
            point.setStroke(Color.BLACK);
            point.setStrokeWidth(1);
            pane.getChildren().add(point);
        }
        Scene scene = new Scene(pane, 1000, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
}
