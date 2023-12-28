package day10;

import com.google.common.primitives.Chars;
import drawUtils.DrawGrid;
import drawUtils.Images;
import fileUtils.FileReader;
import grids.FiniteGrid;
import grids.InfiniteGrid;
import grids.NeighbourType;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Day10 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input10.csv");
        var grid = getGrid(lines);

        //Part 1
        var start = findStart(grid);
        var loopLocations = findLoopLocations(grid, start);
        var lengthOfLoop = loopLocations.size();
        System.out.println("Length of loop: " + lengthOfLoop);
        System.out.println("Number of steps furthest away: " + lengthOfLoop / 2);

        //Part 2
        loopLocations.forEach(p -> {
            var newPointType = switch (grid.getValue(p.point())) {
                case NW -> PointType.LOOP_NW;
                case NE -> PointType.LOOP_NE;
                case SW -> PointType.LOOP_SW;
                case SE -> PointType.LOOP_SE;
                case V -> PointType.LOOP_V;
                case H -> PointType.LOOP_H;
                default -> grid.getValue(p.point());
            };
            grid.setValue(p.point(), newPointType);
        });
        var locationsWithinLoop = findLocationsWithinLoop(grid, loopLocations);
        locationsWithinLoop.forEach(p -> grid.setValue(p, PointType.LOOP_GROUND));
        System.out.println("Number of locations within loop: " + locationsWithinLoop.size());

        drawGridWithLoop(grid);
    }

    private static List<Point> findLocationsWithinLoop(FiniteGrid<PointType> grid, List<PointAndFacing> loopLocations) {
        var result = new ArrayList<Point>();
        for (int y = 0; y < grid.getHeight(); y++) {
            var rowPoints = grid.getRowPoints(y);
            boolean insideLoop = false;
            PointType lastCurve = PointType.GROUND;
            for (Point point : rowPoints) {
                var pointType = grid.getValue(point);
                switch (pointType) {
                    case GROUND, V, H, SE, SW, NW, NE -> {
                        if (insideLoop) result.add(point);
                    }
                    case LOOP_V -> insideLoop = !insideLoop;
                    case LOOP_NE, LOOP_SE -> lastCurve = pointType;
                    case LOOP_NW -> {
                        if (lastCurve.equals(PointType.LOOP_SE)) {
                            insideLoop = !insideLoop;
                        }
                    }
                    case LOOP_SW -> {
                        if (lastCurve.equals(PointType.LOOP_NE)) {
                            insideLoop = !insideLoop;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static void drawGridWithLoop(FiniteGrid<PointType> grid) {
        Map<PointType, Consumer<DrawGrid.DrawParameters>> paintMap = new HashMap<>();

        paintMap.put(PointType.NE, (dp) -> dp.g2d().drawImage(Images.getImage("northeast_blue.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.NW, (dp) -> dp.g2d().drawImage(Images.getImage("northwest_blue.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.SE, (dp) -> dp.g2d().drawImage(Images.getImage("southeast_blue.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.SW, (dp) -> dp.g2d().drawImage(Images.getImage("southwest_blue.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.V, (dp) -> dp.g2d().drawImage(Images.getImage("vertical_blue.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.H, (dp) -> dp.g2d().drawImage(Images.getImage("horizontal_blue.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));

        paintMap.put(PointType.LOOP_NE, (dp) -> dp.g2d().drawImage(Images.getImage("northeast.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.LOOP_NW, (dp) -> dp.g2d().drawImage(Images.getImage("northwest.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.LOOP_SE, (dp) -> dp.g2d().drawImage(Images.getImage("southeast.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.LOOP_SW, (dp) -> dp.g2d().drawImage(Images.getImage("southwest.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.LOOP_V, (dp) -> dp.g2d().drawImage(Images.getImage("vertical.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.LOOP_H, (dp) -> dp.g2d().drawImage(Images.getImage("horizontal.png"), dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize(), null));
        paintMap.put(PointType.GROUND, (dp) -> {
            dp.g2d().setColor(Color.GRAY);
            dp.g2d().fillRect(dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize());
        });
        paintMap.put(PointType.LOOP_GROUND, (dp) -> {
            dp.g2d().setColor(Color.ORANGE);
            dp.g2d().fillRect(dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize());
        });
        var drawGrid = new DrawGrid<>("Pipes", PointType.class, grid.getPointMap(), PointType.GROUND, paintMap);
        drawGrid.repaint(0);
    }

    private static FiniteGrid<PointType> getGrid(List<String> lines) {
        FiniteGrid<PointType> grid = new FiniteGrid<>();
        lines.forEach(line -> grid.addRow(new ArrayList<>(Chars.asList(line.toCharArray()).stream().map(PointType::of).toList())));
        grid.draw(p -> String.valueOf(p.character));
        return grid;
    }

    private static InfiniteGrid<PointType> getGrid2(List<String> lines) {
        InfiniteGrid<PointType> grid = new InfiniteGrid<>();
        for (int y = 0; y < lines.size(); y++) {
            var line = lines.get(y);
            var pointTypes = Chars.asList(line.toCharArray()).stream().map(PointType::of).toList();
            for (int x = 0; x < pointTypes.size(); x++) {
                grid.setValue(new Point(x, y), pointTypes.get(x));
            }
        }
        return grid;
    }

    private static PointAndFacing findStart(FiniteGrid<PointType> grid) {
        var start = grid.getAllPoints().stream()
                .filter(point -> grid.getValue(point).equals(PointType.START))
                .findFirst().orElseThrow();
        var hasLeftConnection = grid.getNeighbour(start, NeighbourType.LEFT)
                .map(left -> Set.of(PointType.H, PointType.NE, PointType.SE).contains(grid.getValue(left))).orElse(false);
        var hasRightConnection = grid.getNeighbour(start, NeighbourType.RIGHT)
                .map(left -> Set.of(PointType.H, PointType.NW, PointType.SW).contains(grid.getValue(left))).orElse(false);
        var hasUpConnection = grid.getNeighbour(start, NeighbourType.RIGHT)
                .map(left -> Set.of(PointType.V, PointType.SE, PointType.SW).contains(grid.getValue(left))).orElse(false);
        var hasDownConnection = grid.getNeighbour(start, NeighbourType.RIGHT)
                .map(left -> Set.of(PointType.V, PointType.NE, PointType.NW).contains(grid.getValue(left))).orElse(false);
        assert Stream.of(hasLeftConnection, hasRightConnection, hasUpConnection, hasDownConnection)
                .filter(value -> value).count() == 2;
        var sPipe = (hasLeftConnection && hasRightConnection) ? PointType.H :
                    (hasLeftConnection && hasUpConnection) ? PointType.NW :
                    (hasLeftConnection && hasDownConnection) ? PointType.SW :
                    (hasRightConnection && hasUpConnection) ? PointType.NE :
                    (hasRightConnection && hasDownConnection) ? PointType.SE : PointType.V;
        grid.setValue(start, sPipe);
        var startFacing = switch (sPipe) {
            case NW, NE, V -> Facing.SOUTH;
            case H -> Facing.EAST;
            case SE, SW -> Facing.NORTH;
            default -> throw new IllegalStateException("Illegal start pipe");
        };
        return new PointAndFacing(start, startFacing);
    }

    private static List<PointAndFacing> findLoopLocations(FiniteGrid<PointType> grid, PointAndFacing startPointAndFacing) {
        var start = new Point(startPointAndFacing.point());
        var currentPointAndFacing = new PointAndFacing(new Point(start), startPointAndFacing.facing());
        var result = new ArrayList<PointAndFacing>();
        do {
            result.add(currentPointAndFacing);
            currentPointAndFacing = getNext(grid, currentPointAndFacing);
        }
        while (!currentPointAndFacing.point().equals(start));
        return result;
    }

    private static PointAndFacing getNext(FiniteGrid<PointType> grid, PointAndFacing pointAndFacing) {
        var currentPoint = pointAndFacing.point();
        var currentFacing = pointAndFacing.facing();
        var value = grid.getValue(currentPoint);
        return switch (value) {
            case H -> switch (currentFacing) {
                case EAST -> new PointAndFacing(new Point(currentPoint.x + 1, currentPoint.y), currentFacing);
                case WEST -> new PointAndFacing(new Point(currentPoint.x - 1, currentPoint.y), currentFacing);
                default -> throw new IllegalStateException("Not connecting");
            };
            case V -> switch (currentFacing) {
                case NORTH -> new PointAndFacing(new Point(currentPoint.x, currentPoint.y - 1), currentFacing);
                case SOUTH -> new PointAndFacing(new Point(currentPoint.x, currentPoint.y + 1), currentFacing);
                default -> throw new IllegalStateException("Not connecting");
            };
            case NE -> switch (currentFacing) {
                case WEST -> new PointAndFacing(new Point(currentPoint.x, currentPoint.y - 1), Facing.NORTH);
                case SOUTH -> new PointAndFacing(new Point(currentPoint.x + 1, currentPoint.y), Facing.EAST);
                default -> throw new IllegalStateException("Not connecting");
            };
            case NW -> switch (currentFacing) {
                case EAST -> new PointAndFacing(new Point(currentPoint.x, currentPoint.y - 1), Facing.NORTH);
                case SOUTH -> new PointAndFacing(new Point(currentPoint.x - 1, currentPoint.y), Facing.WEST);
                default -> throw new IllegalStateException("Not connecting");
            };
            case SE -> switch (currentFacing) {
                case WEST -> new PointAndFacing(new Point(currentPoint.x, currentPoint.y + 1), Facing.SOUTH);
                case NORTH -> new PointAndFacing(new Point(currentPoint.x + 1, currentPoint.y), Facing.EAST);
                default -> throw new IllegalStateException("Not connecting");
            };
            case SW -> switch (currentFacing) {
                case EAST -> new PointAndFacing(new Point(currentPoint.x, currentPoint.y + 1), Facing.SOUTH);
                case NORTH -> new PointAndFacing(new Point(currentPoint.x - 1, currentPoint.y), Facing.WEST);
                default -> throw new IllegalStateException("Not connecting");
            };
            default -> throw new IllegalStateException("Invalid point value");
        };
    }
}
