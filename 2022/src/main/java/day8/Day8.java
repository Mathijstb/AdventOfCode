package day8;

import fileUtils.FileReader;
import grids.FiniteGrid;
import grids.NeighbourType;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Day8 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input8.csv");
        FiniteGrid<Integer> grid = new FiniteGrid<>();
        lines.forEach(line -> {
            var row = line.chars().mapToObj(c -> (char) c)
                    .map(c -> Integer.parseInt(String.valueOf(c)))
                    .toList();
            grid.addRow(row);
        });
        findVisibleTrees(grid);
        findMaxScenicScore(grid);
    }

    private static void findVisibleTrees(FiniteGrid<Integer> grid) {
        Set<Point> visibleTrees = grid.getAllPoints().stream().filter(point -> isVisible(grid, point)).collect(Collectors.toSet());
        System.out.println("Visible trees" + visibleTrees);
        System.out.println("Number of visible trees: " + visibleTrees.size());
    }

    private static boolean isVisible(FiniteGrid<Integer> grid, Point point) {
        return isVisibleFromDirection(grid, point, NeighbourType.LEFT) ||
                isVisibleFromDirection(grid, point, NeighbourType.RIGHT) ||
                isVisibleFromDirection(grid, point, NeighbourType.UP) ||
                isVisibleFromDirection(grid, point, NeighbourType.DOWN);
    }

    private static boolean isVisibleFromDirection(FiniteGrid<Integer> grid, Point point, NeighbourType direction) {
        var neighbours = grid.getAllNeighboursInDirection(point, direction);
        return neighbours.stream().allMatch(neighbour -> grid.getValue(neighbour) < grid.getValue(point));

    }

    private static void findMaxScenicScore(FiniteGrid<Integer> grid) {
        var points = grid.getAllPoints();
        var maxScenicScore = points.stream()
                .map(point -> findScenicScore(grid, point))
                .max(Comparator.comparingLong(i -> i)).orElseThrow();
        System.out.println("Max scenic score: " + maxScenicScore);
    }

    private static long findScenicScore(FiniteGrid<Integer> grid, Point point) {
        int referenceSize = grid.getValue(point);
        return getScenicScoreToDirection(grid, point, NeighbourType.LEFT, referenceSize) *
                getScenicScoreToDirection(grid, point, NeighbourType.RIGHT, referenceSize) *
                getScenicScoreToDirection(grid, point, NeighbourType.UP, referenceSize) *
                getScenicScoreToDirection(grid, point, NeighbourType.DOWN, referenceSize);
    }

    private static long getScenicScoreToDirection(FiniteGrid<Integer> grid, Point point, NeighbourType type, int referenceSize) {
        Optional<Point> neighbourOpt = grid.getNeighbour(point, type);
        if (neighbourOpt.isEmpty()) return 0;
        var neighbour = neighbourOpt.get();
        return (grid.getValue(neighbour) < referenceSize) ? 1 + getScenicScoreToDirection(grid, neighbour, type, referenceSize) : 1;
    }
}
