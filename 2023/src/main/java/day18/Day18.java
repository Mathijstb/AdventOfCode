package day18;

import drawUtils.DrawGrid;
import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.math.BigInteger;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static day18.Movement.NE;
import static day18.Movement.SE;

public class Day18 {

    public static void execute() {
        var lines = FileReader.getFileReader().readFile("input18.csv");

        //part 1
        var instructions = parseInstructions(lines);
        var grid = new InfiniteGrid<Color>();
        var contour = new HashMap<Point, PointType>();
        executeInstructions(grid, contour, instructions);
        fillContour(grid, contour);
        drawGrid(grid);
        var numberOfCubes = grid.points.keySet().size();
        System.out.println("Number of cubes: " + numberOfCubes);

        //part 2
        var instructions2 = parseInstructions2(lines);
        calculateInterior(instructions2);
    }

    private static List<Instruction> parseInstructions2(List<String> lines) {
        return lines.stream().map(line -> {
            var parts = line.split(" ");
            var code = parts[2].split("\\(#")[1].split("\\)")[0];
            var direction = switch (Integer.parseInt(code.substring(5))) {
                case 0 -> Direction.R;
                case 1 -> Direction.D;
                case 2 -> Direction.L;
                case 3 -> Direction.U;
                default -> throw new IllegalArgumentException();
            };
            var numberOfSteps = new BigInteger(code.substring(0, 5), 16).intValue();
            return new Instruction(direction, numberOfSteps, "");
        }).toList();
    }

    private static void calculateInterior(List<Instruction> instructions) {
        List<Point> vertices = new ArrayList<>();
        var current = new Point(0, 0);
        for (Instruction instruction : instructions) {
            var numberOfSteps = instruction.numberOfSteps();
            current = new Point(current);
            switch (instruction.direction()) {
                case U -> current.translate(0, -numberOfSteps);
                case D -> current.translate(0, numberOfSteps);
                case L -> current.translate(-numberOfSteps, 0);
                case R -> current.translate(numberOfSteps, 0);
            }
            vertices.add(current);
        }

        //Shoelace 2A (2 * surface) = sum of determinants of vertices
        long surface = 0L;
        for (int i = 1; i < vertices.size(); i++) {
            var v1 = vertices.get(i - 1);
            var v2 = vertices.get(i);
            surface += (long) v1.x * v2.y - (long) v2.x * v1.y;
        }
        var last = vertices.get(vertices.size() - 1);
        var first = vertices.get(0);
        surface += (long) last.x * first.y - (long) first.x * last.y;
        surface = surface / 2;
        var perimeter = instructions.stream().map(i -> (long) i.numberOfSteps()).reduce(0L, Long::sum);

        //Total surface = surface + perimeter / 2 + 1;
        var numberOfCubes = surface + perimeter / 2 + 1;
        System.out.println("Number of cubes: " + numberOfCubes);
    }

    private static void drawGrid(InfiniteGrid<Color> grid) {
        Map<Color, Consumer<DrawGrid.DrawParameters>> paintMap = new HashMap<>();
        grid.getAllPoints().stream().map(grid::getValue).collect(Collectors.toSet())
                .forEach(color -> paintMap.put(color, (dp) -> {
                    dp.g2d().setColor(color);
                    dp.g2d().fillRect(dp.drawPoint().x, dp.drawPoint().y, dp.blockSize(), dp.blockSize());
                }));

        var drawGrid = new DrawGrid<>("Dig colors", Color.class, grid.points, Color.GRAY, paintMap);
        drawGrid.repaint(0);
    }

    private static List<Instruction> parseInstructions(List<String> lines) {
        return lines.stream().map(line -> {
            var parts = line.split(" ");
            var colorCode = parts[2].split("\\(#")[1].split("\\)")[0];
            return new Instruction(Direction.of(parts[0]), Integer.parseInt(parts[1]), colorCode);
        }).toList();
    }
    private static void executeInstructions(InfiniteGrid<Color> grid, Map<Point, PointType> contour, List<Instruction> instructions) {
        var start = new Point(0, 0);
        var currentPosition = new Point(start);
        grid.setValue(currentPosition, Color.GRAY);
        Direction previousDirection = Direction.U;
        for(Instruction instruction : instructions) {
            for (int i = 0; i < instruction.numberOfSteps(); i++) {
                contour.put(currentPosition, new PointType(previousDirection, instruction.direction()));
                currentPosition = switch (instruction.direction()) {
                    case L -> new Point(currentPosition.x - 1, currentPosition.y);
                    case R -> new Point(currentPosition.x + 1, currentPosition.y);
                    case U -> new Point(currentPosition.x, currentPosition.y - 1);
                    case D -> new Point(currentPosition.x, currentPosition.y + 1);
                };
                grid.setValue(currentPosition, instruction.getColor());
                previousDirection = instruction.direction();
            }
        }

    }

    private static void fillContour(InfiniteGrid<Color> grid, Map<Point, PointType> contour) {
        var minY = grid.getMinY();
        var maxY = grid.getMaxY();
        IntStream.range(minY, maxY + 1).forEach(y -> {
            boolean withinContour = false;
            Optional<Movement> previousTurn = Optional.empty();
            var minX = grid.getMinX();
            var maxX = grid.getMaxX();
            for (int x = minX; x <= maxX; x++) {
                var point = new Point(x, y);
                if (contour.containsKey(point)) {
                    var movement = contour.get(point).getMovement();
                    switch (movement) {
                        case V -> withinContour = !withinContour;
                        case H -> {}
                        case NE -> previousTurn = Optional.of(NE);
                        case SE -> previousTurn = Optional.of(SE);
                        case NW -> withinContour = previousTurn.orElseThrow().equals(SE) != withinContour;
                        case SW -> withinContour = previousTurn.orElseThrow().equals(NE) != withinContour;
                    }
                }
                if (withinContour && !grid.containsPoint(point)) {
                    grid.setValue(point, Color.ORANGE);
                }
            }
        });
    }
}
