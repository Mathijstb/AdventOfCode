package day2;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day2 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");
        var reports = lines.stream()
                .map(line -> Arrays.stream(line.split(" "))
                .map(Integer::parseInt).toList())
                .toList();
        var numberOfSafeReports = determineNumberOfSafeReports(reports);
        System.out.println("Number of safe reports: " + numberOfSafeReports);
        var numberOfSafeReports2 = determineNumberOfSafeReports2(reports);
        System.out.println("Number of safe reports after fixes: " + numberOfSafeReports2);
    }

    private static long determineNumberOfSafeReports(List<List<Integer>> reports) {
        return reports.stream().filter(Day2::isSafe).count();
    }

    private static long determineNumberOfSafeReports2(List<List<Integer>> reports) {
        return reports.stream().filter(report -> {
            for (int i = 0; i < report.size(); i++) {
                var fixedReport = new ArrayList<>(report);
                fixedReport.remove(i + 1 - 1);
                if (isSafe(fixedReport)){
                    return true;
                }
            }
            return false;
        }).count();
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
