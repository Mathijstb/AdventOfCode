package grids;

import org.apache.commons.lang3.StringUtils;

import javax.vecmath.Point3i;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grid3D<T> {

    private final Map<Point3i, T> points = new HashMap<>();

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

    public void remove(Point3i point) {
        points.remove(point);
    }

    private static final Set<Point3i> neighboursExcludingDiagonalsDiffSet = Set.of(
            new Point3i(-1, 0, 0),
            new Point3i(1, 0, 0),
            new Point3i(0, -1, 0),
            new Point3i(0, 1, 0),
            new Point3i(0, 0, -1),
            new Point3i(0, 0, 1));

    private static final Set<Point3i> neighboursIncludingDiagonalsDiffSet = Set.of(
            new Point3i(-1, 0, 0),
            new Point3i(1, 0, 0),
            new Point3i(0, -1, 0),
            new Point3i(0, 1, 0),
            new Point3i(0, 0, -1),
            new Point3i(0, 0, 1),
            new Point3i(-1, -1, -1),
            new Point3i(1, -1, -1),
            new Point3i(-1, 1, -1),
            new Point3i(-1, -1, 1),
            new Point3i(1, 1, -1),
            new Point3i(-1, 1, 1),
            new Point3i(1, -1, 1),
            new Point3i(1, 1, 1));

    public List<Point3i> getNeighbours(Point3i point, boolean includeDiagonals) {
        return (includeDiagonals ? neighboursExcludingDiagonalsDiffSet : neighboursIncludingDiagonalsDiffSet).stream()
                .map(diff -> new Point3i(point.x + diff.x, point.y + diff.y, point.z + diff.z))
                .filter(points::containsKey).toList();
    }

    public enum Dimension {
        X,
        Y,
        Z
    }

    public void draw(Dimension dim1, Dimension dim2, Function<T, String> toStringFunction) {
        var dim3 = Arrays.stream(Dimension.values()).filter(dim -> !Set.of(dim1, dim2).contains(dim)).findFirst().orElseThrow();
        var dimProjection1 = getDimensionProjection(dim1);
        var dimProjection2 = getDimensionProjection(dim2);
        var dimProjection3 = getDimensionProjection(dim3);

        //Collect all points in the dim1-dim2 surface with minimal dim3
        var points = getAllPoints().stream()
                .collect(Collectors.groupingBy(p -> new Point(dimProjection1.apply(p), dimProjection2.apply(p))))
                .entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream()
                        .min(Comparator.comparingInt(dimProjection3::apply)).orElseThrow()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var minDimension1 = points.keySet().stream().min(Comparator.comparingInt(p -> p.x)).orElseThrow().x;
        var maxDimension1 = points.keySet().stream().max(Comparator.comparingInt(p -> p.x)).orElseThrow().x;
        var minDimension2 = points.keySet().stream().min(Comparator.comparingInt(p -> p.y)).orElseThrow().y;
        var maxDimension2 = points.keySet().stream().max(Comparator.comparingInt(p -> p.y)).orElseThrow().y;

        var width = maxDimension1 - minDimension1 + 1;
        var height = maxDimension2 - minDimension2 + 1;
        System.out.printf("%s%s%s%n", StringUtils.repeat(' ', width / 2), dim1, StringUtils.repeat(' ', width / 2));
        System.out.println(StringUtils.repeat('-', width));
        IntStream.range(minDimension2, maxDimension2 + 1).map(v2 -> maxDimension2 - (v2 - minDimension2)).forEach(v2 -> {
            var printRow = IntStream.range(minDimension1, maxDimension1 + 1)
                    .mapToObj(v1 -> new Point(v1, v2))
                    .map(point -> points.containsKey(point) ? toStringFunction.apply(getValue(points.get(point))) : ".")
                    .collect(Collectors.joining());
            var suffix = (v2 == height / 2 + 1) ? " | " + dim2 : " |";
            System.out.println(printRow + suffix);
        });
    }

    private Function<Point3i, Integer> getDimensionProjection(Dimension dimension) {
        return switch (dimension) {
            case X -> p -> p.x;
            case Y -> p -> p.y;
            case Z -> p -> p.z;
        };
    }


}
