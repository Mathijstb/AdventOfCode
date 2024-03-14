package day23;

import algorithms.Dijkstra;
import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Day23 {

    public static void execute() {
        var lines = FileReader.getFileReader().readFile("input23.csv");
        var grid = getGrid(lines);
        var result = findLongestPath(grid);
        System.out.println();
        result.path().forEach(pointAndFacing -> {
            if (grid.getValue(pointAndFacing.point()).equals(PointType.PATH)) {
                grid.setValue(pointAndFacing.point(), PointType.ROUTE);
            }
        });
        grid.draw(p -> String.valueOf(p.character));
        System.out.printf("Maximal path length: %s%n", (result.path().size() - 1));

        //part 2
        System.out.println();
        var grid2 = getGrid(lines);
        var start = new Point(grid2.getRowPoints(0).stream()
                .filter(point -> grid2.getValue(point).equals(PointType.PATH))
                .findFirst().orElseThrow());
        var finish = new Point(grid2.getRowPoints(grid2.getHeight() - 1).stream()
                .filter(point -> grid2.getValue(point).equals(PointType.PATH))
                .toList().getLast());
        Set<Point> visited = new HashSet<>();
        var maxLength = findLongestPathWithoutRevisiting(grid2, start, finish, visited, 0);
        System.out.println("Longest path without revisiting: " + maxLength);
    }

    public static int findLongestPathWithoutRevisiting(FiniteGrid<PointType> grid, Point current, Point finish, Set<Point> visited, int length) {
        visited.add(current);
        if (current.equals(finish)) {
            return length;
        }
        var neighbours = grid.getNeighbours(current, false, pointType -> !pointType.equals(PointType.FOREST))
                .stream().filter(p -> !visited.contains(p)).toList();
        if (neighbours.isEmpty()) {
            return -1;
        }
        return neighbours.stream().map(neighbour ->
            findLongestPathWithoutRevisiting(grid, neighbour, finish, new HashSet<>(visited), length + 1)
        ).max(Comparator.comparingInt(a -> a)).orElseThrow();
    }

    private static Dijkstra.PathResult<PointAndFacing> findLongestPath(FiniteGrid<PointType> grid) {
        var start = new PointAndFacing(grid.getRowPoints(0).stream()
                .filter(point -> grid.getValue(point).equals(PointType.PATH))
                .findFirst().orElseThrow(),
                Facing.SOUTH);
        var finish = new PointAndFacing(grid.getRowPoints(grid.getHeight() - 1).stream()
                .filter(point -> grid.getValue(point).equals(PointType.PATH))
                .findFirst().orElseThrow(),
                Facing.SOUTH);
        return Dijkstra.calculateMaximalPath(
                start,
                pointAndFacing -> pointAndFacing.point().equals(finish.point()),
                pointAndFacing -> 1L,
                pointAndFacing -> getNeighbours(grid, pointAndFacing));
    }

    private static List<PointAndFacing> getNeighbours(FiniteGrid<PointType> grid, PointAndFacing pointAndFacing) {
        var point = pointAndFacing.point();
        var facing = pointAndFacing.facing();
        var neighbours = grid.getNeighbours(point, false, pointType -> !pointType.equals(PointType.FOREST));
        var result = new ArrayList<PointAndFacing>();
        var north = new Point(point.x, point.y - 1);
        var east = new Point(point.x + 1, point.y);
        var south = new Point(point.x, point.y + 1);
        var west = new Point(point.x - 1, point.y);
        if (facing != Facing.SOUTH && neighbours.contains(north) && grid.getValue(north) != PointType.SLOPE_DOWN) result.add(new PointAndFacing(north, Facing.NORTH));
        if (facing != Facing.WEST && neighbours.contains(east) && grid.getValue(east) != PointType.SLOPE_LEFT) result.add(new PointAndFacing(east, Facing.EAST));
        if (facing != Facing.NORTH && neighbours.contains(south) && grid.getValue(south) != PointType.SLOPE_UP) result.add(new PointAndFacing(south, Facing.SOUTH));
        if (facing != Facing.EAST && neighbours.contains(west) && grid.getValue(west) != PointType.SLOPE_RIGHT) result.add(new PointAndFacing(west, Facing.WEST));
        return result;
    }

    private static FiniteGrid<PointType> getGrid(List<String> lines) {
        var grid = new FiniteGrid<PointType>();
        lines.forEach(line -> grid.addRow(new ArrayList<>(Chars.asList(line.toCharArray()).stream().map(PointType::of).toList())));
        grid.draw(p -> String.valueOf(p.character));
        return grid;
    }

}
