package grids;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InfiniteGrid<T> {

    public final Map<Point, T> points = new HashMap<>();

    public enum DrawConfiguration {
        REVERSE_X, REVERSE_Y
    }

    private Set<DrawConfiguration> drawConfiguration = new HashSet<>();

    public InfiniteGrid<T> copy() {
        var result = new InfiniteGrid<T>();
        points.forEach((key, value) -> result.setValue(new Point(key), value));
        result.drawConfiguration = drawConfiguration;
        return result;
    }

    public int getWidth() {
        return getMaxX() - getMinX() + 1;
    }

    public int getHeight() {
        return getMaxY() - getMinY() + 1;
    }

    public void setDrawConfiguration(Set<DrawConfiguration> drawConfiguration) {
        this.drawConfiguration = drawConfiguration;
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

    public T getValue(Point point) {
        return points.get(point);
    }

    public boolean containsPoint(Point point) {
        return points.containsKey(point);
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

    private static final Set<Point> neighboursExcludingDiagonalsDiffSet = Set.of(
            new Point(-1, 0),
            new Point(1, 0),
            new Point(0, -1),
            new Point(0, 1));

    private static final Set<Point> neighboursIncludingDiagonalsDiffSet = Set.of(
            new Point(-1, 0),
            new Point(1, 0),
            new Point(0, -1),
            new Point(0, 1),
            new Point(-1, -1),
            new Point(1, -1),
            new Point(-1, 1),
            new Point(1, 1));

    public List<Point> getNeighbours(Point point, boolean includeDiagonals) {
        var result = includeDiagonals ? neighboursIncludingDiagonalsDiffSet : neighboursExcludingDiagonalsDiffSet;
        return result.stream().map(diff -> new Point(point.x + diff.x, point.y + diff.y))
                .filter(points::containsKey).toList();
    }

    public List<Point> getNeighbours(Point point, boolean includeDiagonals, Predicate<T> predicate) {
        return getNeighbours(point, includeDiagonals).stream()
                .filter(p -> predicate.test(getValue(p))).toList();
    }


    public List<T> getNeighbourValues(Point point, boolean includeDiagonals) {
        List<Point> neighbours = getNeighbours(point, includeDiagonals);
        return neighbours.stream().map(points::get).collect(Collectors.toList());
    }

    public void draw(Function<T, String> toStringFunction, String emptyValue) {
        Map<Integer, Set<Point>> pointMap = new HashMap<>();
        IntStream.range(getMinY(), getMaxY() + 1).forEach(y -> pointMap.put(y, new HashSet<>()));
        getAllPoints().forEach(point -> pointMap.get(point.y).add(point));
        boolean reverseY = drawConfiguration.contains(DrawConfiguration.REVERSE_Y);
        IntStream rangeY = IntStream.range(getMinY(), getMaxY() + 1);
        if (drawConfiguration.contains(DrawConfiguration.REVERSE_Y)) {
            rangeY = rangeY.boxed().sorted(Collections.reverseOrder()).mapToInt(v -> v);
        }
        rangeY.forEach(y -> {
            Set<Point> points = pointMap.get(y);
            String printRow = IntStream.range(getMinX(), getMaxX() + 1).mapToObj(x -> {
                Point point = new Point(x, y);
                return points.contains(point) ? toStringFunction.apply(getValue(point)) : emptyValue;
            }).collect(Collectors.joining());
            System.out.println(printRow);
        });
    }
}
