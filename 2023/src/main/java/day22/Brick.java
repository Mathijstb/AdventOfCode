package day22;

import grids.Grid3D;
import lombok.Getter;

import javax.vecmath.Point3i;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Brick {

    private final char id;

    private List<Point3i> points = new ArrayList<>();

    public Brick(char id, Point3i start, Point3i end) {
        this.id = id;
        for (int x = start.x; x <= end.x; x++) {
            for (int y = start.y; y <= end.y; y++) {
                for (int z = start.z; z <= end.z; z++) {
                    points.add(new Point3i(x, y, z));
                }
            }
        }
    }

    public List<Point3i> getPoints() {
        return new ArrayList<>(points);
    }

    public void setPoints(List<Point3i> points) {
        this.points = points;
    }

    public Point3i getLowestPoint() {
        return getPoints().stream().min(Comparator.comparing(p -> p.z)).orElseThrow();
    }

    public boolean isFloating(Grid3D<PointState> grid) {
        return getPoints().stream()
                .map(point -> new Point3i(point.x, point.y, point.z - 1))
                .allMatch(pointBelow -> pointBelow.z > 0 && (!grid.containsPoint(pointBelow) || grid.getValue(pointBelow).brick().equals(this)));
    }

    public Set<Brick> getBricksBelow(Grid3D<PointState> grid) {
        return getPoints().stream()
                .map(point -> new Point3i(point.x, point.y, point.z - 1))
                .filter(p -> grid.containsPoint(p) && !grid.getValue(p).brick().equals(this))
                .map(pointBelow -> grid.getValue(pointBelow).brick())
                .collect(Collectors.toSet());
    }

    public Set<Brick> getBricksAbove(Grid3D<PointState> grid) {
        return getPoints().stream()
                .map(point -> new Point3i(point.x, point.y, point.z + 1))
                .filter(p -> grid.containsPoint(p) && !grid.getValue(p).brick().equals(this))
                .map(pointAbove -> grid.getValue(pointAbove).brick())
                .collect(Collectors.toSet());
    }
}
