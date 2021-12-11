package day2;

import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input2.csv");
        List<Instruction> instructions = input.stream().map(line ->
                new Instruction(Arrays.stream(line.split("")).map(Direction::of).collect(Collectors.toList()))).collect(Collectors.toList());
        printBathroomCode(instructions);
        printBathroomCode2(instructions);
    }

    private static void printBathroomCode(List<Instruction> instructions) {
        FiniteGrid<Integer> grid = new FiniteGrid<>();
        grid.addRow(List.of(1, 2, 3));
        grid.addRow(List.of(4, 5, 6));
        grid.addRow(List.of(7, 8, 9));
        Point position = new Point(1, 1);
        List<Integer> code = getCodePositions(position, grid, instructions).stream().map(grid::getValue).collect(Collectors.toList());

        System.out.println("Bathroom code 1: " + code);
    }

    private static void printBathroomCode2(List<Instruction> instructions) {
        FiniteGrid<Character> grid = new FiniteGrid<>();
        grid.addRow(List.of(' ', ' ', '1', ' ', ' '));
        grid.addRow(List.of(' ', '2', '3', '4', ' '));
        grid.addRow(List.of('5', '6', '7', '8', '9'));
        grid.addRow(List.of(' ', 'A', 'B', 'C', ' '));
        grid.addRow(List.of(' ', ' ', 'D', ' ', ' '));
        Point position = new Point(1, 1);
        List<Character> code = getCodePositions(position, grid, instructions).stream().map(grid::getValue).collect(Collectors.toList());

        System.out.println("Bathroom code 2: " + code);
    }

    private static <T> List<Point> getCodePositions(Point position, FiniteGrid<T> grid, List<Instruction> instructions) {
        List<Point> codePositions = new ArrayList<>();
        instructions.forEach(instruction -> {
            instruction.getDirections().forEach(direction -> {
                switch (direction) {
                    case L: {
                        moveIfPossible(grid, position, -1, 0);
                        break;
                    }
                    case R: {
                        moveIfPossible(grid, position, 1, 0);
                        break;
                    }
                    case U: {
                        moveIfPossible(grid, position, 0, -1);
                        break;
                    }
                    case D: {
                        moveIfPossible(grid, position, 0, 1);
                        break;
                    }
                    default: throw new IllegalArgumentException("Invalid direction");
                }
            });
            codePositions.add(new Point(position));
        });
        return codePositions;
    }

    private static <T> void moveIfPossible(FiniteGrid<T> grid, Point position, int dx, int dy) {
        Point newPoint = new Point(position);
        newPoint.translate(dx, dy);
        if (grid.getAllPoints().contains(newPoint) && !grid.getValue(newPoint).equals(' ')) {
            position.translate(dx, dy);
        }
    }
}