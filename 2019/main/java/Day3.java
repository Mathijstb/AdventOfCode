import lombok.Value;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day3 {

    private enum Direction {
        LEFT('L'),
        UP('U'),
        RIGHT('R'),
        DOWN('D');

        private final char character;

        Direction(char character) {
            this.character = character;
        }

        public static Direction fromText(char character) {
            return Arrays.stream(values()).filter(t -> t.character == character).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid value"));
        }
    }

    @Value
    private static class Instruction {
        Direction direction;
        int amount;
    }

    @Value
    private static class Path {
        List<Instruction> instructions;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input3.csv");
        List<Path> paths = getPaths(lines);
        findClosestCrossing(paths);
    }

    private static void findClosestCrossing(List<Path> paths) {
        Path path1 = paths.get(0);
        Path path2 = paths.get(1);
        Map<Point, Integer> coords1 = getCoordinates(path1);
        Map<Point, Integer> coords2 = getCoordinates(path2);
        List<Point> crossings = coords1.keySet().stream().filter(coords2::containsKey).collect(Collectors.toList());

        //Point closestCrossing = crossings.stream().min(Comparator.comparing(point -> Math.abs(point.x) + Math.abs(point.y))).orElseThrow();
        //System.out.println("Closest crossing (Manhattan distance): " + closestCrossing);
        //System.out.println("Manhattan distance: " + (Math.abs(closestCrossing.x) + Math.abs(closestCrossing.y)));

        Point closestCrossing = crossings.stream().min(Comparator.comparing(point -> coords1.get(point) + coords2.get(point))).orElseThrow();
        System.out.println("Closest crossing: " + closestCrossing);
        System.out.println("Wire length: " + (coords1.get(closestCrossing) + coords2.get(closestCrossing)));
    }

    private static Map<Point, Integer> getCoordinates(Path path) {
        Map<Point, Integer> coords = new HashMap<>();
        Point coord = new Point(0, 0);
        int numberOfSteps = 0;
        for (Instruction instruction: path.instructions) {
            int dx = 0;
            int dy = 0;
            switch (instruction.direction) {
                case UP: dy = -1; break;
                case DOWN: dy = 1; break;
                case LEFT: dx = -1; break;
                case RIGHT: dx = 1; break;
                default: throw new IllegalStateException("invalid direction");
            }
            for (int i = 0; i < instruction.amount; i++) {
                coord.translate(dx, dy);
                numberOfSteps += 1;
                if (!coords.containsKey(coord)) {
                    coords.put(new Point(coord.x, coord.y), numberOfSteps);
                }
            }
        }
        return coords;
    }

    private static List<Path> getPaths(List<String> lines) {
        return lines.stream().map(line -> new Path(Arrays.stream(line.split(","))
                              .map(s -> new Instruction(Direction.fromText(s.charAt(0)), Integer.parseInt(s.substring(1))))
                              .collect(Collectors.toList()))).collect(Collectors.toList());
    }

}
