package day22;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class Day22 {

    private static BiFunction<FiniteGrid<PointType>, State, State> otherSideOfMapFunction;

    private static final List<State> states = new ArrayList<>();

    private static final Cube cube = new Cube();

    private static Map<Integer, Point> sideIndexToOriginMap = new HashMap<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input22.csv");
        var grid = readGrid(lines.subList(0, lines.size() - 2));
        var instructions = readInstructions(lines.get(lines.size() - 1));

        //part a
        drawGrid(grid, Collections.emptyList());
        otherSideOfMapFunction = Day22::getPointOnOtherSideOfMap;
        executeInstructions(grid, instructions);
        findPassword();

        //part b
        states.clear();
        otherSideOfMapFunction = Day22::getPointOnOtherSideOfMap2;
        sideIndexToOriginMap = getSideIndexToOriginMap();
        executeInstructions(grid, instructions);
        findPassword();
    }

    private static FiniteGrid<PointType> readGrid(List<String> lines) {
        var height = lines.size();
        var width = lines.stream().map(String::length).reduce(0, Integer::max);
        FiniteGrid<PointType> grid = FiniteGrid.initializeGrid(width, height, PointType.EMPTY);
        for (int y = 0; y < lines.size(); y++) {
            var line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                var pointType = switch (line.charAt(x)) {
                    case ' ' -> PointType.EMPTY;
                    case '.' -> PointType.AIR;
                    case '#' -> PointType.WALL;
                    default -> throw new IllegalArgumentException("Can not parse line");
                };
                grid.setValue(new Point(x, y), pointType);
            }
        }
        return grid;
    }

    private static List<Instruction> readInstructions(String line) {
        String[] parts = line.split("[^A-Z0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])");
        return Arrays.stream(parts).map(s -> switch (s) {
            case "L" -> new Instruction(InstructionType.LEFT, Optional.empty());
            case "R" -> new Instruction(InstructionType.RIGHT, Optional.empty());
            default -> new Instruction(InstructionType.FORWARD, Optional.of(Integer.parseInt(s)));
        }).toList();
    }

    private static void executeInstructions(FiniteGrid<PointType> grid, List<Instruction> instructions) {
        var firstRow = grid.getRow(0);
        var position = new Point(IntStream.range(0, firstRow.size()).filter(x -> firstRow.get(x).equals(PointType.AIR)).findFirst().orElseThrow(), 0);
        var facing = Facing.RIGHT;
        var state = new State(position, facing);

        states.add(state);
        for (Instruction instruction : instructions) {
            state = executeInstruction(grid, instruction, state);
        }
    }

    private static State executeInstruction(FiniteGrid<PointType> grid, Instruction instruction, State state) {
        State newState = state;
        switch (instruction.type()) {
            case FORWARD -> {
                int amount = instruction.value().orElseThrow();
                for (int i = 0; i < amount; i++) {
                   var newStateOpt = determineNextPosition(grid, newState);
                   if (newStateOpt.isPresent()) {
                       newState = newStateOpt.get();
                       states.add(newState);
                       drawGrid(grid, states);
                   }
                   else {
                       break;
                   }
                }
            }
            case LEFT -> {
                newState = new State(newState.position(), Facing.of(Math.floorMod(state.facing().index - 1, 4)));
                states.add(newState);
                drawGrid(grid, states);
            }
            case RIGHT -> {
                newState = new State(newState.position(), Facing.of(Math.floorMod(state.facing().index + 1, 4)));
                states.add(newState);
                drawGrid(grid, states);
            }
        }
        return newState;
    }

    private static Optional<State> determineNextPosition(FiniteGrid<PointType> grid, State state) {
        var position = state.position();
        var facing = state.facing();
        Point newPoint = switch (facing) {
            case RIGHT -> new Point(position.x + 1, position.y);
            case LEFT -> new Point(position.x - 1, position.y);
            case DOWN -> new Point(position.x, position.y + 1);
            case UP -> new Point(position.x, position.y - 1);
        };
        var newFacing = facing;
        if (!grid.containsPoint(newPoint) || grid.getValue(newPoint).equals(PointType.EMPTY)) {
            var newState = otherSideOfMapFunction.apply(grid, state);
            newPoint = newState.position();
            newFacing = newState.facing();
        }

        var pointType = grid.getValue(newPoint);
        if (pointType.equals(PointType.WALL)) {
            return Optional.empty();
        }
        else {
            assert pointType.equals(PointType.AIR);
            return Optional.of(new State(newPoint, newFacing));
        }
    }

    private static State getPointOnOtherSideOfMap(FiniteGrid<PointType> grid, State state) {
        var position = state.position();
        var facing = state.facing();
        var newPosition = switch (facing) {
            case RIGHT -> grid.getRowPoints(position.y).stream()
                    .filter(p -> Set.of(PointType.WALL, PointType.AIR).contains(grid.getValue(p)))
                    .min(Comparator.comparingInt(p -> p.x)).stream().findFirst().orElseThrow();
            case LEFT -> grid.getRowPoints(position.y).stream()
                    .filter(p -> Set.of(PointType.WALL, PointType.AIR).contains(grid.getValue(p)))
                    .max(Comparator.comparingInt(p -> p.x)).stream().findFirst().orElseThrow();
            case DOWN -> grid.getColPoints(position.x).stream()
                    .filter(p -> Set.of(PointType.WALL, PointType.AIR).contains(grid.getValue(p)))
                    .min(Comparator.comparingInt(p -> p.y)).stream().findFirst().orElseThrow();
            case UP -> grid.getColPoints(position.x).stream()
                    .filter(p -> Set.of(PointType.WALL, PointType.AIR).contains(grid.getValue(p)))
                    .max(Comparator.comparingInt(p -> p.y)).stream().findFirst().orElseThrow();
        };
        return new State(newPosition, state.facing());
    }

    //  1 2
    //  4
    //5 6
    //3
    private static Map<Integer, Point> getSideIndexToOriginMap() {
        Map<Integer, Point> map = new HashMap<>();
        map.put(1, new Point(50, 0));
        map.put(2, new Point(100, 0));
        map.put(3, new Point(0, 150));
        map.put(4, new Point(50, 50));
        map.put(5, new Point(0, 100));
        map.put(6, new Point(50, 100));
        return map;
    }

    private static State getPointOnOtherSideOfMap2(FiniteGrid<PointType> grid, State state) {
        var gridPosition = state.position();
        var facing = state.facing();
        int sideIndex = determineSideIndex(gridPosition);
        var sideOrigin = sideIndexToOriginMap.get(sideIndex);
        var position = new Point(gridPosition.x - sideOrigin.x, gridPosition.y - sideOrigin.y);

        var newFacing = cube.getOppositeFacing(sideIndex, facing);
        var newSideIndex = cube.getOppositeSideIndex(sideIndex, facing);
        var newSideOrigin = sideIndexToOriginMap.get(newSideIndex);
        var newPosition = transformPoint(position, facing, newFacing);
        var newGridPosition = new Point(newPosition.x + newSideOrigin.x, newPosition.y + newSideOrigin.y);
        return new State(newGridPosition, newFacing);
    }

    private static Point transformPoint(Point point, Facing oldFacing, Facing newFacing) {
        int sideSize = 50;
        var angle = Math.floorMod(newFacing.index - oldFacing.index, 4) * 90;
        var newPoint = rotatePoint(point, angle);
        return switch (newFacing) {
            case UP, DOWN -> new Point(newPoint.x, sideSize - 1 - newPoint.y);
            case LEFT, RIGHT -> new Point(sideSize - 1 - newPoint.x, newPoint.y);
        };
    }

    private static Point rotatePoint(Point point, int angle) {
        double[] pt = {point.x, point.y};
        AffineTransform.getRotateInstance(Math.toRadians(angle), 24.50, 24.50)
                .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
        double newX = pt[0];
        double newY = pt[1];
        return new Point((int) newX, (int) newY);
    }

    private static int determineSideIndex(Point point) {
        var x = point.x;
        var y = point.y;
        //  3
        //5 1 2
        //  4
        //  6

        //  1 2
        //  4
        //5 6
        //3

        if (50 <= x && x <= 99 && 0 <= y && y <= 49) return 1;
        if (100 <= x && x <= 149 && 0 <= y && y <= 49) return 2;
        if (50 <= x && x <= 99 && 50 <= y && y <= 99) return 4;
        if (0 <= x && x <= 49 && 100 <= y && y <= 149) return 5;
        if (50 <= x && x <= 99 && 100 <= y && y <= 149) return 6;
        if (0 <= x && x <= 49 && 150 <= y && y <= 199) return 3;
        throw new IllegalArgumentException("Invalid point: " + point);
    }


    private static void drawGrid(FiniteGrid<PointType> grid, List<State> states) {
        var gridCopy = grid.copy();
        states.forEach(state -> {
            var position = state.position();
            switch (state.facing()) {
                case LEFT -> gridCopy.setValue(position, PointType.LEFT);
                case RIGHT -> gridCopy.setValue(position, PointType.RIGHT);
                case UP -> gridCopy.setValue(position, PointType.UP);
                case DOWN -> gridCopy.setValue(position, PointType.DOWN);
            }
        });
        gridCopy.draw(pointType -> switch (pointType) {
            case AIR -> ".";
            case WALL -> "#";
            case EMPTY -> " ";
            case RIGHT -> ">";
            case DOWN -> "v";
            case LEFT -> "<";
            case UP -> "^";
        });
        System.out.println();
    }

    private static void findPassword() {
        var lastState = states.get(states.size() - 1);
        var position = lastState.position();
        var facing = lastState.facing();
        int password = 1000 * (position.y + 1) + 4 * (position.x + 1) + facing.index;
        System.out.println("Password: " + password);
    }
}