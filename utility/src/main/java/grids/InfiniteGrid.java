package grids;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InfiniteGrid<T> {

    private final Map<Point, T> points = new HashMap<>();

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

    public T getValue(Point point) {
        return points.get(point);
    }

    public void setValue(Point point, T value) {
        points.put(point, value);
    }

    public void clearValue(Point point) { points.remove(point); }

    public void updateValue(Point point, Function<T, T> updateFunction) {
        setValue(point, updateFunction.apply(getValue(point)));
    }

    public Set<Point> getAllPoints() {
        return points.keySet();
    }

    public List<Point> getPoints(Predicate<T> predicate) {
        return getAllPoints().stream().filter(point -> predicate.test(getValue(point)))
                .collect(Collectors.toList());
    }

    public List<Point> getNeighbours(Point point, boolean includeDiagonals) {
        List<Point> neighbours = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int newX = point.x + dx;
                int newY = point.y + dy;
                if (dx == 0 && dy == 0) continue;
                if (dx != 0 && dy != 0 && !includeDiagonals) continue;
                if (newX >= getMinX() && newX <= getMaxX() && newY >= getMinY() && newY <= getMaxY()) {
                    Point newPoint = new Point(newX, newY);
                    if (points.containsKey(newPoint)) neighbours.add(newPoint);
                }
            }
        }
        return neighbours;
    }

    public List<T> getNeighbourValues(Point point, boolean includeDiagonals) {
        List<Point> neighbours = getNeighbours(point, includeDiagonals);
        return neighbours.stream().map(points::get).collect(Collectors.toList());
    }

    public void draw(Function<T, String> toStringFunction, String emptyValue) {
        Map<Integer, Set<Point>> pointMap = new HashMap<>();
        IntStream.range(getMinY(), getMaxY() + 1).forEach(y -> pointMap.put(y, new HashSet<>()));
        getAllPoints().forEach(point -> pointMap.get(point.y).add(point));
        for (int y = getMinY(); y <= getMaxY(); y++) {
            int yCoord = y;
            Set<Point> points = pointMap.get(yCoord);
            String printRow = IntStream.range(getMinX(), getMaxX() + 1).mapToObj(x -> {
                Point point = new Point(x, yCoord);
                return points.contains(point) ? toStringFunction.apply(getValue(point)) : emptyValue;
            }).collect(Collectors.joining());
            System.out.println(printRow);
        }
    }
}