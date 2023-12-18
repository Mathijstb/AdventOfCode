package day11;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.FiniteGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day11 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input11.csv");
        var grid = getGrid(lines);

        //Part1
        var expandedGrid = expandGrid(grid);
        expandedGrid.draw(p -> String.valueOf(p.character));
        var sumOfShortestPaths = findSumOfShortestPaths(expandedGrid);
        System.out.println("Sum of shortest paths: " + sumOfShortestPaths);

        //Part2
        var sumOfShortestPaths2 = findSumOfShortestPaths2(grid);
        System.out.println("Sum of shortest paths 2: " + sumOfShortestPaths2);
    }

    private static FiniteGrid<PointType> getGrid(List<String> lines) {
        var grid = new FiniteGrid<PointType>();
        lines.forEach(line -> grid.addRow(new ArrayList<>(Chars.asList(line.toCharArray()).stream().map(PointType::of).toList())));
        return grid;
    }

    private static FiniteGrid<PointType> expandGrid(FiniteGrid<PointType> grid) {
        var result = grid.copy();
        findEmptyRowIndices(grid).forEach(y ->
                result.insertRow(y, new ArrayList<>(IntStream.range(0, result.getWidth()).mapToObj(x -> PointType.SPACE).toList())));
        findEmptyColumnIndices(grid).forEach(x ->
                result.insertColumn(x, new ArrayList<>(IntStream.range(0, result.getHeight()).mapToObj(y -> PointType.SPACE).toList())));
        return result;
    }

    private static List<Integer> findEmptyRowIndices(FiniteGrid<PointType> grid) {
        return IntStream.range(0, grid.getHeight()).mapToObj(y -> grid.getHeight() - y - 1)
             .filter(y -> grid.getRow(y).stream().allMatch(p -> p.equals(PointType.SPACE))).toList();
    }

    private static List<Integer> findEmptyColumnIndices(FiniteGrid<PointType> grid) {
        return IntStream.range(0, grid.getWidth()).mapToObj(x -> grid.getWidth() - x - 1)
                .filter(x -> grid.getCol(x).stream().allMatch(p -> p.equals(PointType.SPACE))).toList();
    }

    private static int findSumOfShortestPaths(FiniteGrid<PointType> grid) {
        var galaxyLocations = grid.getAllPoints().stream()
                .filter(p -> grid.getValue(p).equals(PointType.GALAXY)).toList();
        var galaxies = IntStream.range(0, galaxyLocations.size()).mapToObj(i -> new Galaxy(i, galaxyLocations.get(i))).toList();
        Map<Galaxy, Map<Galaxy, Integer>> distances = new HashMap<>();
        galaxies.forEach(galaxy1 -> {
            Map<Galaxy, Integer> distanceMap = new HashMap<>();
            distances.put(galaxy1, distanceMap);
            galaxies.forEach(galaxy2 -> distanceMap.put(galaxy2, findDistance(grid, galaxy1, galaxy2)));
        });
        return distances.values().stream().map(distanceMap -> distanceMap.values().stream()
                .reduce(0, Integer::sum)).reduce(0, Integer::sum) / 2;
    }

    private static int findDistance(FiniteGrid<PointType> grid, Galaxy galaxy1, Galaxy galaxy2) {
        return Math.abs(galaxy1.point().x - galaxy2.point().x) + Math.abs(galaxy1.point().y - galaxy2.point().y);
    }

    private static long findSumOfShortestPaths2(FiniteGrid<PointType> grid) {
        var emptyRowIndices = findEmptyRowIndices(grid);
        var emptyColumnIndices = findEmptyColumnIndices(grid);
        var galaxyLocations = grid.getAllPoints().stream()
                .filter(p -> grid.getValue(p).equals(PointType.GALAXY)).toList();
        var galaxies = IntStream.range(0, galaxyLocations.size()).mapToObj(i -> new Galaxy(i, galaxyLocations.get(i))).toList();
        Map<Galaxy, Map<Galaxy, Long>> distances = new HashMap<>();
        galaxies.forEach(galaxy1 -> {
            Map<Galaxy, Long> distanceMap = new HashMap<>();
            distances.put(galaxy1, distanceMap);
            galaxies.forEach(galaxy2 -> distanceMap.put(galaxy2, findDistance2(galaxy1, galaxy2, emptyRowIndices, emptyColumnIndices)));
        });
        return distances.values().stream().map(distanceMap -> distanceMap.values().stream()
                .reduce(0L, Long::sum)).reduce(0L, Long::sum) / 2;
    }

    private static long findDistance2(Galaxy galaxy1, Galaxy galaxy2, List<Integer> emptyRowIndices, List<Integer> emptyColumnIndices) {
        var emptyLineDistance = 1000000;
        var x1 = galaxy1.point().x;
        var x2 = galaxy2.point().x;
        var y1 = galaxy1.point().y;
        var y2 = galaxy2.point().y;
        var numberOfEmptyColumnsInBetween = findNumberOfIndicesInRange(x1, x2, emptyColumnIndices);
        var numberOfEmptyRowsInBetween = findNumberOfIndicesInRange(y1, y2, emptyRowIndices);
        return Math.abs(x1 - x2) + numberOfEmptyColumnsInBetween * (emptyLineDistance - 1)
                + Math.abs(y1 - y2) + numberOfEmptyRowsInBetween * (emptyLineDistance - 1);
    }

    private static long findNumberOfIndicesInRange(int bound1, int bound2, List<Integer> indices) {
        if (bound1 > bound2) {
            return indices.stream().filter(i -> i > bound2 && i < bound1).count();
        }
        else {
            return indices.stream().filter(i -> i > bound1 && i < bound2).count();
        }
    }


}
