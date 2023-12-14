package day10;

import com.google.common.primitives.Chars;
import day3.Symbol;
import fileUtils.FileReader;
import grids.FiniteGrid;
import grids.NeighbourType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Day10 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input10.csv");
        var grid = getGrid(lines);
        var lengthOfLoop = findLengthOfLoop(grid);
        System.out.println("Length of loop: " + lengthOfLoop);
        System.out.println("Number of steps furthest away: " + lengthOfLoop / 2);
    }

    private static FiniteGrid<Pipe> getGrid(List<String> lines) {
        FiniteGrid<Pipe> grid = new FiniteGrid<>();
        lines.forEach(line -> grid.addRow(new ArrayList<>(Chars.asList(line.toCharArray()).stream().map(Pipe::of).toList())));
        grid.draw(p -> String.valueOf(p.character));
        return grid;
    }

    private static int findLengthOfLoop(FiniteGrid<Pipe> grid) {
        var start = grid.getAllPoints().stream()
                .filter(point -> grid.getValue(point).equals(Pipe.START))
                .findFirst().orElseThrow();
        var hasLeftConnection = grid.getNeighbour(start, NeighbourType.LEFT)
                .map(left -> Set.of(Pipe.H, Pipe.NE, Pipe.SE).contains(grid.getValue(left))).orElse(false);
        var hasRightConnection = grid.getNeighbour(start, NeighbourType.RIGHT)
                .map(left -> Set.of(Pipe.H, Pipe.NW, Pipe.SW).contains(grid.getValue(left))).orElse(false);
        var hasUpConnection = grid.getNeighbour(start, NeighbourType.RIGHT)
                .map(left -> Set.of(Pipe.V, Pipe.SE, Pipe.SW).contains(grid.getValue(left))).orElse(false);
        var hasDownConnection = grid.getNeighbour(start, NeighbourType.RIGHT)
                .map(left -> Set.of(Pipe.V, Pipe.NE, Pipe.NW).contains(grid.getValue(left))).orElse(false);
        assert Stream.of(hasLeftConnection, hasRightConnection, hasUpConnection, hasDownConnection)
                .filter(value -> value).count() == 2;
        var sPipe = (hasLeftConnection && hasRightConnection) ? Pipe.H :
                    (hasLeftConnection && hasUpConnection) ? Pipe.NW :
                    (hasLeftConnection && hasDownConnection) ? Pipe.SW :
                    (hasRightConnection && hasUpConnection) ? Pipe.NE :
                    (hasRightConnection && hasDownConnection) ? Pipe.SE : Pipe.V;
        grid.setValue(start, sPipe);
        var startFacing = switch (sPipe) {
            case NW, NE, V -> Facing.SOUTH;
            case H -> Facing.EAST;
            case SE, SW -> Facing.NORTH;
            default -> throw new IllegalStateException("Illegal start pipe");
        };
        return walkAround(grid, new PointAndFacing(start, startFacing));
    }

    private static int walkAround(FiniteGrid<Pipe> grid, PointAndFacing startPointAndFacing) {
        var start = new Point(startPointAndFacing.point());
        var currentPointAndFacing = new PointAndFacing(new Point(start), startPointAndFacing.facing());
        var numberOfSteps = 0;
        do {
            currentPointAndFacing = getNext(grid, currentPointAndFacing);
            numberOfSteps += 1;
        }
        while (!currentPointAndFacing.point().equals(start));
        return numberOfSteps;
    }

    private static PointAndFacing getNext(FiniteGrid<Pipe> grid, PointAndFacing pointAndFacing) {
        var currentPoint = pointAndFacing.point();
        var currentFacing = pointAndFacing.facing();
        var value = grid.getValue(currentPoint);
        Point next;
        Facing nextFacing;
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
            default -> throw new IllegalStateException("Invalid pipe value");
        };
    }
}
