package day13;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Day13 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input13.csv");
        var grids = getGrids(lines);
        var result = determineResult(grids);
        System.out.println();
        System.out.println("Total result: " + result);
        System.out.println();
        var alternativeResult = determineAlternativeResult(grids);
        System.out.println("Alternative result: " + alternativeResult);
    }

    private static int determineResult(List<FiniteGrid<PointType>> grids) {
        var results = IntStream.range(0, grids.size()).map(i -> {
            var grid = grids.get(i);
            System.out.println();
            System.out.printf("Grid %s:", i);
            System.out.println();
            System.out.println("-".repeat(grid.getWidth()));
            grid.draw(p -> String.valueOf(p.character));
            System.out.println("-".repeat(grid.getWidth()));
            var horizontalIndex = determineHorizontalReflectionIndex(grid);
            var verticalIndex = determineVerticalReflectionIndex(grid);

            if (horizontalIndex.isEmpty()) {
                System.out.println("No horizontal reflection");
            } else {
                System.out.printf("Number of columns above reflection: %d", horizontalIndex.get() + 1);
                System.out.println();
            }

            if (verticalIndex.isEmpty()) {
                System.out.println("No vertical reflection");
            } else {
                System.out.printf("Number of columns left of reflection: %d", verticalIndex.get() + 1);
                System.out.println();
            }
            int result = 100 * (horizontalIndex.map(h -> h + 1).orElse(0)) + (verticalIndex.map(v -> v + 1).orElse(0));
            System.out.println("Result: " + result);
            return result;
        }).boxed().toList();
        return results.stream().reduce(0, Integer::sum);
    }

    private static int determineAlternativeResult(List<FiniteGrid<PointType>> grids) {
        return grids.stream().map(grid -> {
            var horizontalReflectionIndex = determineHorizontalReflectionIndex(grid);
            var verticalReflectionIndex = determineVerticalReflectionIndex(grid);
            var alternativeHorizontalReflectionIndexOpt = getAlternativeHorizontalReflectionIndex(grid, horizontalReflectionIndex.orElse(-1));
            return alternativeHorizontalReflectionIndexOpt
                    .map(integer -> 100 * (integer + 1))
                    .orElseGet(() -> getAlternativeVerticalReflectionIndex(grid, verticalReflectionIndex.orElse(-1)).orElseThrow() + 1);
        }).reduce(0, Integer::sum);
    }

    private static List<FiniteGrid<PointType>> getGrids(List<String> lines) {
        return FileReader.splitLines(lines, String::isEmpty).stream().map(lineGroup -> {
            var grid = new FiniteGrid<PointType>();
            lineGroup.forEach(line -> grid.addRow(new ArrayList<>(Chars.asList(line.toCharArray()).stream().map(PointType::of).toList())));
            return grid;
        }).toList();
    }

    private static Optional<Integer> getAlternativeHorizontalReflectionIndex(FiniteGrid<PointType> grid, int currentIndex) {
        var allPoints = grid.getAllPoints();
        for (Point point : allPoints) {
            var oldValue = grid.getValue(point);
            var newValue = oldValue.equals(PointType.ASH) ? PointType.ROCK : PointType.ASH;
            grid.setValue(point, newValue);
            var newIndex = determineHorizontalReflectionIndex(grid, currentIndex);
            grid.setValue(point, oldValue);
            if (newIndex.isPresent() && !newIndex.get().equals(currentIndex)) {
                return newIndex;
            }
        }
        return Optional.empty();
    }

    private static Optional<Integer> getAlternativeVerticalReflectionIndex(FiniteGrid<PointType> grid, int currentIndex) {
        var allPoints = grid.getAllPoints();
        for (Point point : allPoints) {
            var oldValue = grid.getValue(point);
            var newValue = oldValue.equals(PointType.ASH) ? PointType.ROCK : PointType.ASH;
            grid.setValue(point, newValue);
            var newIndex = determineVerticalReflectionIndex(grid, currentIndex);
            grid.setValue(point, oldValue);
            if (newIndex.isPresent() && !newIndex.get().equals(currentIndex)) {
                return newIndex;
            }
        }
        return Optional.empty();
    }

    private static Optional<Integer> determineHorizontalReflectionIndex(FiniteGrid<PointType> grid) {
        var reflections = IntStream.range(0, grid.getHeight() - 1)
                .filter(lineNumber -> hasHorizontalReflection(grid, lineNumber))
                .boxed().toList();
        return reflections.stream().findFirst();
    }

    private static Optional<Integer> determineHorizontalReflectionIndex(FiniteGrid<PointType> grid, int excludeIndex) {
        var reflections = IntStream.range(0, grid.getHeight() - 1)
                .filter(lineNumber -> hasHorizontalReflection(grid, lineNumber) && lineNumber != excludeIndex)
                .boxed().toList();
        return reflections.stream().findFirst();
    }

    private static Optional<Integer> determineVerticalReflectionIndex(FiniteGrid<PointType> grid) {
        var reflections =  IntStream.range(0, grid.getWidth() - 1)
                .filter(lineNumber -> hasVerticalReflection(grid, lineNumber))
                .boxed().toList();
        return reflections.stream().findFirst();
    }

    private static Optional<Integer> determineVerticalReflectionIndex(FiniteGrid<PointType> grid,  int excludeIndex) {
        var reflections =  IntStream.range(0, grid.getWidth() - 1)
                .filter(lineNumber -> hasVerticalReflection(grid, lineNumber) && lineNumber != excludeIndex)
                .boxed().toList();
        return reflections.stream().findFirst();
    }

    private static boolean hasHorizontalReflection(FiniteGrid<PointType> grid, int lineNumber) {
        return IntStream.range(0, lineNumber + 1)
                .filter(i -> 2 * lineNumber + 1 - i < grid.getHeight())
                .allMatch(i -> grid.getRow(i).equals(grid.getRow(2 * lineNumber + 1 - i)));
    }

    private static boolean hasVerticalReflection(FiniteGrid<PointType> grid, int lineNumber) {
        return IntStream.range(0, lineNumber + 1)
                .filter(i -> 2 * lineNumber + 1 - i < grid.getWidth())
                .allMatch(i -> grid.getCol(i).equals(grid.getCol(2 * lineNumber + 1 - i)));
    }

}
