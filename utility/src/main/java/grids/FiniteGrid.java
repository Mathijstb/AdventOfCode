package grids;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FiniteGrid<T> {

    private final List<List<T>> points = new ArrayList<>();

    public int getWidth() {
        return points.get(0).size();
    }

    public int getHeight() {
        return points.size();
    }

    public void addRow(List<T> row) {
        assert points.size() == 0 || getWidth() == row.size();
        points.add(row);
    }

    public void draw(Function<T, String> toStringFunction) {
        for (int i = 0; i < getHeight(); i++) {
            List<String> strings = points.get(i).stream().map(toStringFunction).collect(Collectors.toList());
            System.out.println(strings);
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

    public List<T> getNeighbourValues(Point point, boolean includeDiagonals) {
        List<Point> neighbours = getNeighbours(point, includeDiagonals);
        return neighbours.stream().map(n -> points.get(n.y).get(n.x)).collect(Collectors.toList());
    }
}
