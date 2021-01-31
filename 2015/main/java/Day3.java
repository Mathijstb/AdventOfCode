import fileUtils.FileReader;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3 {

    private static Set<Point> visitedLocations = new HashSet<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input3.csv");
        String line = lines.stream().findFirst().orElseThrow();
        findVisitedLocations(line);
    }

    private static void findVisitedLocations(String line) {
        Point coordinate = new Point(0, 0);
        visitedLocations.add(coordinate);
        Point santaCoordinate = coordinate.getLocation();
        Point robotCoordinate = coordinate.getLocation();
        for (int i = 0; i < line.length(); i++) {
            if (i % 2 == 0) {
                santaCoordinate = getNextCoordinate(santaCoordinate, line.charAt(i));
                visitedLocations.add(santaCoordinate);
            }
            else{
                robotCoordinate = getNextCoordinate(robotCoordinate, line.charAt(i));
                visitedLocations.add(robotCoordinate);
            }
        }
        System.out.println("number of visited locations: " + visitedLocations.size());
    }

    private static Point getNextCoordinate(Point coordinate, char character) {
        Point nextCoordinate = coordinate.getLocation();
        switch (character) {
            case '^': nextCoordinate.translate(0, -1); break;
            case '>': nextCoordinate.translate(1, 0); break;
            case 'v': nextCoordinate.translate(0, 1); break;
            case '<': nextCoordinate.translate(-1, 0); break;
            default: throw new IllegalStateException("invalid character");
        }
        return nextCoordinate;
    }
}
