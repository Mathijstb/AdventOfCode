import lombok.Data;
import lombok.Value;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class Day12 {

    private enum Direction {
        NORTH('N'),
        EAST('E'),
        SOUTH('S'),
        WEST('W'),
        LEFT('L'),
        RIGHT('R'),
        FORWARD('F');

        private final char character;

        Direction(char character) {
            this.character = character;
        }
    }

    @Value
    private static class Instruction {
        Direction direction;
        int value;
    }

    @Data
    private static class Ship {
        int x;
        int y;
        int angle = 90;
    }

    @Data
    private static class Waypoint {
        int x = 10;
        int y = -1;

        public void rotate(int angle) {
            double rad = Math.toRadians(angle);
            int newX = (int) Math.round(x * Math.cos(rad) - y * Math.sin(rad));
            int newY = (int) Math.round(x * Math.sin(rad) + y * Math.cos(rad));
            x = newX;
            y = newY;
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input12.csv");
        List<Instruction> instructions = getInstructions(lines);

        Ship ship = new Ship();
        //executeInstructions(instructions, ship);
        Waypoint waypoint = new Waypoint();

        TestPane.Test();
        executeInstructions2(instructions, ship, waypoint);
        System.out.printf("x: %s, y: %s, angle: %s, distance: %s%n", ship.x, ship.y, ship.angle, Math.abs(ship.x) + Math.abs(ship.y));
    }

    private static List<Instruction> getInstructions(List<String> lines) {
        return lines.stream().map(line -> {
            char character = line.charAt(0);
            int value = Integer.parseInt(line.substring(1));
            Direction direction = Arrays.stream(Day12.Direction.values()).filter(s -> s.character == character).findFirst().orElseThrow();
            return new Instruction(direction, value);
        }).collect(Collectors.toList());
    }

    private static void executeInstructions2(List<Instruction> instructions, Ship ship, Waypoint waypoint) {
        for (Instruction instruction: instructions) {
            switch (instruction.direction) {
                case NORTH: waypoint.y -= instruction.value; break;
                case EAST: waypoint.x += instruction.value; break;
                case SOUTH: waypoint.y += instruction.value; break;
                case WEST: waypoint.x -= instruction.value; break;
                case LEFT: waypoint.rotate(-instruction.value); break;
                case RIGHT: waypoint.rotate(instruction.value); break;
                case FORWARD: moveForward2(ship, waypoint, instruction.value); break;
            }

            //draw
            TestPane.setShipCoordinate(new Point(ship.x, ship.y));
            TestPane.setWaypointCoordinate(new Point(waypoint.x, waypoint.y));
            if (instruction.direction == Direction.FORWARD) {
                try {
                    sleep(100);
                    TestPane.frame.repaint();
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    private static void moveForward2(Ship ship, Waypoint waypoint, int value) {
        ship.x = ship.x + waypoint.x * value;
        ship.y = ship.y + waypoint.y * value;
    }

//    private static void executeInstructions(List<Instruction> instructions, Ship ship) {
//        for (Instruction instruction: instructions) {
//            switch (instruction.direction) {
//                case NORTH: ship.y -= instruction.value; break;
//                case EAST: ship.x += instruction.value; break;
//                case SOUTH: ship.y += instruction.value; break;
//                case WEST: ship.x -= instruction.value; break;
//                case LEFT: ship.angle = (ship.angle - instruction.value) % 360; break;
//                case RIGHT: ship.angle = (ship.angle + instruction.value) % 360; break;
//                case FORWARD: moveForward(ship, instruction.value); break;
//            }
//
//        }
//    }
//
//    private static void moveForward(Ship ship, int value) {
//        ship.x = ship.x + (int) (Math.sin(Math.toRadians(ship.angle)) * value);
//        ship.y = ship.y - (int) (Math.cos(Math.toRadians(ship.angle)) * value);
//    }
}
