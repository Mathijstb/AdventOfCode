package day22;

import fileUtils.FileReader;
import grids.Grid3D;

import javax.vecmath.Point3i;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
        System.out.println();
        printBricksAbove(grid, bricks);
        var numberOfBricksDisintegratable = bricks.stream().filter(brick -> {
            var bricksAbove = brick.getBricksAbove(grid);
            return bricksAbove.stream().allMatch(brickAbove -> (long) brickAbove.getBricksBelow(grid).size() > 1);
        }).count();
        System.out.println();
        System.out.println("Number of bricks that can safely be disintegrated: " + numberOfBricksDisintegratable);
    }

    private static void printBricksBelow(Grid3D<PointState> grid, List<Brick> bricks) {
        bricks.forEach(brick -> {
            var bricksBelow = brick.getBricksBelow(grid);
            var bricksBelowString = bricksBelow.isEmpty() ? "-" : bricksBelow.stream()
                    .map(b -> String.valueOf(b.getId())).collect(Collectors.joining(", "));
            System.out.printf("Brick %s is supported by: %s%n", brick.getId(), bricksBelowString);
        });
    }

    private static void printBricksAbove(Grid3D<PointState> grid, List<Brick> bricks) {
        bricks.forEach(brick -> {
            var bricksAbove = brick.getBricksAbove(grid);
            var bricksAboveString = bricksAbove.isEmpty() ? "-" : bricksAbove.stream()
                    .map(b -> String.valueOf(b.getId())).collect(Collectors.joining(", "));
            System.out.printf("Brick %s is supporting: %s%n", brick.getId(), bricksAboveString);
        });
    }

    private static final Function<PointState, String> drawFunction = pointState -> String.valueOf(pointState.brick().getId());

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
        var id = new AtomicInteger(0);
        return lines.stream().map(line -> {
            var points = Arrays.stream(line.split("~"))
                    .map(part -> part.split(","))
                    .map(coords -> new Point3i(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])))
                    .toList();
            char characterId = (char) ('A' + id.getAndIncrement());
            return new Brick(characterId, points.get(0), points.get(1));
        }).toList();
    }

    private static Grid3D<PointState> parseGrid(List<Brick> bricks) {
        var grid = new Grid3D<PointState>();
        bricks.forEach(brick -> brick.getPoints().forEach(point -> grid.setValue(point, new PointState(brick))));
        return grid;
    }
}
