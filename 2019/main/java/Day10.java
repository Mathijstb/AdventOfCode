import lombok.Value;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day10 {

    private enum Space {
        EMPTY('.'),
        ASTEROID('#');

        private final Character character;

        Space(Character character) {
            this.character = character;
        }

        public static Space fromText(Character character) {
            return Arrays.stream(values()).filter(s -> s.character.equals(character)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid value"));
        }
    }

    @Value
    private static class Angle {
        int dx;
        int dy;
    }

    private static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b,a % b);
    }

    private static List<List<Space>> grid = new ArrayList<>();
    private static Map<Point, Space> coordMap = new HashMap<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input10.csv");
        readGrid(lines);
        Point stationCoord = determineBestMonitoringPosition();
        //Point stationCoord = new Point(11, 13);
        executeLaser(stationCoord);
    }

    private static void executeLaser(Point stationCoord) {
        Map<Angle, List<Point>> angleMap = getAngleMap(determinePointsWithAsteroids(), stationCoord);
        List<Angle> angles = new ArrayList<>(angleMap.keySet());
        angles.sort(Comparator.comparing(angle -> ((Math.toDegrees(Math.atan2(angle.dx, angle.dy)) + 360) % 360)));
        angles.forEach(angle -> {
            List<Point> points = angleMap.get(angle);
            points.sort(Comparator.comparing(point -> determineDistance(stationCoord, point)));
        });
        int angleIndex = 0;
        int shotNumber = 1;
        while (true) {
            Angle angle = angles.get(angleIndex);
            List<Point> points = angleMap.get(angle);
            if (points.size() > 0) {
                System.out.printf("Shoot: %s %s%n", shotNumber, points.get(0));
                points.remove(0);
                shotNumber += 1;
            }
            if (shotNumber > coordMap.size()) break;
            angleIndex = (angleIndex + 1) % angles.size();
        }
    }

    private static Map<Angle, List<Point>> getAngleMap(List<Point> pointsWithAsteroids, Point point) {
        Map<Angle, List<Point>> angleMap = new HashMap<>();
        pointsWithAsteroids.stream().filter(otherPoint -> !otherPoint.equals(point)).forEach(otherPoint -> {
            Angle angle = determineAngle(point, otherPoint);
            if (!angleMap.containsKey(angle)) angleMap.put(angle, new ArrayList<>());
            angleMap.get(angle).add(otherPoint);
        });
        return angleMap;
    }

    private static List<Point> determinePointsWithAsteroids() {
       return coordMap.entrySet().stream().filter(entry -> entry.getValue().equals(Space.ASTEROID))
               .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private static Point determineBestMonitoringPosition() {
        Map<Point, Integer> pointToNumberOfAsteroidsMap = new HashMap<>();
        List<Point> pointsWithAsteroids = determinePointsWithAsteroids();
        pointsWithAsteroids.forEach(point -> {
            Map<Angle, List<Point>> angleMap = getAngleMap(pointsWithAsteroids, point);
            pointToNumberOfAsteroidsMap.put(point, angleMap.keySet().size());
        });
        Map.Entry<Point, Integer> maxEntry = pointToNumberOfAsteroidsMap.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElseThrow(() -> new IllegalStateException("Max expected"));
        Point monitorCoord = maxEntry.getKey();
        System.out.println("Best point: " + monitorCoord);
        System.out.println("Number of asteroids: " + maxEntry.getValue());
        return monitorCoord;
    }

    private static Angle determineAngle(Point point, Point otherPoint) {
        int dy = -(otherPoint.y - point.y);
        int dx = otherPoint.x - point.x;
        int length = Math.abs(gcd(dx, dy));
        return new Angle(dx / length, dy / length);
    }

    private static double determineDistance(Point point, Point otherPoint) {
        int dy = otherPoint.y - point.y;
        int dx = otherPoint.x - point.x;
        return Math.abs(gcd(dx, dy));
    }

    private static void readGrid(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            List<Space> row = new ArrayList<>();
            for (int j = 0; j < line.length(); j++) {
                Space space = Space.fromText(line.charAt(j));
                row.add(space);
                coordMap.put(new Point(j, i), space);
            }
            grid.add(row);
        };
    }


}
