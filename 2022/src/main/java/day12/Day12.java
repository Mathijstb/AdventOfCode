package day12;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 {

    private static final int MAX_VALUE = 999999;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input12.csv");
        var heightMap = readHeightMap(lines);
        var finish = heightMap.getAllPoints().stream().filter(p -> heightMap.getValue(p).type().equals(PointType.FINISH)).findFirst().orElseThrow();

        //part a
        var stateMap = findShortestPaths(heightMap, finish);
        var start = heightMap.getAllPoints().stream().filter(p -> heightMap.getValue(p).type().equals(PointType.START)).findFirst().orElseThrow();
        System.out.println("Distance to finish: " + stateMap.getValue(start).distance());

        //part b
        var lowestPoints = heightMap.getAllPoints().stream().filter(p -> heightMap.getValue(p).height() == 0).collect(Collectors.toSet());
        var shortestDistance = lowestPoints.stream().map(p -> stateMap.getValue(p).distance()).min(Comparator.comparingInt(x -> x)).stream().findFirst().orElseThrow();

        System.out.println("Distance of lowest point with shortest distance: " + shortestDistance);
    }

    private static FiniteGrid<Location> readHeightMap(List<String> lines) {
        var heightMap = new FiniteGrid<Location>();
        lines.forEach(line -> {
            var row = line.chars()
                    .mapToObj(c -> switch (c) {
                        case 'S' -> new Location(PointType.START, 0);
                        case 'E' -> new Location(PointType.FINISH, 25);
                        default -> new Location(PointType.EMPTY, c - (int) 'a');
                    }).toList();
            heightMap.addRow(row);
        });
        return heightMap;
    }

    private static void drawHeightMap(FiniteGrid<Location> grid) {
        grid.draw(s -> String.format("%3s", s.height()));
    }

    private static FiniteGrid<LocationState> findShortestPaths(FiniteGrid<Location> heightMap, Point finish) {
        var stateMap = FiniteGrid.initializeGrid(heightMap.getWidth(), heightMap.getHeight(), new LocationState(MAX_VALUE));
        stateMap.setValue(finish, new LocationState(0));

        var remaining = new HashSet<>(heightMap.getAllPoints());
        Set<Point> currentPoints = new HashSet<>();
        currentPoints.add(finish);
        var distance = 0;
        while (!currentPoints.isEmpty()) {
            remaining.removeAll(currentPoints);
            distance += 1;
            var neighbours = getNeighbours(currentPoints, remaining, heightMap);
            for (Point neighbour : neighbours) {
                stateMap.setValue(neighbour, new LocationState(distance));
            }
            currentPoints = neighbours;
        }
        return stateMap;
    }

    private static Set<Point> getNeighbours(Set<Point> currentPoints, Set<Point> remaining, FiniteGrid<Location> heightMap) {
        return currentPoints.stream()
                .map(current -> heightMap.getNeighbours(current, false).stream()
                            .filter(remaining::contains)
                            .filter(p -> heightMap.getValue(current).height() <= heightMap.getValue(p).height() + 1)
                            .collect(Collectors.toSet()))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    private static int getDistance(Point point1, Point point2, FiniteGrid<Location> heightMap) {
        var height1 = heightMap.getValue(point1).height();
        var height2 = heightMap.getValue(point2).height();
        return (height2 > height1 + 1) ? MAX_VALUE : 1;
    }

}
