package day16;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day16 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input16.csv");
        var grid = parseGrid(lines);
        flowThroughGrid(grid);

        //Part 2
        findBestStartingPosition(grid);
    }

    private static FiniteGrid<PointState> parseGrid(List<String> lines) {
        var result = new FiniteGrid<PointState>();
        lines.stream().map(line -> Chars.asList(line.toCharArray()))
                .forEach(line -> result.addRow(new ArrayList<>(line.stream().map(p -> new PointState(PointType.of(p))).toList())));
        return result;
    }

    private static FiniteGrid<PointState> resetGrid(FiniteGrid<PointState> grid) {
        var result = grid.copy();
        result.getAllPoints().forEach(p -> result.setValue(p, new PointState(grid.getValue(p).pointType)));
        return result;
    }

    private static void findBestStartingPosition(FiniteGrid<PointState> grid) {
        var width = grid.getWidth();
        var height = grid.getHeight();
        var maxEnlightenedTop = IntStream.range(0, width).mapToObj(x -> new Point(x, 0))
                .map(p -> getNumberEnlightened(grid, p, Direction.SOUTH))
                .max(Comparator.comparingLong(n -> n)).orElseThrow();
        var maxEnlightenedBottom = IntStream.range(0, width).mapToObj(x -> new Point(x, height - 1))
                .map(p -> getNumberEnlightened(grid, p, Direction.NORTH))
                .max(Comparator.comparingLong(n -> n)).orElseThrow();
        var maxEnlightenedLeft = IntStream.range(0, height).mapToObj(y -> new Point(0, y))
                .map(p -> getNumberEnlightened(grid, p, Direction.EAST))
                .max(Comparator.comparingLong(n -> n)).orElseThrow();
        var maxEnlightenedRight = IntStream.range(0, height).mapToObj(y -> new Point(width - 1, y))
                .map(p -> getNumberEnlightened(grid, p, Direction.WEST))
                .max(Comparator.comparingLong(n -> n)).orElseThrow();

        var maxEnlightened = Stream.of(maxEnlightenedLeft, maxEnlightenedBottom, maxEnlightenedRight, maxEnlightenedTop).max(Comparator.comparingLong(n -> n)).orElseThrow();
        System.out.println("Max enlightened: " + maxEnlightened);
    }

    private static long getNumberEnlightened(FiniteGrid<PointState> oldGrid, Point position, Direction direction) {
        var grid = resetGrid(oldGrid);
        flowNextStep(grid, position, direction);
        return grid.getAllPoints().stream().filter(p -> grid.getValue(p).isEnlightened()).count();
    }

    private static void flowThroughGrid(FiniteGrid<PointState> grid) {
        System.out.println("Starting grid:");
        grid.draw(PointState::getDrawCharacter);
        var start = new Point(0, 0);
        var direction = Direction.EAST;
        flowNextStep(grid, start, direction);

        System.out.println();
        System.out.println("End grid:");
        grid.draw(PointState::getDrawCharacter);

        System.out.println();
        System.out.println("Enlightened:");
        grid.draw(PointState::getEnlightenedCharacter);

        var numberEnlightened = grid.getAllPoints().stream().filter(p -> grid.getValue(p).isEnlightened()).count();
        System.out.println();
        System.out.println("Number enlightened: " + numberEnlightened);
    }

    private static void flowNextStep(FiniteGrid<PointState> grid, Point position, Direction direction) {
        var directions = grid.getValue(position).directions;
        if (directions.contains(direction)) return;
        grid.getValue(position).directions.add(direction);
        switch (grid.getValue(position).pointType) {
            case EMPTY -> move(grid, position, direction);
            case SPLITTER_V -> {
                switch (direction) {
                    case EAST, WEST -> {
                        move(grid, position, Direction.NORTH);
                        move(grid, position, Direction.SOUTH);
                    }
                    case NORTH, SOUTH -> move(grid, position, direction);
                }
            }
            case SPLITTER_H -> {
                switch (direction) {
                    case NORTH, SOUTH -> {
                        move(grid, position, Direction.EAST);
                        move(grid, position, Direction.WEST);
                    }
                    case EAST, WEST -> move(grid, position, direction);
                }
            }
            case MIRROR_B -> {
                switch (direction) {
                    case EAST -> move(grid, position, Direction.SOUTH);
                    case NORTH -> move(grid, position, Direction.WEST);
                    case WEST -> move(grid, position, Direction.NORTH);
                    case SOUTH -> move(grid, position, Direction.EAST);
                }
            }
            case MIRROR_F -> {
                switch (direction) {
                    case EAST -> move(grid, position, Direction.NORTH);
                    case NORTH -> move(grid, position, Direction.EAST);
                    case WEST -> move(grid, position, Direction.SOUTH);
                    case SOUTH -> move(grid, position, Direction.WEST);
                }
            }
        }
    }

    private static void move(FiniteGrid<PointState> grid, Point position, Direction direction) {
        var nextPosition = switch (direction) {
            case EAST -> new Point(position.x + 1, position.y);
            case NORTH -> new Point(position.x, position.y - 1);
            case WEST -> new Point(position.x - 1, position.y);
            case SOUTH -> new Point(position.x, position.y + 1);
        };
        if (nextPosition.x >= 0 && nextPosition.y >= 0 && nextPosition.x < grid.getWidth() && nextPosition.y < grid.getHeight()) {
            flowNextStep(grid, nextPosition, direction);
        }
    }
}
