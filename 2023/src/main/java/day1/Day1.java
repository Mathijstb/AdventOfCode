package day1;

import fileUtils.FileReader;

import java.util.*;
import java.util.regex.Pattern;

public class Day1 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");
        findSumOfCalibrationValues(lines);
        System.out.println();
        findSumOfCalibrationValues2(lines);
    }

    private static void findSumOfCalibrationValues(List<String> lines) {
        var firstNumberPattern = Pattern.compile("\\d");
        var lastNumberPattern = Pattern.compile("(\\d)(?!.*\\d)");
        var numbers = lines.stream().map(line -> {
            var firstNumberMatcher = firstNumberPattern.matcher(line);
            var lastNumberMatcher = lastNumberPattern.matcher(line);
            var firstNumber = firstNumberMatcher.find() ? firstNumberMatcher.group(0) : null;
            var lastNumber = lastNumberMatcher.find() ? lastNumberMatcher.group(0) : null;
            return Integer.parseInt(firstNumber + lastNumber);
        }).reduce(0, Integer::sum);
        System.out.print("Sum: " + numbers);
    }

    private static void findSumOfCalibrationValues2(List<String> lines) {
        var firstNumberPattern = Pattern.compile("(\\d|one|two|three|four|five|six|seven|eight|nine)");
        var lastNumberPattern = Pattern.compile("(\\d|one|two|three|four|five|six|seven|eight|nine)(?!.*(\\d|one|two|three|four|five|six|seven|eight|nine))");
        var numbers = lines.stream().map(line -> {
            var firstNumberMatcher = firstNumberPattern.matcher(line);
            var lastNumberMatcher = lastNumberPattern.matcher(line);
            var firstNumber = firstNumberMatcher.find() ? firstNumberMatcher.group(0) : null;
            String lastNumber = null;
            int start = 0;
            while (lastNumberMatcher.find(start)) {
                lastNumber = lastNumberMatcher.group(0);
                start += 1;
            }
            assert firstNumber != null;
            assert lastNumber != null;
            int firstDigit = isNumber(firstNumber) ? Integer.parseInt(firstNumber) : word2Integer(firstNumber);
            int lastDigit = isNumber(lastNumber) ? Integer.parseInt(lastNumber) : word2Integer(lastNumber);
            return Integer.parseInt("" + firstDigit + lastDigit);
        }).reduce(0, Integer::sum);
        System.out.print("Sum: " + numbers);
    }

    private static boolean isNumber(String word) {
        try {
            Integer.parseInt(word);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static int word2Integer(String word) {
        return switch (word) {
            case "one" -> 1;
            case "two" -> 2;
            case "three" -> 3;
            case "four" -> 4;
            case "five" -> 5;
            case "six" -> 6;
            case "seven" -> 7;
            case "eight" -> 8;
            case "nine" -> 9;
            default -> throw new IllegalArgumentException("Illegal input string");
        };
    }
}
