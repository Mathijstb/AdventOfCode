package day14;

import drawUtils.DrawGrid;
import drawUtils.Images;
import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Day14b {

    private static final Point SOURCE = new Point(500, 0);
    public static void execute() {
        var paths = readPaths(FileReader.getFileReader().readFile("input14.csv"));
        var grid = setupGrid(paths);

        //For video -----------------------------------------------------------
        Map<PointType, Consumer<DrawGrid.DrawParameters>> paintMap = new HashMap<>();
        paintMap.put(PointType.SAND, (dp) -> {
            dp.getG2d().setColor(Color.ORANGE);
            dp.getG2d().fillOval(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
        });
        paintMap.put(PointType.ROCK, (dp) -> {
            dp.getG2d().setColor(Color.GRAY);
            dp.getG2d().fillRect(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
        });
        paintMap.put(PointType.AIR, (dp) -> {
            dp.getG2d().setColor(Color.WHITE);
            dp.getG2d().fillRect(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
        });
        var drawGrid = new DrawGrid<>("Sand", PointType.class, grid.points, PointType.AIR, paintMap);
        //---------------------------------------------------------------------

        flowSand(grid, drawGrid);
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
        //Add bottom
        var bottomY = grid.getMaxY() + 2;
        var bottomStartX = SOURCE.x - (bottomY - SOURCE.y);
        var bottomEndX = SOURCE.x + (bottomY - SOURCE.y);
        for (int x = bottomStartX; x <= bottomEndX + 1; x++) {
            grid.setValue(new Point(x, bottomY), PointType.ROCK);
        }
        //Add air
        for (int x = bottomStartX; x <= bottomEndX; x++) {
            for (int y = SOURCE.y; y <= bottomY; y++) {
                if (!grid.containsPoint(new Point(x, y))) {
                    grid.setValue(new Point(x, y), PointType.AIR);
                }
            }
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

    private static void flowSand(InfiniteGrid<PointType> grid, DrawGrid<PointType> drawGrid) {
        drawGrid.repaint(1000);
        var fullOfSandUnits = false;
        var numberOfBlockedSandUnits = 0;
        while (!fullOfSandUnits) {
            var previous = new Point(SOURCE);
            var oldPreviousType = grid.getValue(SOURCE);
            while (true) {
                var nextOpt = getNextMove(grid, previous);
                if (nextOpt.isEmpty()) {
                    if (oldPreviousType.equals(PointType.SOURCE)) {
                        fullOfSandUnits = true;
                        grid.setValue(SOURCE, PointType.SAND);
                    }
                    numberOfBlockedSandUnits += 1;

                    //For video ------------------------------------------
                    if ((numberOfBlockedSandUnits >= 300) && (numberOfBlockedSandUnits <= 1000)) {
                        drawGrid.repaint(10);
                    }
                    if ((numberOfBlockedSandUnits > 1000) && (numberOfBlockedSandUnits <= 5000)) {
                        drawGrid.repaint(2);
                    }
                    if ((numberOfBlockedSandUnits >= 5000)) {
                        drawGrid.repaint(1);
                    }
                    //----------------------------------------------------
                    break;
                }
                var next = nextOpt.get();
                grid.setValue(previous, oldPreviousType);
                oldPreviousType = grid.getValue(next);
                grid.setValue(next, PointType.SAND);
                previous = next;
                if (numberOfBlockedSandUnits < 300) {
                    drawGrid.repaint(1);
                }
            }
        }
        drawGrid.repaint(0);
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
