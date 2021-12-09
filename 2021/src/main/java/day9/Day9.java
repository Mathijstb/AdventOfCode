package day9;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day9 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input9.csv");

        FiniteGrid<Integer> grid = getGrid(input);
        printSumOfRiskLevels(grid);

        List<List<Point>> basins = findBasins(grid);
        printMultiplicationOf3LargestSizes(basins);
    }

    private static FiniteGrid<Integer> getGrid(List<String> input) {
        FiniteGrid<Integer> grid = new FiniteGrid<>();
        input.forEach(line -> grid.addRow(Arrays.stream(line.split("")).map(Integer::parseInt).collect(Collectors.toList())));
        return grid;
    }

    private static void printSumOfRiskLevels(FiniteGrid<Integer> grid) {
        List<Point> allPoints = grid.getAllPoints();
        List<Point> lowPoints = allPoints.stream().filter(point -> isLowPoint(grid, point)).collect(Collectors.toList());
        long sumOfRiskLevels = lowPoints.stream().map(p -> grid.getValue(p) + 1).mapToLong(v -> v).sum();
        System.out.println("Sum of risk levels: " + sumOfRiskLevels);
    }

    private static boolean isLowPoint(FiniteGrid<Integer> grid, Point point) {
        return grid.getNeighbours(point, false).stream().allMatch(n -> grid.getValue(n) > grid.getValue(point));
    }

    private static List<List<Point>> findBasins(FiniteGrid<Integer> grid) {
        List<Point> allPoints = grid.getAllPoints();
        List<Point> lowPoints = allPoints.stream().filter(point -> isLowPoint(grid, point)).collect(Collectors.toList());

        return lowPoints.stream().map(lowPoint -> {
            List<Point> basin = new ArrayList<>();
            fillBasin(grid, basin, lowPoint);
            return basin;
        }).collect(Collectors.toList());
    }

    private static void fillBasin(FiniteGrid<Integer> grid, List<Point> basin, Point point) {
        basin.add(point);
        grid.getNeighbours(point, false).stream()
                .filter(n -> grid.getValue(n) < 9)
                .filter(n -> !basin.contains(n))
                .forEach(n -> fillBasin(grid, basin, n));
    }

    private static void printMultiplicationOf3LargestSizes(List<List<Point>> basins) {
        List<Integer> sortedSizes = basins.stream().map(List::size).sorted().collect(Collectors.toList());
        List<Integer> largestBasins = sortedSizes.subList(sortedSizes.size() - 3, sortedSizes.size());
        long multiplication = largestBasins.stream().reduce(1, (s1, s2) -> s1 * s2);
        System.out.println("Multiplication of basin sizes: " + multiplication);
    }
}
