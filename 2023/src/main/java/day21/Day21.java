package day21;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day21 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input21.csv");
        var grid = parseGrid(lines);
        int numberOfSteps = 64;
        var finishPoints = walkSteps(grid.copy(), numberOfSteps);
        drawGrid(grid, finishPoints, numberOfSteps);
        System.out.println();

        //part 2
        var smallGridsize = grid.getWidth();
        var largeGrid = createLargeGrid(grid, 2);

        numberOfSteps = 65;
        var remainder = numberOfSteps % smallGridsize;
        walkSteps(largeGrid.copy(), remainder);
        walkSteps(largeGrid.copy(), remainder + smallGridsize);
        walkSteps(largeGrid.copy(), remainder + smallGridsize * 2);
        //Solution: f(n+x) = 3896 + 15440 x + 15281 x^2, n= 65, x = 202300
    }

    private static InfiniteGrid<PointType> parseGrid(List<String> lines) {
        var grid = new InfiniteGrid<PointType>();
        for (int y = 0; y < lines.size(); y++) {
            var line = lines.get(y);
            var pointTypes = Chars.asList(line.toCharArray()).stream().map(PointType::of).toList();
            for (int x = 0; x < pointTypes.size(); x++) {
                grid.setValue(new Point(x, y), pointTypes.get(x));
            }
        }
        return grid;
    }

    private static void drawGrid(InfiniteGrid<PointType> grid, List<Point> finishPoints, int numberOfSteps) {
        System.out.println();
        var drawGrid = grid.copy();
        finishPoints.forEach(fp -> drawGrid.setValue(fp, PointType.FINISH));
        drawGrid.draw(pointType -> String.valueOf(pointType.character), String.valueOf(PointType.PLOT.character));
        System.out.printf("Number of finish points after %s steps: %s%n", numberOfSteps, finishPoints.size());
    }

    private static InfiniteGrid<PointType> createLargeGrid(InfiniteGrid<PointType> grid, int numberOfRings) {
        InfiniteGrid<PointType> result = new InfiniteGrid<>();
        var size = grid.getWidth();
        if (size != grid.getHeight()) {
            throw new IllegalArgumentException("Grid is not square"); // assert square grid
        }
        for (int dx = -numberOfRings; dx <= numberOfRings; dx++) {
            for (int dy = -numberOfRings; dy <= numberOfRings; dy++) {
                for (Point point: grid.getAllPoints()) {
                    var value = grid.getValue(point);
                    if (!(dx == 0 && dy == 0) && value.equals(PointType.START)) {
                        value = PointType.PLOT;
                    }
                    result.setValue(new Point(point.x + dx * size, point.y + dy * size), value);
                }
            }
        }
        return result;
    }

    private static List<Point> walkSteps(InfiniteGrid<PointType> grid, int numberOfSteps) {
        var isEvenNumberOfSteps = numberOfSteps % 2 == 0;
        var start = grid.getPoints(p -> p.equals(PointType.START)).stream().findFirst().orElseThrow();
        grid.setValue(start, PointType.PLOT);

        Set<Point> currentPoints = new HashSet<>(List.of(start));
        Set<Point> passedPoints = new HashSet<>(currentPoints);
        Set<Point> finishPoints = isEvenNumberOfSteps? new HashSet<>(currentPoints) : new HashSet<>();
        for (int i = 0; i < numberOfSteps; i++) {
            var newPoints = currentPoints.stream()
                    .map(cp -> grid.getNeighbours(cp, false, pointType -> pointType.equals(PointType.PLOT))
                            .stream().filter(p -> !passedPoints.contains(p)).toList())
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());
           if (i % 2 == (isEvenNumberOfSteps ? 1 : 0)) {
               passedPoints.addAll(newPoints);
               finishPoints.addAll(newPoints);
           }
           currentPoints = newPoints;
        }
        System.out.printf("Number of finish points after %s steps: %s%n", numberOfSteps, finishPoints.size());
        return finishPoints.stream().toList();
    }
}
