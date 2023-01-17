package day24;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day24 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input24.csv");
        var grid = readGrid(lists);
        drawGrid(grid);

        var goalMap = findGoalMap(grid);
        var distances = findDistances(grid, goalMap);
        findShortestPath(distances, goalMap);
        findShortestPath2(distances, goalMap);
    }

    private static FiniteGrid<GridPoint> readGrid(List<String> lists) {
        FiniteGrid<GridPoint> grid = new FiniteGrid<>();
        for (String list : lists) {
            List<GridPoint> row = new ArrayList<>();
            list.chars().forEach(c -> {
                switch (c) {
                    case '#' -> row.add(new GridPoint(PointType.WALL, Optional.empty()));
                    case '.' -> row.add(new GridPoint(PointType.EMPTY, Optional.empty()));
                    default -> row.add(new GridPoint(PointType.EMPTY, Optional.of(Character.getNumericValue(c))));
                }
            });
            grid.addRow(row);
        }
        return grid;
    }

    private static void drawGrid(FiniteGrid<GridPoint> grid) {
        grid.draw(gridPoint -> switch (gridPoint.pointType()) {
            case WALL -> "#";
            case EMPTY -> gridPoint.goalIndex().isEmpty() ? "." : String.valueOf(gridPoint.goalIndex().get());
        });
    }

    private static Map<Integer, Point> findGoalMap(FiniteGrid<GridPoint> grid) {
        Map<Integer, Point> goalMap = new HashMap<>();
        grid.getAllPoints().stream()
                .filter(point -> grid.getValue(point).goalIndex().isPresent())
                .forEach(point -> goalMap.put(grid.getValue(point).goalIndex().orElseThrow(), point));
        return goalMap;
    }

    private static Map<Integer, Map<Integer, Integer>> findDistances(FiniteGrid<GridPoint> grid, Map<Integer, Point> goalMap) {
        Map<Integer, Map<Integer, Integer>> distances = new HashMap<>();
        goalMap.forEach((key, value) -> distances.put(key, findShortestPathsFromPoint(grid, value, goalMap)));
        return distances;
    }

    private static Map<Integer, Integer> findShortestPathsFromPoint(FiniteGrid<GridPoint> grid, Point start, Map<Integer, Point> goalMap) {
        Map<Integer, Integer> result = new HashMap<>();
        Set<Point> visited = new HashSet<>();
        visited.add(start);
        Set<Point> current = new HashSet<>(visited);
        int numberOfSteps = 0;
        while (current.size() > 0) {
            current = current.stream()
                    .map(p -> grid.getNeighbours(p, false, n -> n.pointType().equals(PointType.EMPTY)))
                    .flatMap(List::stream)
                    .filter(p -> !visited.contains(p))
                    .collect(Collectors.toSet());
            numberOfSteps += 1;
            for(Map.Entry<Integer, Point> entry : goalMap.entrySet()) {
                if (current.contains(entry.getValue())) {
                    result.put(entry.getKey(), numberOfSteps);
                }
            }
            visited.addAll(current);
        }
        return result;
    }

    private static void findShortestPath(Map<Integer, Map<Integer, Integer>> distances, Map<Integer, Point> goalMap) {
        Set<Integer> remainingIndices = new HashSet<>(goalMap.keySet());
        remainingIndices.remove(0);
        int shortestPath = findRemainingDistance(0, remainingIndices, distances);
        System.out.println("Shortest path: " + shortestPath);
    }

    private static int findRemainingDistance(int currentIndex, Set<Integer> remainingIndices, Map<Integer, Map<Integer, Integer>> distances) {
        if (remainingIndices.size() == 0) return 0;
        return remainingIndices.stream().map(nextIndex -> {
            var distance = distances.get(currentIndex).get(nextIndex);
            Set<Integer> newRemainingIndices = new HashSet<>(remainingIndices);
            newRemainingIndices.remove(nextIndex);
            return distance + findRemainingDistance(nextIndex, newRemainingIndices, distances);
        }).min(Comparator.comparingInt(i -> i)).orElseThrow();
    }

    private static void findShortestPath2(Map<Integer, Map<Integer, Integer>> distances, Map<Integer, Point> goalMap) {
        Set<Integer> remainingIndices = new HashSet<>(goalMap.keySet());
        remainingIndices.remove(0);
        int shortestPath = findRemainingDistance2(0, remainingIndices, distances);
        System.out.println("Shortest path: " + shortestPath);
    }

    private static int findRemainingDistance2(int currentIndex, Set<Integer> remainingIndices, Map<Integer, Map<Integer, Integer>> distances) {
        if (remainingIndices.size() == 0) {
            return distances.get(currentIndex).get(0);
        }
        return remainingIndices.stream().map(nextIndex -> {
            var distance = distances.get(currentIndex).get(nextIndex);
            Set<Integer> newRemainingIndices = new HashSet<>(remainingIndices);
            newRemainingIndices.remove(nextIndex);
            return distance + findRemainingDistance2(nextIndex, newRemainingIndices, distances);
        }).min(Comparator.comparingInt(i -> i)).orElseThrow();
    }
}