package day14;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day14 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input14.csv");

        //part 1
        var grid = getGrid(lines);
        grid.draw(p -> String.valueOf(p.character));
        moveRocks(grid, Direction.NORTH);
        System.out.println();
        System.out.println("After moving all rocks north: ");
        grid.draw(p -> String.valueOf(p.character));
        var load = getLoad(grid);
        System.out.println();
        System.out.println("Load: " + load);
        System.out.println();


        //part 2
        var grid2 = getGrid(lines);
        doCycles(grid2);
    }

    private static void doCycles(FiniteGrid<PointType> grid) {
        long numberOfCycles = 1000000000;
        Map<String, Integer> cycles = new HashMap<>();
        cycles.put(getHash(grid), 0);
        for (int currentCycle = 1; currentCycle <= numberOfCycles; currentCycle++) {
            doCycle(grid);
            var hash = getHash(grid);
            if (!cycles.containsKey(hash)) {
                cycles.put(hash, currentCycle);
            }
            else {
                var repetitionLength = currentCycle - cycles.get(hash);
                System.out.println("First repetition found at cycle: " + currentCycle + " of length: " + repetitionLength);
                var remainingCycles = (numberOfCycles - currentCycle) % repetitionLength;
                for (int i = 0; i < remainingCycles; i++) {
                    doCycle(grid);
                }
                var load = getLoad(grid);
                System.out.println("Load: " + load);
                System.out.println();
                break;
            }
        }

    }

    private static void doCycle(FiniteGrid<PointType> grid) {
        moveRocks(grid, Direction.NORTH);
        moveRocks(grid, Direction.WEST);
        moveRocks(grid, Direction.SOUTH);
        moveRocks(grid, Direction.EAST);
    }

    private static String getHash(FiniteGrid<PointType> grid) {
        return grid.getAllPoints().stream()
                .filter(p -> grid.getValue(p).equals(PointType.ROCK))
                .map(p -> String.format("(%s,%s)", p.x,p.y))
                .reduce("", (a,b) -> a + b);
    }

    private static FiniteGrid<PointType> getGrid(List<String> lines) {
        var result = new FiniteGrid<PointType>();
        lines.stream().map(line -> Chars.asList(line.toCharArray()))
                .forEach(line -> result.addRow(new ArrayList<>(line.stream().map(PointType::of).toList())));
        return result;
    }

    private static long getLoad(FiniteGrid<PointType> grid) {
        long result = 0;
        for (int y = 0; y < grid.getHeight(); y++) {
            var numberOfRocks = grid.getRow(y).stream()
                    .filter(p -> p.equals(PointType.ROCK))
                    .count();
            result += numberOfRocks * (grid.getHeight() - y);
        }
        return result;
    }

    private static void moveRocks(FiniteGrid<PointType> grid, Direction direction) {
        Stream<List<Point>> rangeStream = switch (direction) {
            case NORTH -> IntStream.range(1, grid.getHeight()).mapToObj(grid::getRowPoints);
            case EAST -> IntStream.iterate(grid.getWidth() - 1, i -> i >= 0, i -> i - 1).mapToObj(grid::getColPoints);
            case SOUTH -> IntStream.iterate(grid.getHeight() - 1, i -> i >= 0, i -> i - 1).mapToObj(grid::getRowPoints);
            case WEST -> IntStream.range(1, grid.getWidth()).mapToObj(grid::getColPoints);
        };
        rangeStream
                .forEach(range -> range.stream()
                        .filter(p -> grid.getValue(p).equals(PointType.ROCK))
                        .forEach(p -> moveToDestination(grid, new Point(p), direction))
                );
    }

    private static void moveToDestination(FiniteGrid<PointType> grid, Point start, Direction direction) {
        Optional<Point> destination = findDestination(grid, start, direction);
        if (destination.isPresent()) {
            grid.setValue(start, PointType.EMPTY);
            grid.setValue(destination.get(), PointType.ROCK);
        }
    }

    private static Optional<Point> findDestination(FiniteGrid<PointType> grid, Point start, Direction direction) {
        var shift = switch (direction) {
            case NORTH -> new Point(0, -1);
            case EAST -> new Point(1, 0);
            case SOUTH -> new Point(0, 1);
            case WEST -> new Point(-1, 0);
        };
        Function<Point, Boolean> isWithinBoundary = switch (direction) {
            case NORTH -> p -> p.y >= 0;
            case EAST -> p -> p.x < grid.getWidth();
            case SOUTH -> p -> p.y < grid.getHeight();
            case WEST -> p -> p.x >= 0;
        };
        var currentPoint = new Point(start);
        var nextPoint =  new Point(currentPoint);
        nextPoint.translate(shift.x, shift.y);
        while (isWithinBoundary.apply(nextPoint) && grid.getValue(nextPoint).equals(PointType.EMPTY)) {
            currentPoint = new Point(nextPoint);
            nextPoint.translate(shift.x, shift.y);
        }
        return currentPoint.equals(start) ? Optional.empty() : Optional.of(currentPoint);
    }
}
