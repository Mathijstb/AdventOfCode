package day22;

import fileUtils.FileReader;
import grids.Grid3D;

import javax.vecmath.Point3i;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static grids.Grid3D.Dimension.*;

public class Day22 {

    public static void execute() {
        var lines = FileReader.getFileReader().readFile("input22.csv");
        var bricks = parseBricks(lines);
        var grid = parseGrid(bricks);
        drawGrid(grid);
        dropBricks(grid, bricks);
        drawGrid(grid);
        printBricksBelow(grid, bricks);
        printBricksAbove(grid, bricks);

        System.out.printf("Number of bricks that can safely be disintegrated: %s%n", determineNumberOfDisintegratableBricks(grid, bricks));
        System.out.printf("Total number of falling bricks: %s%n", determineNumberOfFallingBricks(grid, bricks));
    }

    private static int determineNumberOfFallingBricks(Grid3D<PointState> grid, List<Brick> bricks) {
        return bricks.stream()
                .sorted(Comparator.comparingInt(brick -> brick.getLowestPoint().z))
                .map(brick -> determineNumberOfFallingBricks(grid, brick))
                .reduce(0, Integer::sum);
    }

    private static int determineNumberOfFallingBricks(Grid3D<PointState> grid, Brick brick) {
        Set<Brick> currentBricks = new HashSet<>(Set.of(brick));
        Set<Brick> removedBricks = new HashSet<>(currentBricks);
        while (!currentBricks.isEmpty()) {
            Set<Brick> newBricks = currentBricks.stream()
                    .map(cb -> cb.getBricksAbove(grid))
                    .flatMap(Collection::stream).collect(Collectors.toSet())
                    .stream().filter(brickAbove -> removedBricks.containsAll(brickAbove.getBricksBelow(grid)))
                    .collect(Collectors.toSet());
            removedBricks.addAll(newBricks);
            currentBricks = newBricks;
        }
        var result = removedBricks.size() - 1;
        System.out.printf("Removing brick %s results in number of falling bricks: %s%n", brick.getDescription(), result == 0 ? "-" : result);
        return result;
    }

    private static long determineNumberOfDisintegratableBricks(Grid3D<PointState> grid, List<Brick> bricks) {
        return bricks.stream().filter(brick -> {
            var bricksAbove = brick.getBricksAbove(grid);
            return bricksAbove.stream().allMatch(brickAbove -> (long) brickAbove.getBricksBelow(grid).size() > 1);
        }).count();
    }

    private static void printBricksBelow(Grid3D<PointState> grid, List<Brick> bricks) {
        bricks.forEach(brick -> {
            var bricksBelow = brick.getBricksBelow(grid);
            var bricksBelowString = bricksBelow.isEmpty() ? "-" : bricksBelow.stream()
                    .map(b -> String.valueOf(b.getDescription())).collect(Collectors.joining(", "));
            System.out.printf("Brick %s is supported by: %s%n", brick.getDescription(), bricksBelowString);
        });
        System.out.println();
    }

    private static void printBricksAbove(Grid3D<PointState> grid, List<Brick> bricks) {
        bricks.forEach(brick -> {
            var bricksAbove = brick.getBricksAbove(grid);
            var bricksAboveString = bricksAbove.isEmpty() ? "-" : bricksAbove.stream()
                    .map(b -> String.valueOf(b.getDescription())).collect(Collectors.joining(", "));
            System.out.printf("Brick %s is supporting: %s%n", brick.getDescription(), bricksAboveString);
        });
        System.out.println();
    }

    private static final Function<PointState, String> drawFunction = pointState -> String.valueOf(pointState.brick().getDisplayCharacter());

    private static void drawGrid(Grid3D<PointState> grid) {
        grid.draw(X, Z, drawFunction);
        System.out.println();
        grid.draw(Y, Z, drawFunction);
        System.out.println();
    }

    private static void dropBricks(Grid3D<PointState> grid, List<Brick> bricks) {
        List<Brick> currentBricks = new ArrayList<>(bricks);
        currentBricks.sort(Comparator.comparing(brick -> brick.getLowestPoint().z));
        while (!currentBricks.isEmpty()) {
            var currentBrick = currentBricks.remove(0);
            if (!currentBrick.isFloating(grid)) {
                continue;
            }
            while (currentBrick.isFloating(grid)) {
                var oldPoints = currentBrick.getPoints();
                var newPoints = oldPoints.stream().map(point -> new Point3i(point.x, point.y, point.z - 1)).toList();
                oldPoints.forEach(grid::remove);
                newPoints.forEach(newPoint -> grid.setValue(newPoint, new PointState(currentBrick)));
                currentBrick.setPoints(newPoints);
            }
        }
    }

    private static List<Brick> parseBricks(List<String> lines) {
        AtomicInteger id = new AtomicInteger(1);
        return lines.stream().map(line -> {
            var points = Arrays.stream(line.split("~"))
                    .map(part -> part.split(","))
                    .map(coords -> new Point3i(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])))
                    .toList();
            return new Brick(id.getAndIncrement(), points.get(0), points.get(1));
        }).toList();
    }

    private static Grid3D<PointState> parseGrid(List<Brick> bricks) {
        var grid = new Grid3D<PointState>();
        bricks.forEach(brick -> brick.getPoints().forEach(point -> grid.setValue(point, new PointState(brick))));
        return grid;
    }
}
