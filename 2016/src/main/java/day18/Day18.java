package day18;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.stream.IntStream;

import static day18.PointType.SAFE;
import static day18.PointType.TRAP;

public class Day18 {

    public static void execute() {
        String line = FileReader.getFileReader().readFile("input18.csv").stream().findFirst().orElseThrow();
        var grid = readGrid(line);
        determineNextRows(grid);
        drawGrid(grid);
        var numberOfSafeTiles = grid.getAllPoints().stream().filter(point -> grid.getValue(point).equals(SAFE)).count();
        System.out.println("Number of safe tiles: " + numberOfSafeTiles);
    }

    private static FiniteGrid<PointType> readGrid(String line) {
        FiniteGrid<PointType> grid = new FiniteGrid<>();
        var firstRow = line.chars().mapToObj(c -> c == '.' ? SAFE : TRAP).toList();
        grid.addRow(firstRow);
        return grid;
    }

    private static void determineNextRows(FiniteGrid<PointType> grid) {
        for (int n = 0; n < 400000 - 1; n++) {
            int y = grid.getHeight();
            var nextRow = IntStream.range(0, grid.getWidth()).mapToObj(x -> {
                var point = new Point(x, y);
                return isTrap(point, grid) ? TRAP : SAFE;
            }).toList();
            grid.addRow(nextRow);
        }
    }

    private static boolean isTrap(Point point, FiniteGrid<PointType> grid) {
        var left = new Point(point.x - 1, point.y - 1);
        var center = new Point(point.x, point.y - 1);
        var right = new Point(point.x + 1, point.y - 1);
        boolean leftIsTrap = grid.containsPoint(left) && grid.getValue(left).equals(TRAP);
        boolean centerIsTrap = grid.containsPoint(center) && grid.getValue(center).equals(TRAP);
        boolean rightIsTrap = grid.containsPoint(right) && grid.getValue(right).equals(TRAP);
        return (leftIsTrap && centerIsTrap && !rightIsTrap) ||
                (!leftIsTrap && rightIsTrap && centerIsTrap) ||
                (leftIsTrap && !centerIsTrap && !rightIsTrap) ||
                (!leftIsTrap && !centerIsTrap && rightIsTrap);
    }

    private static void drawGrid(FiniteGrid<PointType> grid) {
        grid.draw(pointType -> switch (pointType) {
            case SAFE -> ".";
            case TRAP -> "^";
        });
    }
}