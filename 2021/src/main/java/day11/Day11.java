package day11;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day11 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input11.csv");
        FiniteGrid<Integer> grid = getGrid(input);
        runSimulation(grid, 2000);
    }

    private static FiniteGrid<Integer> getGrid(List<String> input) {
        FiniteGrid<Integer> grid = new FiniteGrid<>();
        input.forEach(line -> grid.addRow(Arrays.stream(line.split("")).map(Integer::parseInt).collect(Collectors.toList())));
        return grid;
    }

    private static void runSimulation(FiniteGrid<Integer> grid, int numberOfSteps) {
        int totalFlashes = 0;
        int firstStepAllFlash = -1;
        boolean allFlashedAtSameTime = false;
        for (int i = 0; i < numberOfSteps; i++) {
            //increase all by 1
            grid.getAllPoints().forEach(point -> grid.setValue(point, grid.getValue(point) + 1));

            // apply flashes iteratively
            applyFlashes(grid);
            int numberOfFlashes = grid.getPoints(value -> value == 0).size();
            if (!allFlashedAtSameTime && numberOfFlashes == grid.getHeight() * grid.getWidth()) {
                firstStepAllFlash = i+1;
                allFlashedAtSameTime = true;
            }
            totalFlashes += numberOfFlashes;

            System.out.println("Step: " + (i+1));
            System.out.println("Number of flashes: " + numberOfFlashes);
            System.out.println("Total flashes: " + totalFlashes);
            System.out.println("--------------------------");
            grid.draw(value -> {
                if (value == 0) return String.format("\u001B[32m% 3d", value);
                return String.format("\u001B[0m% 3d", value);
            });
            System.out.println();
        }
        System.out.println("First step all flash: " + firstStepAllFlash);
    }

    private static void applyFlashes(FiniteGrid<Integer> grid) {
        List<Point> flashingPoints = grid.getPoints(value -> value > 9);
        while (flashingPoints.size() > 0) {
            flashingPoints.forEach(point -> {
                grid.setValue(point, 0);

                //increase value of each point that has not flashed yet
                grid.getNeighbours(point, true).stream()
                        .filter(neighbour -> grid.getValue(neighbour) != 0)
                        .forEach(neighbour -> grid.updateValue(neighbour, value -> value + 1));
            });
            flashingPoints = grid.getPoints(value -> value > 9);
        }
    }

}