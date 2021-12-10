package day2;

import fileUtils.FileReader;

import java.util.List;
import java.util.stream.Collectors;

public class Day2 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2a.csv");
        List<Instruction> instructions = lines.stream().map(line -> {
            var parts = line.split(" ");
            var direction = Direction.of(parts[0]);
            var amount = Integer.parseInt(parts[1]);
            return new Instruction(direction, amount);
        }).collect(Collectors.toList());
        executeInstructions(instructions);
        executeInstructions2(instructions);
    }

    private static void executeInstructions(List<Instruction> instructions) {
        int position = 0;
        int depth = 0;
        for (Instruction instruction : instructions) {
            switch (instruction.getDirection()) {
                case UP: depth -= instruction.getAmount(); break;
                case DOWN: depth += instruction.getAmount(); break;
                case FORWARD: position += instruction.getAmount();break;
            }
        }
        System.out.println("Position: " + position);
        System.out.println("Depth: " + depth);
        System.out.println("Multiplication: " + position * depth);
    }

    private static void executeInstructions2(List<Instruction> instructions) {
        int position = 0;
        int depth = 0;
        int aim = 0;
        for (Instruction instruction : instructions) {
            switch (instruction.getDirection()) {
                case UP: aim -= instruction.getAmount(); break;
                case DOWN: aim += instruction.getAmount(); break;
                case FORWARD: {
                    position += instruction.getAmount();
                    depth += aim * instruction.getAmount();
                } break;
            }
        }
        System.out.println("Position: " + position);
        System.out.println("Depth: " + depth);
        System.out.println("Multiplication: " + position * depth);
    }

}
