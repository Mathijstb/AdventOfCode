package day14;

import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class
Day14a {

    private static final Point SOURCE = new Point(500, 0);
    public static void execute() {
        var paths = readPaths(FileReader.getFileReader().readFile("input14.csv"));
        var grid = setupGrid(paths);
        drawGrid(grid);
        flowSand(grid);
    }

    private static List<Path> readPaths(List<String> lines) {
        return lines.stream()
                .map(line -> new Path(Arrays.stream(line.split(" -> "))
                        .map(s -> {
                            var coords = s.split(",");
                            return new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                        }).toList())
                ).toList();
    }

    private static InfiniteGrid<PointType> setupGrid(List<Path> paths) {
        var grid = new InfiniteGrid<PointType>();
        //Add paths
        paths.forEach(path -> {
            var points = path.points();
            for (int i = 0; i < points.size(); i++) {
                var start = points.get(i);
                var end = (i + 1 ) < points.size() ? points.get(i + 1) : start;
                addLine(start, end, grid);
            }
        });
        //Add source
        grid.setValue(SOURCE, PointType.SOURCE);
        //Add air
        int minX = grid.getMinX();
        int maxX = grid.getMaxX();
        int minY = grid.getMinY();
        int maxY = grid.getMaxY();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                var point = new Point(x, y);
                if (!grid.points.containsKey(point)) {
                    grid.setValue(point, PointType.AIR);
                }
            }
        }
        //Add abyss
        for (int x = minX - 1 ; x <= maxX + 1; x++) {
            grid.setValue(new Point(x, minY - 1), PointType.ABYSS);
            grid.setValue(new Point(x, maxY + 1), PointType.ABYSS);
        }
        for (int y = minY - 1 ; y <= maxY + 1; y++) {
            grid.setValue(new Point(minX - 1, y), PointType.ABYSS);
            grid.setValue(new Point(maxX + 1, y), PointType.ABYSS);
        }
        return grid;
    }

    private static void addLine(Point start, Point end, InfiniteGrid<PointType> grid) {
        for (int x = start.x; x <= end.x; x++) {
            grid.setValue(new Point(x, start.y), PointType.ROCK);
        }
        for (int x = start.x; x >= end.x; x--) {
            grid.setValue(new Point(x, start.y), PointType.ROCK);
        }
        for (int y = start.y; y <= end.y; y++) {
            grid.setValue(new Point(start.x, y), PointType.ROCK);
        }
        for (int y = start.y; y >= end.y; y--) {
            grid.setValue(new Point(start.x, y), PointType.ROCK);
        }
    }

    private static void flowSand(InfiniteGrid<PointType> grid) {
        var endOfAbyssFound = false;
        var numberOfBlockedSandUnits = 0;
        while (!endOfAbyssFound) {
            var previous = new Point(SOURCE);
            var oldPreviousType = grid.getValue(SOURCE);
            while (true) {
                var nextOpt = getNextMove(grid, previous);
                if (nextOpt.isEmpty()) {
                    if (oldPreviousType.equals(PointType.ABYSS)) {
                        endOfAbyssFound = true;
                        grid.setValue(previous, oldPreviousType);
                        //drawGrid(grid);
                        break;
                    }
                    numberOfBlockedSandUnits += 1;
                    break;
                }
                var next = nextOpt.get();
                grid.setValue(previous, oldPreviousType);
                oldPreviousType = grid.getValue(next);
                grid.setValue(next, PointType.SAND);
                previous = next;
                //drawGrid(grid);
            }
        }
        drawGrid(grid);
        System.out.println("Number of blocked sand units: " + numberOfBlockedSandUnits);
    }

    private static Optional<Point> getNextMove(InfiniteGrid<PointType> grid, Point current) {
        Set<PointType> blockingTypes = Set.of(PointType.ROCK, PointType.SAND);
        var option1 = new Point(current.x, current.y + 1);
        if (grid.containsPoint(option1) && !blockingTypes.contains(grid.getValue(option1))) return Optional.of(option1);
        var option2 = new Point(current.x - 1, current.y + 1);
        if (grid.containsPoint(option2) && !blockingTypes.contains(grid.getValue(option2))) return Optional.of(option2);
        var option3 = new Point(current.x + 1, current.y + 1);
        if (grid.containsPoint(option3) && !blockingTypes.contains(grid.getValue(option3))) return Optional.of(option3);
        return Optional.empty();
    }

    private static void drawGrid(InfiniteGrid<PointType> grid) {
        grid.draw(pointType -> switch (pointType) {
            case ROCK -> "#";
            case AIR -> ".";
            case SOURCE -> "+";
            case SAND -> "o";
            case ABYSS -> ".";
        }, " ");
        System.out.println();
    }
}
