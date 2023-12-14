package day24;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 {

    private static Point start;
    private static Point goal;

    private static Set<Point> accessiblePoints;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input24.csv");
        var grid = readGrid(lines);
        findFastestPaths(grid);
    }

    private static FiniteGrid<PointType> readGrid(List<String> lines) {
        FiniteGrid<PointType> grid = new FiniteGrid<>();
        lines.forEach(line -> grid.addRow(line.chars().mapToObj(c -> PointType.of((char) c)).collect(Collectors.toList())));
        start = grid.getRowPoints(0).stream().filter(p -> grid.getValue(p).equals(PointType.AIR)).findFirst().orElseThrow();
        goal = grid.getRowPoints(grid.getHeight() - 1).stream().filter(p -> grid.getValue(p).equals(PointType.AIR)).findFirst().orElseThrow();
        grid.setValue(start, PointType.START);
        grid.setValue(goal, PointType.GOAL);
        return grid;
    }

    private static void findFastestPaths(FiniteGrid<PointType> grid) {
        Set<Blizzard> blizzards = new HashSet<>();
        grid.getPoints(pointType -> Set.of(PointType.BLIZZARD_LEFT, PointType.BLIZZARD_RIGHT, PointType.BLIZZARD_UP, PointType.BLIZZARD_DOWN).contains(pointType))
                .forEach(point -> blizzards.add(new Blizzard(point, grid.getValue(point))));
        drawGrid(grid);
        accessiblePoints = grid.getAllPoints().stream()
                .filter(p -> Set.of(PointType.BLIZZARD_LEFT, PointType.BLIZZARD_RIGHT, PointType.BLIZZARD_UP, PointType.BLIZZARD_DOWN, PointType.AIR, PointType.GOAL, PointType.START).contains(grid.getValue(p)))
                .collect(Collectors.toSet());
        int numberOfMinutes1 = findFastestPath2(grid, blizzards, start, goal);
        int numberOfMinutes2 = findFastestPath2(grid, blizzards, goal, start);
        int numberOfMinutes3 = findFastestPath2(grid, blizzards, start, goal);
        int total = numberOfMinutes1 + numberOfMinutes2 + numberOfMinutes3;
        System.out.printf("Reached final goal in %s + %s + %s = %s minutes!", numberOfMinutes1, numberOfMinutes2, numberOfMinutes3, total);
    }

    private static int findFastestPath2(FiniteGrid<PointType> grid, Set<Blizzard> blizzards, Point start, Point goal) {
        Set<Point> currentPoints = new HashSet<>();
        Set<Point> newPoints = new HashSet<>();
        currentPoints.add(start);
        for (int i = 0; i < 1000; i++) {
            moveBlizzards(grid, blizzards);
            drawGrid(grid);
            Set<Point> blizzardLocations = blizzards.stream().map(blizzard -> blizzard.point).collect(Collectors.toSet());
            for(Point point : currentPoints) {
                newPoints.addAll(getNeighbours(point).stream().filter(n -> !blizzardLocations.contains(n)).toList());
            }
            if (newPoints.contains(goal)) {
                System.out.printf("Reached goal in %s minutes!", i + 1);
                System.out.println();
                return (i + 1);
            }
            currentPoints = newPoints;
            newPoints = new HashSet<>();
        }
        return 9999;
    }

    private static void moveBlizzards(FiniteGrid<PointType> grid, Set<Blizzard> blizzards) {
        blizzards.forEach(blizzard -> {
            grid.setValue(blizzard.point, PointType.AIR);
            switch (blizzard.pointType) {
                case BLIZZARD_LEFT -> blizzard.point.translate(-1, 0);
                case BLIZZARD_RIGHT -> blizzard.point.translate(1, 0);
                case BLIZZARD_UP -> blizzard.point.translate(0, -1);
                case BLIZZARD_DOWN -> blizzard.point.translate(0, 1);
            }
            if (blizzard.point.x <= 0) {
                blizzard.point = new Point(grid.getWidth() - 2, blizzard.point.y);
            }
            else if (blizzard.point.x >= grid.getWidth() - 1) {
                blizzard.point = new Point(1, blizzard.point.y);
            }
            else if (blizzard.point.y <= 0) {
                blizzard.point = new Point(blizzard.point.x, grid.getHeight() - 2);
            }
            else if(blizzard.point.y >= grid.getHeight() - 1) {
                blizzard.point = new Point(blizzard.point.x, 1);
            }
            grid.setValue(blizzard.point, blizzard.pointType);
        });
    }

    private static Set<Point> getNeighbours(Point point) {
        return Stream.of(point, new Point(point.x - 1, point.y), new Point(point.x + 1, point.y), new Point(point.x, point.y - 1), new Point(point.x, point.y + 1))
                .filter(p -> accessiblePoints.contains(p)).collect(Collectors.toSet());
    }

    private static void drawGrid(FiniteGrid<PointType> grid) {
        grid.draw(pointType -> switch (pointType) {
            case WALL -> "#";
            case AIR -> ".";
            case START -> "S";
            case GOAL -> "G";
            case BLIZZARD_LEFT -> "<";
            case BLIZZARD_RIGHT -> ">";
            case BLIZZARD_UP -> "^";
            case BLIZZARD_DOWN -> "v";
        });
        System.out.println();
    }

}