package day8;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class Day8 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input8.csv");
        var instructions = readInstructions(lists);
        executeInstructions(instructions);
    }

    private static List<Instruction> readInstructions(List<String> lists) {
        return lists.stream().map(list -> {
           if (list.startsWith("rect ")) {
               var params = list.split("rect ")[1].split("x");
               return new Instruction(InstructionType.RECT, Integer.parseInt(params[0]), Integer.parseInt(params[1]));
           } else if (list.startsWith("rotate column x")) {
               var params = list.split("rotate column x=")[1].split(" by ");
               return new Instruction(InstructionType.ROTATE_X, Integer.parseInt(params[0]), Integer.parseInt(params[1]));
           } else if (list.startsWith("rotate row y=")) {
               var params = list.split("rotate row y=")[1].split(" by ");
               return new Instruction(InstructionType.ROTATE_Y, Integer.parseInt(params[0]), Integer.parseInt(params[1]));
           }
           else {
               throw new IllegalArgumentException("Can not parse line");
           }
        }).toList();
    }

    private static void executeInstructions(List<Instruction> instructions) {
        FiniteGrid<Boolean> grid = FiniteGrid.initializeGrid(50, 6, false);
        instructions.forEach(instruction -> executeInstruction(grid, instruction));
        var numberOfPixelsLit = grid.getAllPoints().stream().filter(grid::getValue).count();
        System.out.println("Number of pixels that are lit: " + numberOfPixelsLit);
    }

    private static void executeInstruction(FiniteGrid<Boolean> grid, Instruction instruction) {
        switch (instruction.type()) {
            case RECT -> {
                var width = instruction.param1();
                var height = instruction.param2();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        grid.setValue(new Point(x, y), true);
                    }
                }
            }
            case ROTATE_X -> {
                var x = instruction.param1();
                var amount = instruction.param2();
                var column = grid.getCol(x);
                var newColumn = new ArrayList<>(column.subList(column.size() - amount, column.size()));
                newColumn.addAll(column.subList(0, column.size() - amount));
                IntStream.range(0, newColumn.size()).forEach(i -> grid.setValue(new Point(x, i), newColumn.get(i)));
            }
            case ROTATE_Y -> {
                var y = instruction.param1();
                var amount = instruction.param2();
                var row = grid.getRow(y);
                var newRow = new ArrayList<>(row.subList(row.size() - amount, row.size()));
                newRow.addAll(row.subList(0, row.size() - amount));
                IntStream.range(0, newRow.size()).forEach(i -> grid.setValue(new Point(i, y), newRow.get(i)));
            }
        }
        System.out.println();
        drawGrid(grid);
    }

    private static void drawGrid(FiniteGrid<Boolean> grid) {
        grid.draw(value -> value ? "#" :" ");
    }
}