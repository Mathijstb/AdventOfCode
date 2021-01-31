import fileUtils.FileReader;
import lombok.Value;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day6 {

    private enum SwitchDirection {
        ON,
        OFF,
        TOGGLE
    }

    @Value
    private static class Instruction {
        SwitchDirection switchDirection;
        Point coordFrom;
        Point coordTo;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input6.csv");
        List<Instruction> instructions = getInstructions(lines);
        executeInstructions(instructions);
    }

    private static void executeInstructions(List<Instruction> instructions) {
        int[][] grid = new int[1000][1000];
        instructions.forEach(instruction -> {
            Point coordFrom = instruction.coordFrom;
            Point coordTo = instruction.coordTo;
            for (int y = coordFrom.y; y <= coordTo.y; y++) {
                for (int x = coordFrom.x; x <= coordTo.x; x++) {
                    switch (instruction.switchDirection) {
                        case ON: grid[y][x] += 1; break;
                        case OFF: grid[y][x] = Math.max(0, grid[y][x] - 1); break;
                        case TOGGLE: grid[y][x] += 2; break;
                    }
                }
            }
        });
        long brightness = 0;
        for (int[] line : grid) {
            for (int b : line) {
                brightness += b;
            }
        }
        System.out.println("Brightness: " + brightness);
    }

    private static List<Instruction> getInstructions(List<String> lines) {
        final String turnOn = "turn on ";
        final String turnOff = "turn off ";
        final String toggle = "toggle ";
        return lines.stream().map(line -> {
            SwitchDirection switchDirection;
            String restString;
            if (line.contains(turnOn)) {
                switchDirection = SwitchDirection.ON;
                restString = line.substring(turnOn.length());
            }
            else if (line.contains(turnOff)) {
                switchDirection = SwitchDirection.OFF;
                restString = line.substring(turnOff.length());
            }
            else if (line.contains(toggle)) {
                switchDirection = SwitchDirection.TOGGLE;
                restString = line.substring(toggle.length());
            }
            else
                throw new IllegalStateException("invalid line");
           String[] coords = restString.split(" through ");
           String[] coordFrom = coords[0].split(",");
           String[] coordTo = coords[1].split(",");
           return new Instruction(switchDirection, new Point(Integer.parseInt(coordFrom[0]), Integer.parseInt(coordFrom[1])),
                                                   new Point(Integer.parseInt(coordTo[0]),   Integer.parseInt(coordTo[1])));
        }).collect(Collectors.toList());
    }

}
