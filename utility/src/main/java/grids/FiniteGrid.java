package grids;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FiniteGrid<T> {

    public static <T> FiniteGrid<T> initializeGrid(int width, int height, T defaultValue) {
        var result = new FiniteGrid<T>();
        for (int y = 0; y < height; y++) {
            List<T> row = IntStream.range(0, width).mapToObj(x -> defaultValue).collect(Collectors.toList());
            result.addRow(row);
        }
        return result;
    }

    private final List<List<T>> points = new ArrayList<>();

    public int getWidth() {
        return points.getFirst().size();
    }

    public int getHeight() {
        return points.size();
    }

    public Map<Point, T> getPointMap() {
        return getAllPoints().stream().collect(Collectors.toMap(p -> p, this::getValue));
    }

    public FiniteGrid<T> copy() {
        var result = new FiniteGrid<T>();
        points.forEach(row -> result.addRow(new ArrayList<>(row)));
        return result;
    }

    public void addRow(List<T> row) {
        assert points.isEmpty() || getWidth() == row.size();
        points.add(row);
    }

    public void insertRow(int index, List<T> row) {
        assert points.isEmpty() || getWidth() == row.size();
        points.add(index, row);
    }

    public void addColumn(List<T> column) {
        assert points.isEmpty() || getHeight() == column.size();
        for (int y = 0; y < points.size(); y++) {
            var row = points.get(y);
            row.add(column.get(y));
        }
    }

    public void insertColumn(int index, List<T> column) {
        assert points.isEmpty() || getHeight() == column.size();
        for (int y = 0; y < points.size(); y++) {
            var row = points.get(y);
            row.add(index, column.get(y));
        }
    }

    public void draw(Function<T, String> toStringFunction) {
        for (int i = 0; i < getHeight(); i++) {
            List<String> strings = points.get(i).stream().map(toStringFunction).collect(Collectors.toList());
            System.out.println(String.join("", strings));
        }
    }

    public T getValue(Point point) {
        return points.get(point.y).get(point.x);
    }

    public void setValue(Point point, T value) {
        points.get(point.y).set(point.x, value);
    }

    public void updateValue(Point point, Function<T, T> updateFunction) {
        setValue(point, updateFunction.apply(getValue(point)));
    }

    public List<Point> getAllPoints() {
        return IntStream.range(0, getHeight())
                .mapToObj(row -> IntStream.range(0, getWidth()).mapToObj(col -> new Point(col, row)).collect(Collectors.toList())).flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<Point> getPoints(Predicate<T> predicate) {
        return IntStream.range(0, getHeight())
                .mapToObj(row -> IntStream.range(0, getWidth()).mapToObj(col -> new Point(col, row)).collect(Collectors.toList())).flatMap(List::stream)
                .filter(point -> predicate.test(getValue(point)))
                .collect(Collectors.toList());
    }

    public List<Point> getRowPoints(int y) {
        return IntStream.range(0, getWidth()).mapToObj(x -> new Point(x, y)).toList();
    }

    public List<Point> getColPoints(int x) {
        return IntStream.range(0, getHeight()).mapToObj(y -> new Point(x, y)).toList();
    }

    public List<T> getRow(int y) {
        return points.get(y);
    }

    public List<T> getCol(int x) {
        return points.stream().map(row -> row.get(x)).toList();
    }

    public List<Point> getNeighbours(Point point, boolean includeDiagonals) {
        List<Point> neighbours = new ArrayList<>();
        neighbours.add(new Point(point.x-1, point.y));
        neighbours.add(new Point(point.x+1, point.y));
        neighbours.add(new Point(point.x, point.y-1));
        neighbours.add(new Point(point.x, point.y+1));
        if (includeDiagonals) {
            neighbours.add(new Point(point.x-1, point.y-1));
            neighbours.add(new Point(point.x+1, point.y-1));
            neighbours.add(new Point(point.x-1, point.y+1));
            neighbours.add(new Point(point.x+1, point.y+1));
        }
        return neighbours.stream().filter(p -> p.x >= 0 && p.y >= 0 && p.x < getWidth() && p.y < getHeight()).collect(Collectors.toList());
    }

    public List<Point> getNeighbours(Point point, boolean includeDiagonals, Predicate<T> predicate) {
        return getNeighbours(point, includeDiagonals).stream().filter(p -> predicate.test(getValue(p))).toList();
    }

    public List<T> getNeighbourValues(Point point, boolean includeDiagonals) {
        List<Point> neighbours = getNeighbours(point, includeDiagonals);
        return neighbours.stream().map(n -> points.get(n.y).get(n.x)).collect(Collectors.toList());
    }

    public Optional<Point> getPoint(Point p) {
        return  (p.x >= 0 && p.x < getWidth() && p.y >= 0 && p.y < getHeight()) ? Optional.of(p) : Optional.empty();
    }

    public boolean containsPoint(Point p) {
        return getPoint(p).isPresent();
    }

    public Optional<Point> getNeighbour(Point point, NeighbourType type) {
        return switch (type) {
            case UP -> getPoint(new Point(point.x, point.y - 1));
            case DOWN -> getPoint(new Point(point.x, point.y + 1));
            case LEFT -> getPoint(new Point(point.x - 1, point.y));
            case RIGHT -> getPoint(new Point(point.x + 1, point.y));
        };
    }

    public List<Point> getAllNeighboursInDirection(Point point, NeighbourType type) {
        var neighbour = getNeighbour(point, type);
        if (neighbour.isEmpty()) {
            return Collections.emptyList();
        }
        else {
            var result = new ArrayList<Point>();
            result.add(neighbour.get());
            result.addAll(getAllNeighboursInDirection(neighbour.get(), type));
            return result;
        }

    }
}
