package day18;

import fileUtils.FileReader;
import grids.Grid3D;

import javax.vecmath.Point3i;
import java.util.*;
import java.util.stream.Collectors;

public class Day18 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input18.csv");
        var grid = readGrid(lines);
        determineNumberOfSides(grid);
        findNumberOfSidesAdjacentToSteam(grid);
    }

    private static Grid3D<PointType> readGrid(List<String> lines) {
        var grid = new Grid3D<PointType>();
        lines.forEach(line -> {
            var coords = line.split(",");
            var point = new Point3i(Integer.parseInt(coords[0]),
                                    Integer.parseInt(coords[1]),
                                    Integer.parseInt(coords[2]));
            grid.setValue(point, PointType.LAVA);
        });
        return grid;
    }

    private static void determineNumberOfSides(Grid3D<PointType> grid) {
        var count = grid.getAllPoints().stream().map(point -> {
            var neighbours = grid.getNeighbours(point, false);
            return 6 - neighbours.size();
        }).reduce(0, Integer::sum);
        System.out.println("Number of sides: " + count);
    }

    private static int minX;
    private static int maxX;
    private static int minY;
    private static int maxY;
    private static int minZ;
    private static int maxZ;

    private static void findNumberOfSidesAdjacentToSteam(Grid3D<PointType> grid) {
        minX = grid.getMinX() -1;
        minY = grid.getMinY() -1;
        minZ = grid.getMinZ() -1;
        maxX = grid.getMaxX() +1;
        maxY = grid.getMaxY() +1;
        maxZ = grid.getMaxZ() +1;
        var previousSet = getStartSet(grid);
        previousSet.forEach(p -> grid.setValue(p, PointType.STEAM));

        //Get all adjacent points towards the center that are not lava and fill them with steam
        while (!previousSet.isEmpty()) {
            var nextSet = previousSet.stream()
                    .map(point -> getAdjacent(point).stream()
                            .filter(p -> !grid.containsPoint(p)).collect(Collectors.toSet()))
                    .flatMap(Collection::stream).collect(Collectors.toSet());
            nextSet.forEach(p -> grid.setValue(p, PointType.STEAM));
            previousSet = nextSet;
        }

        //Count all sides that are adjacent to steam
        long numberAdjacentToSteam = grid.getAllPoints().stream().filter(p -> grid.getValue(p).equals(PointType.LAVA))
                .map(p -> grid.getNeighbours(p, false).stream()
                        .filter(neighbour -> grid.getValue(neighbour).equals(PointType.STEAM))
                        .count())
                .reduce(0L, Long::sum);
        System.out.println("Number of sides adjacent to steam: " + numberAdjacentToSteam);
    }

    ///Start with a set of points that are the boundary of the grid
    private static Set<Point3i> getStartSet(Grid3D<PointType> grid) {
        Set<Point3i> points = new HashSet<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                var pointMinZ = new Point3i(x, y, minZ);
                if (!grid.containsPoint(pointMinZ)) {
                    points.add(pointMinZ);
                }
                var pointMaxZ = new Point3i(x, y, maxZ);
                if (!grid.containsPoint(pointMaxZ)) {
                    points.add(pointMaxZ);
                }
            }
        }
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                var pointMinY = new Point3i(x, minY, z);
                if (!grid.containsPoint(pointMinY)) {
                    points.add(pointMinY);
                }
                var pointMaxY = new Point3i(x, maxY, z);
                if (!grid.containsPoint(pointMaxY)) {
                    points.add(pointMaxY);
                }
            }
        }
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                var pointMinX = new Point3i(minX, y, z);
                if (!grid.containsPoint(pointMinX)) {
                    points.add(pointMinX);
                }
                var pointMaxX = new Point3i(maxX, y, z);
                if (!grid.containsPoint(pointMaxX)) {
                    points.add(pointMaxX);
                }
            }
        }
        return points;
    }

    /// Get all adjacent points towards the center
    private static Set<Point3i> getAdjacent(Point3i point) {
        Set<Point3i> adjacent = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int newX = point.x + dx;
                    int newY = point.y + dy;
                    int newZ = point.z + dz;

                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if ((dx != 0 && dy != 0) || (dx != 0 && dz != 0) || (dy != 0 && dz != 0)) continue;
                    var newPoint = new Point3i(newX, newY, newZ);
                    if (newX >= minX && newX <= maxX && newY >= minY && newY <= maxY && newZ >= minZ && newZ <= maxZ) {
                        adjacent.add(newPoint);
                    }
                }
            }
        }
        return adjacent;
    }




}