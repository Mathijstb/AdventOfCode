package day10;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Day10 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input10.csv");
        var instructions = readInstructions(lines);
        var values = executeInstructions(instructions);
        determineSignalStrengths(values);
        drawPixels(values);
    }

    private static List<Instruction> readInstructions(List<String> lines) {
        return lines.stream().map(line -> line.equals("noop")
                ? new Instruction(InstructionType.NOOP, Optional.empty())
                : new Instruction(InstructionType.ADDX, Optional.of(Integer.parseInt(line.split(" ")[1])))).toList();
    }

    private static List<Integer> executeInstructions(List<Instruction> instructions) {
        List<Integer> values = new ArrayList<>();
        values.add(1);
        for (Instruction instruction : instructions) {
            int previousValue = values.get(values.size() - 1);
            switch (instruction.type()) {
                case NOOP -> values.add(previousValue);
                case ADDX -> {
                    values.add(previousValue);
                    values.add(previousValue + instruction.parameter().orElseThrow());
                }
            }
        }
        return values;
    }

    private static void determineSignalStrengths(List<Integer> values) {
        Set<Integer> indices = Set.of(20, 60, 100, 140, 180, 220);
        var sum = indices.stream().map(index -> values.get(index - 1) * index).reduce(0, Integer::sum);
        System.out.println("Sum of signal strengths: " + sum);
    }

    private static void drawPixels(List<Integer> values) {
        var grid = new FiniteGrid<Boolean>();
        var cycle = 1;
        for (int y = 0; y < 6; y++) {
            List<Boolean> row = new ArrayList<>();
            for (int x = 0; x < 40; x++) {
                var crtPosition = values.get(cycle - 1);
                row.add(x >= crtPosition - 1 && x <= crtPosition + 1);
                cycle += 1;
            }
            grid.addRow(row);
        }
        grid.draw(value -> value.equals(Boolean.TRUE) ? "#" : ".");
    }

}
