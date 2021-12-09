package grids;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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

    public T getValue(Point point) {
        return points.get(point.y).get(point.x);
    }

    public void setValue(Point point, T value) {
        points.get(point.y).set(point.x, value);
    }

    public List<Point> getAllPoints() {
        return IntStream.range(0, getHeight())
                .mapToObj(row -> IntStream.range(0, getWidth()).mapToObj(col -> new Point(col, row)).collect(Collectors.toList())).flatMap(List::stream)
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
                if (newX >= 0 && newX < getWidth() && newY >= 0 && newY < getHeight()) {
                    neighbours.add(new Point(newX, newY));
                }
            }
        }
        return neighbours;
    }

    public List<T> getNeighbourValues(Point point, boolean includeDiagonals) {
        List<Point> neighbours = getNeighbours(point, includeDiagonals);
        return neighbours.stream().map(n -> points.get(n.y).get(n.x)).collect(Collectors.toList());
    }
}
