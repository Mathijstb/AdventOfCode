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
        FiniteGrid<Risk> grid = getGrid(input);

        System.out.println("Risk levels: ");
        System.out.println("-------------");
        grid.draw(riskLevel -> String.valueOf(riskLevel.getEnterLevel()));
        System.out.println();

        findShortestPath(grid);
    }

    private static FiniteGrid<Risk> getGrid(List<String> input) {
        FiniteGrid<Risk> grid = new FiniteGrid<>();
        input.forEach(line -> grid.addRow(Arrays.stream(line.split("")).map(s -> new Risk(Integer.parseInt(s), new ArrayList<>(), 999999)).collect(Collectors.toList())));
        return grid;
    }

    private static void findShortestPath(FiniteGrid<Risk> grid) {
        Point end = new Point(grid.getWidth()-1, grid.getHeight()-1);
        grid.getValue(end).setTotalLevel(0);

        Set<Point> nextPoints = grid.getNeighbours(end, false).stream().filter(n -> n.x < end.x || n.y < end.y).collect(Collectors.toSet());
        while (!nextPoints.isEmpty()) {
            findShortestPathPhase(grid, nextPoints);
            nextPoints = nextPoints.stream().map(point ->
                            grid.getNeighbours(point, false).stream().filter(n -> n.x < point.x || n.y < point.y).collect(Collectors.toList()))
                            .flatMap(Collection::stream).collect(Collectors.toSet());
        }

        System.out.println("Total risk levels: ");
        System.out.println("-------------------");
        grid.draw(value -> String.format("\u001B[0m% 3d", value.totalLevel));
        System.out.println();
    }

    private static void findShortestPathPhase(FiniteGrid<Risk> grid, Set<Point> points) {
        points.forEach(point -> {
            Risk risk = grid.getValue(point);
            List<Point> neighbours = grid.getNeighbours(point, false).stream().filter(n -> n.x > point.x || n.y > point.y).collect(Collectors.toList());
            neighbours.forEach(neighbour -> {
                Risk neighbourRisk = grid.getValue(neighbour);
                if (risk.getTotalLevel() > neighbourRisk.getTotalLevel() + neighbourRisk.getEnterLevel()) {
                    risk.setTotalLevel(neighbourRisk.getTotalLevel() + neighbourRisk.getEnterLevel());
                }
            });
        });
    }

}