package day5;

import fileUtils.FileReader;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day5 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input5.csv");
        List<Line> lines = readLines(input);
        System.out.println(lines);

        //Print overlap of at least 2 for horizontal/vertical lines only
        printNumberOfPointsWithOverlapAtLeast2(lines.stream().filter(Line::isStraight).collect(Collectors.toList()));

        //Print overlap of at least 2 including all lines
        printNumberOfPointsWithOverlapAtLeast2(lines);
    }

    private static List<Line> readLines(List<String> lines) {
        return lines.stream().map(line -> {
            String[] parts = line.split(" -> ");
            String[] startParts = parts[0].split(",");
            String[] endParts = parts[1].split(",");
            Point start = new Point(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]));
            Point finish = new Point(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1]));
            return new Line(start, finish);
        }).collect(Collectors.toList());
    }

    private static void printNumberOfPointsWithOverlapAtLeast2(List<Line> lines) {
        Map<Point, Integer> pointToOverlapMap = new HashMap<>();
        lines.forEach(line -> {
            List<Point> points = line.getPoints();
            points.forEach(point ->  {
                int numberOfOverlaps = pointToOverlapMap.containsKey(point) ? pointToOverlapMap.get(point) + 1 : 1;
                pointToOverlapMap.put(point, numberOfOverlaps);
            });
        });
        long numberOverOverlapsAtLeast2 = pointToOverlapMap.entrySet().stream().filter(entry -> entry.getValue() >= 2).count();
        System.out.println("number of overlaps: " + numberOverOverlapsAtLeast2);
    }

}
