package day1;

import fileUtils.FileReader;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day1 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input1.csv");
        assert input.size() == 1;
        List<Instruction> instructions = Arrays.stream(input.get(0).split(", ")).map(s -> {
            Instruction.Turn turn = Instruction.Turn.of(s.substring(0, 1));
            int numberOfSteps = Integer.parseInt(s.substring(1));
            return new Instruction(turn, numberOfSteps);
        }).collect(Collectors.toList());
        printDistance(instructions);
        printDistanceFirstLocationVisitingTwice(instructions);
    }

    private static void printDistance(List<Instruction> instructions) {
        Point location = new Point(0, 0);
        Direction direction = Direction.NORTH;
        for (Instruction instruction : instructions) {
            direction = getNewDirection(direction, instruction.getTurn());
            location = getNewLocation(location, direction, instruction.getNumberOfSteps());
        }
        int distance = Math.abs(location.x) + Math.abs(location.y);
        System.out.println("Distance: " + distance);
    }

    private static void printDistanceFirstLocationVisitingTwice(List<Instruction> instructions) {
        Set<Point> visitedLocations = new HashSet<>();
        Point location = new Point(0, 0);
        Direction direction = Direction.NORTH;
        visitedLocations.add(location);
        boolean visitedTwice = false;
        for (Instruction instruction : instructions) {
            direction = getNewDirection(direction, instruction.getTurn());

            int dx;
            int dy;
            switch (direction) {
                case NORTH: {
                    dx = 0;
                    dy = -1;
                } break;
                case EAST: {
                    dx = 1;
                    dy = 0;
                } break;
                case SOUTH: {
                    dx = 0;
                    dy = 1;
                } break;
                case WEST: {
                    dx = -1;
                    dy = 0;
                } break;
                default: throw new IllegalArgumentException("Invalid direction");
            }

            for (int i = 0; i < instruction.getNumberOfSteps(); i++) {
                location = new Point(location);
                location.translate(dx, dy);
                if (visitedLocations.contains(location)) {
                    visitedTwice = true;
                    break;
                }
                visitedLocations.add(location);
            }

            if (visitedTwice) break;
        }
        int distance = Math.abs(location.x) + Math.abs(location.y);
        System.out.println("Distance first location visiting twice: " + distance);
    }

    private static Direction getNewDirection(Direction direction, Instruction.Turn turn) {
        if (turn.equals(Instruction.Turn.L)) {
            switch (direction) {
                case NORTH: return Direction.WEST;
                case EAST: return Direction.NORTH;
                case SOUTH: return Direction.EAST;
                case WEST: return Direction.SOUTH;
                default: throw new IllegalArgumentException("Invalid direction");
            }
        }
        else if (turn.equals(Instruction.Turn.R)) {
            switch (direction) {
                case NORTH: return Direction.EAST;
                case EAST: return Direction.SOUTH;
                case SOUTH: return Direction.WEST;
                case WEST: return Direction.NORTH;
                default: throw new IllegalArgumentException("Invalid direction");
            }
        }
        else {
            throw new IllegalArgumentException("Invalid turn value");
        }
    }


    private static Point getNewLocation(Point location, Direction direction, int numberOfSteps) {
        Point newLocation = new Point(location);
        switch (direction) {
            case NORTH: newLocation.translate(0, -numberOfSteps); break;
            case EAST: newLocation.translate(numberOfSteps, 0); break;
            case SOUTH: newLocation.translate(0, numberOfSteps); break;
            case WEST: newLocation.translate(-numberOfSteps, 0); break;
        }
        return newLocation;
    }

}