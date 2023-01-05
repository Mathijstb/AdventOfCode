package day13;

import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Day13 {

    private static final Point start = new Point(1, 1);
    private static final Point goal = new Point(31, 39);

    public static void execute() {
        int designNumber = Integer.parseInt(FileReader.getFileReader().readFile("input13.csv").stream().findFirst().orElseThrow());
        var grid = determineGrid(designNumber);
        drawGrid(grid);

        //part a
        determineSmallestNumberOfSteps(grid);

        //part b
        determineNumberOfLocationsAfterNumberOfSteps(grid);
    }

    private static InfiniteGrid<Boolean> determineGrid(int designNumber) {
        var grid = new InfiniteGrid<Boolean>();
        for (int x = 0; x <= goal.x * 2; x++) {
            for (int y = 0; y <= goal.y * 2; y++) {
                var point = new Point(x, y);
                boolean isOpen = isOpen(point, designNumber);
                grid.setValue(point, isOpen);
            }
        }
        return grid;
    }

    private static boolean isOpen(Point point, int designNumber) {
        int x = point.x;
        int y = point.y;
        int value = (x * x) + (3 * x) + (2 * x * y) + y + (y * y) + designNumber;
        var numberOfBits = new BigInteger(String.valueOf(value)).bitCount();
        return (numberOfBits / 2) * 2 == numberOfBits;
    }

    private static void determineSmallestNumberOfSteps(InfiniteGrid<Boolean> grid) {
        Set<Point> points = new HashSet<>();
        points.add(start);
        int numberOfSteps = 0;
        while (!points.contains(goal)) {
            Set<Point> newPoints = new HashSet<>();
            points.forEach(point -> newPoints.addAll(grid.getNeighbours(point, false, p -> p)));
            points.addAll(newPoints);
            numberOfSteps += 1;
        }
        System.out.printf("Found goal after %d steps", numberOfSteps);
    }

    private static void determineNumberOfLocationsAfterNumberOfSteps(InfiniteGrid<Boolean> grid) {
        int maxNumberOfSteps = 50;
        Set<Point> points = new HashSet<>();
        points.add(start);
        for (int i = 0; i < maxNumberOfSteps; i++) {
            Set<Point> newPoints = new HashSet<>();
            points.forEach(point -> newPoints.addAll(grid.getNeighbours(point, false, p -> p)));
            points.addAll(newPoints);
        }
        System.out.printf("Number of locations reachable after %d steps: %d", maxNumberOfSteps, points.size());
    }

    private static void drawGrid(InfiniteGrid<Boolean> grid) {
        grid.draw(value -> value ? " " : "#", " ");
    }
}