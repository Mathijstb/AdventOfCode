package day3;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Day3 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input3.csv");
        long result = executeMulInstructions(lines);
        System.out.println("Result: " + result);
        // 181459516 too high
    }

    private static long executeMulInstructions(List<String> lines) {
        return lines.stream()
                .map(Day3::executeMulInstructions)
                .reduce(0L, Long::sum);
    }

    private static final Pattern pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)");

    private static long executeMulInstructions(String line) {
        List<String> allMatches = new ArrayList<>();
        var matcher = pattern.matcher(line);
        while (matcher.find()) {
            allMatches.add(matcher.group());
        }
        return allMatches.stream().map(s -> {
            var parts = s.split(",");
            var number1 = Long.parseLong(parts[0].split("\\(")[1]);
            var number2 = Long.parseLong(parts[1].split("\\)")[0]);
            return number1 * number2;
        }).reduce(0L, Long::sum);
    }

}
