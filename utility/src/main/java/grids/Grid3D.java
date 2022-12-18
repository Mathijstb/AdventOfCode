package grids;

import javax.vecmath.Point3i;
import java.util.*;

public class Grid3D<T> {

    public final Map<Point3i, T> points = new HashMap<>();

    public T getValue(Point3i point) {
        return points.get(point);
    }

    public boolean containsPoint(Point3i point) {
        return points.containsKey(point);

    }

    public Set<Point3i> getAllPoints() {
        return points.keySet();
    }

    public int getMinX() {
        return points.keySet().stream().map(p -> p.x).mapToInt(v -> v).min().orElseThrow();
    }

    public int getMaxX() {
        return points.keySet().stream().map(p -> p.x).mapToInt(v -> v).max().orElseThrow();
    }

    public int getMinY() {
        return points.keySet().stream().map(p -> p.y).mapToInt(v -> v).min().orElseThrow();
    }

    public int getMaxY() {
        return points.keySet().stream().map(p -> p.y).mapToInt(v -> v).max().orElseThrow();
    }

    public int getMinZ() {
        return points.keySet().stream().map(p -> p.z).mapToInt(v -> v).min().orElseThrow();
    }

    public int getMaxZ() {
        return points.keySet().stream().map(p -> p.z).mapToInt(v -> v).max().orElseThrow();
    }

    public void setValue(Point3i point, T value) {
        points.put(point, value);
    }

    public Set<Point3i> getNeighbours(Point3i point, boolean includeDiagonals) {
        Set<Point3i> neighbours = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int newX = point.x + dx;
                    int newY = point.y + dy;
                    int newZ = point.z + dz;

                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (!includeDiagonals) {
                        if ((dx != 0 && dy != 0) || (dx != 0 && dz != 0) || (dy != 0 && dz != 0)) continue;
                    }

                    var newPoint = new Point3i(newX, newY, newZ);
                    if (points.containsKey(newPoint)) neighbours.add(newPoint);
                }
            }
        }
        return neighbours;
    }


}
