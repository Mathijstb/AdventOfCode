package day13;

import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day13 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input13a.csv");
        int emptyLineIndex = IntStream.range(0, input.size()).filter(i -> input.get(i).isEmpty()).findFirst().orElseThrow();
        InfiniteGrid<Integer> grid = getGrid(input.subList(0, emptyLineIndex));
        List<Instruction> instructions = getInstructions(input.subList(emptyLineIndex + 1, input.size()));

        System.out.println("Initial grid: ");
        grid.draw( s -> "#", " ");
        System.out.println();

        InfiniteGrid<Integer> gridCopy = new InfiniteGrid<>();
        grid.getAllPoints().forEach(point -> gridCopy.setValue(point, grid.getValue(point)));
        executeFolding(gridCopy, List.of(instructions.get(0)));
        System.out.println("Number of visible dots after first fold: " + gridCopy.getAllPoints().size());

        executeFolding(grid, instructions);
    }

    private static InfiniteGrid<Integer> getGrid(List<String> input) {
        InfiniteGrid<Integer> grid = new InfiniteGrid<>();
        input.stream().map( s -> {
            String[] coords = s.split(",");
            return new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
        }).forEach(point -> grid.setValue(point, 0));
        return grid;
    }

    private static List<Instruction> getInstructions(List<String> input) {
        return input.stream().map(s -> {
            String[] foldDirectionAndLineNumber = s.split("fold along ")[1].split("=");
            return new Instruction(FoldDirection.of(foldDirectionAndLineNumber[0]), Integer.parseInt(foldDirectionAndLineNumber[1]));
        }).collect(Collectors.toList());
    }

    private static void executeFolding(InfiniteGrid<Integer> grid, List<Instruction> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            executeFold(grid, instruction.getFoldDirection(), instruction.getLineNumber());
            System.out.printf("Fold %d (%s):%n", i, instruction.getFoldDirection().toString());
            grid.draw( s -> "#", " ");
            System.out.println();
        }
    }

    private static void executeFold(InfiniteGrid<Integer> grid, FoldDirection foldDirection, int lineNumber) {
        Set<Point> points = getPointsForFolding(grid, foldDirection, lineNumber);
        points.forEach(point -> {
            Point newPoint = new Point(point);
            grid.clearValue(point);
            switch (foldDirection) {
                case HORIZONTAL: newPoint.translate(0, 2 * (lineNumber - point.y)); break;
                case VERTICAL: newPoint.translate(2 * (lineNumber - point.x), 0); break;
                default: throw new IllegalArgumentException("Invalid fold direction");
            }
            grid.setValue(newPoint, 0);
        });
    }

    private static Set<Point> getPointsForFolding(InfiniteGrid<Integer> grid, FoldDirection foldDirection, int lineNumber) {
        switch (foldDirection) {
            case HORIZONTAL: return grid.getAllPoints().stream().filter(point -> point.y > lineNumber).collect(Collectors.toSet());
            case VERTICAL: return grid.getAllPoints().stream().filter(point -> point.x > lineNumber).collect(Collectors.toSet());
            default: throw new IllegalArgumentException("Invalid fold direction");
        }
    }


}