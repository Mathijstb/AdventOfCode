package day2;

import fileUtils.FileReader;

import java.util.Arrays;
import java.util.List;

public class Day2 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");
        var reports = readReports(lines);
        var numberOfSafeReports = determineNumberOfSafeReports(reports);
        System.out.println("Number of safe reports: " + numberOfSafeReports);
    }

    private static List<List<Integer>> readReports(List<String> lines) {
        return lines.stream().map(line ->
                Arrays.stream(line.split(" "))
                        .map(Integer::parseInt).toList()
        ).toList();
    }

    private static long determineNumberOfSafeReports(List<List<Integer>> reports) {
        return reports.stream().filter(Day2::isSafe).count();
    }

    private static boolean isSafe(List<Integer> report) {
        int previousDirection = 0;
        for (int i = 1; i < report.size(); i++) {
            var diff = report.get(i - 1) - report.get(i);
            if (diff == 0 || Math.abs(diff) > 3) return false;
            int direction = (diff < 0 ? - 1 : 1);
            if (previousDirection != 0 && direction != previousDirection) return false;
            previousDirection = direction;
        }
        return true;
    }
}
