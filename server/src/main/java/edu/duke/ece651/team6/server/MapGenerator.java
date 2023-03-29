package edu.duke.ece651.team6.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.duke.ece651.team6.shared.Territory;

/**
 * A map generator, can generator a game map with any number (no greater than 26) of territories randomly.
 * User can use custom names to name the territory.
 */
public class MapGenerator {
    private Set<String> names;
    private List<Point2D> points;
    private final int n;
    private List<Triangle> triangles;
    private Set<Edge> connections;
    private Set<Edge> voronoi;
    
    public MapGenerator(int n) {
        names = new HashSet<>();
        this.n = n;
        generateMap();
    }

    public MapGenerator(int n, String... names) {
        this.names = new HashSet<>();
        this.n = n;
        for (String name : names) {
            this.names.add(name);
        }
        generateMap();
    }

    /**
     * Generate the map, based on Delaunay triangulation incremental algorithm.
     */
    private void generateMap() {
        Set<Point2D> points = new HashSet<>();
        Random random = new Random();
        // Generate points randomly, distance between any two points are guaranteed to be greater than 100.
        for (int i = 0; i < n; i++) {
            int adder = 0;
            while (true) {
                if (adder++ > 100) {
                    throw new RuntimeException("Cannot generate the map!");
                }
                int x = random.nextInt(960) + 20;
                int y = random.nextInt(460) + 20;
                Point2D p = new Point2D(x, y);
                boolean closeTo = false;
                for (Point2D point : points) {
                    if (point.closeTo(p, 50.0)) {
                        closeTo = true;
                        break;
                    }
                }
                if (!closeTo) {
                    points.add(p);
                    break;
                }
            }
        }
        this.points = new ArrayList<>(points);
        this.points.sort((Point2D a, Point2D b) -> (Double.compare(a.x, b.x)));
        triangles = new LinkedList<>();
        Triangle superTri = new Triangle(new Point2D(-1, -1), new Point2D(4002, -1), new Point2D(-1, 2001));
        List<Triangle> tempTri = new LinkedList<>();
        tempTri.add(superTri);
        Set<Edge> buffer = new HashSet<>(); 
        // Delaunay triangulation incremental algorithm
        for (Point2D p : this.points) {
            buffer.clear();
            ListIterator<Triangle> it =  tempTri.listIterator();
            while (it.hasNext()) {
                Triangle t = it.next();
                if (t.onTheLeft(p)) {
                    triangles.add(t);
                    it.remove();
                } else if (t.circleContains(p)) {
                    Set<Edge> set = t.getEdges();
                    for (Edge e : set) {
                        if (buffer.contains(e)) {
                            buffer.remove(e);
                        } else {
                            buffer.add(e);
                        }
                    }
                    it.remove();
                }
            }
            for (Edge edge : buffer) {
                tempTri.add(new Triangle(p, edge));
            }
        }
        triangles.addAll(tempTri);
        ListIterator<Triangle> it = triangles.listIterator();
        while (it.hasNext()) {
            Triangle t = it.next();
            if (t.overlaps(superTri)) {
                it.remove();
            }
        }
        connections = new HashSet<>();
        for (Triangle t : triangles) {
            connections.addAll(t.getEdges());
        }
        generateVoronoi();
    }

    /**
     * Get the adjacent list of the map.
     * @return
     */
    public Map<Territory, Set<Territory>> getTheMap() {
        Map<Territory, Set<Territory>> map = new HashMap<>();
        List<Territory> list = new ArrayList<>();
        Iterator<String> it = names.iterator();
        char c = 'A';
        for (int i = 0; i < n; i++) {
            String str;
            if (it.hasNext()) {
                str = it.next();
            } else {
                str = String.valueOf(c);
                c++;
            }
            Territory t = new Territory(str);
            map.put(t, new HashSet<>());
            list.add(t);
        }
        for (Edge edge : connections) {
            int index1 = points.indexOf(edge.p1);
            int index2 = points.indexOf(edge.p2);
            map.get(list.get(index1)).add(list.get(index2));
            map.get(list.get(index2)).add(list.get(index1));
        }
        return map;
    }

    /**
     * Get the map with distance between any two adjacent Territories.
     * @return the distance map.
     */
    public Map<Territory, Map<Territory, Double>> getDistanceMap() {
        Map<Territory, Map<Territory, Double>> map = new HashMap<>();
        List<Territory> list = new ArrayList<>();
        Iterator<String> it = names.iterator();
        char c = 'A';
        for (int i = 0; i < n; i++) {
            String str;
            if (it.hasNext()) {
                str = it.next();
            } else {
                str = String.valueOf(c);
                c++;
            }
            Territory t = new Territory(str);
            map.put(t, new HashMap<>());
            list.add(t);
        }
        for (Edge edge : connections) {
            int index1 = points.indexOf(edge.p1);
            int index2 = points.indexOf(edge.p2);
            double distance = edge.p1.dist(edge.p2);
            map.get(list.get(index1)).put(list.get(index2), distance);
            map.get(list.get(index2)).put(list.get(index1), distance);
        }
        return map;
    }

    /**
     * Get voronoi map.
     */
    private void generateVoronoi() {
        voronoi = new HashSet<>();
        for (Edge e : connections) {
            Triangle t1 = null;
            Triangle t2 = null;
            for (Triangle t : triangles) {
                if (t.overlaps(e.p1) && t.overlaps(e.p2)) {
                    if (t1 == null) {
                        t1 = t;
                    } else {
                        t2 = t;
                        break;
                    }
                }
            }
            if (t1 != null && t2 != null) {
                voronoi.add(new Edge(t1.c, t2.c));
            } else if (t1 != null) {
                Edge ve = t1.getVoronoiEdge(e);
                if (ve != null) {
                    voronoi.add(ve);
                }
            }
        }
    }
    
    public List<Point2D> getPoints() {
        return points;
    }

    public Set<Edge> getVoronoi() {
        return voronoi;
    }

}
