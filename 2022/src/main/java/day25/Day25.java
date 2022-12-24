package day25;

import fileUtils.FileReader;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day25 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input25.csv");
        calculateSum(lines);
    }

    private static void calculateSum(List<String> lines) {
        long sum = lines.stream().map(Day25::convertToDecimal).reduce(0L, Long::sum);
        String snafu = converToSnafu(sum);
        System.out.println("Snafu sum: " + snafu);
    }

    private static long convertToDecimal(String line) {
        var positive = line.replace('=', '0').replace('-', '0');
        var negative = line.chars().mapToObj(c -> switch (c) {
                case '=' -> '2';
                case '-' -> '1';
                default -> '0';
            }).map(Object::toString).collect(Collectors.joining());
        return Long.parseLong(new BigInteger(positive, 5).subtract(new BigInteger(negative, 5)).toString(10));
    }

    private static String converToSnafu(long number) {
        var resultSize = new BigInteger(String.valueOf(number)).toString(5).length();
        var helpNumber = new BigInteger("2".repeat(resultSize), 5);
        var result = new BigInteger(String.valueOf(Long.parseLong(helpNumber.toString()) + number)).toString(5);
        assert (result.length() == resultSize);
        return result.chars()
                .mapToObj(c -> (char) c)
                .map(character -> switch (character) {
                    case '0' -> "=";
                    case '1' -> "-";
                    default -> String.valueOf(Character.getNumericValue(character) - 2);
                }).collect(Collectors.joining());
    }


}