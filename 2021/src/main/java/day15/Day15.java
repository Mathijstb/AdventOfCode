package day15;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day15 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input15.csv");

        //Create normal grid
        FiniteGrid<Risk> grid = getGrid(input);
        List<Point> shortestPath = findShortestPath(grid);

        //draw grid
        List<Risk> shortestPathValues = shortestPath.stream().map(grid::getValue).collect(Collectors.toList());
        grid.draw(risk -> shortestPathValues.contains(risk) ? String.format("\u001B[42m% 5d", risk.totalLevel) : String.format("\u001B[0m% 5d", risk.totalLevel));
        System.out.println("\u001B[0mLowest risk from top left to bottom right: " + grid.getValue(new Point(grid.getWidth()-1, grid.getHeight()-1)).getTotalLevel());
        System.out.println("Shortest path length: " + shortestPathValues.size());
        System.out.println();

        //Create big grid
        FiniteGrid<Risk> bigGrid = getBigGrid(input);
        List<Point> shortestPath2 = findShortestPath(bigGrid);

        //draw grid
        List<Risk> shortestPathValues2 = shortestPath2.stream().map(bigGrid::getValue).collect(Collectors.toList());
        bigGrid.draw(risk -> shortestPathValues2.contains(risk) ? String.format("\u001B[42m% 5d", risk.totalLevel) : String.format("\u001B[0m% 5d", risk.totalLevel));
        System.out.println("\u001B[0mLowest risk from top left to bottom right: " + bigGrid.getValue(new Point(bigGrid.getWidth()-1, bigGrid.getHeight()-1)).getTotalLevel());
        System.out.println("Shortest path length: " + shortestPathValues2.size());
    }

    private static FiniteGrid<Risk> getGrid(List<String> input) {
        FiniteGrid<Risk> grid = new FiniteGrid<>();
        input.forEach(line -> grid.addRow(Arrays.stream(line.split("")).map(s -> new Risk(Integer.parseInt(s), new ArrayList<>(), 99999999)).collect(Collectors.toList())));
        return grid;
    }

    private static FiniteGrid<Risk> getBigGrid(List<String> input) {
        FiniteGrid<Risk> grid = getGrid(input);

        List<Point> points = grid.getAllPoints();
        List<List<Risk>> bigGridRows = new ArrayList<>();
        for (int gridsRow = 0; gridsRow < 5; gridsRow++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                int rowNumber = y;
                List<Risk> risks = points.stream().filter(point -> point.y == rowNumber).map(grid::getValue).collect(Collectors.toList());
                List<Risk> bigGridRow = new ArrayList<>();
                for (int gridsCol = 0; gridsCol < 5; gridsCol++) {
                    int extraRisk = gridsRow + gridsCol;
                    bigGridRow.addAll(risks.stream().map(risk -> {
                        int newRisk = risk.enterLevel + extraRisk;
                        if (newRisk > 9) {
                            newRisk = Math.floorMod(newRisk, 9);
                        }
                        return new Risk(newRisk, new ArrayList<>(), risk.totalLevel);}).collect(Collectors.toList()));
                }
                bigGridRows.add(bigGridRow);
            }
        }
        FiniteGrid<Risk> bigGrid = new FiniteGrid<>();
        bigGridRows.forEach(bigGrid::addRow);
        return bigGrid;
    }

    private static List<Point> findShortestPath(FiniteGrid<Risk> grid) {
        List<Point> allPoints = grid.getAllPoints();
        allPoints.forEach(point -> grid.getValue(point).setTotalLevel(9999));

        Point source = new Point(0, 0);
        grid.getValue(source).setTotalLevel(0);

        PriorityQueue<Point> pq = new PriorityQueue<>(Comparator.comparing(p -> grid.getValue(p).getTotalLevel()));
        pq.add(source);

        Set<Point> settledPoints = new HashSet<>();
        while(!pq.isEmpty()) {
            Point currentPoint = pq.remove();
            if (settledPoints.contains(currentPoint)) continue;

            Risk risk = grid.getValue(currentPoint);
            List<Point> neighbours = grid.getNeighbours(currentPoint, false).stream().filter(n -> !settledPoints.contains(n)).collect(Collectors.toList());
            neighbours.forEach(neighbour -> {
                Risk neighbourRisk = grid.getValue(neighbour);
                if (risk.totalLevel + neighbourRisk.enterLevel < neighbourRisk.totalLevel) {
                    neighbourRisk.setTotalLevel(risk.totalLevel + neighbourRisk.enterLevel);
                    List<Point> newShortestPath = new ArrayList<>(risk.getShortestPath());
                    newShortestPath.add(neighbour);
                    neighbourRisk.setShortestPath(newShortestPath);
                }
                pq.add(neighbour);
            });
            settledPoints.add(currentPoint);
        }
        return grid.getValue(new Point(grid.getWidth()-1, grid.getHeight()-1)).getShortestPath();
    }

}