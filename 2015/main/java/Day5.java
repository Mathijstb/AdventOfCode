import fileUtils.FileReader;

import java.util.List;
import java.util.regex.Pattern;

public class Day5 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input5.csv");
        findNumberOfNiceStrings2(lines);
    }

    private static void findNumberOfNiceStrings(List<String> lines) {
        long numberOfNiceStrings = lines.stream().filter(line ->
                Pattern.matches(".*[aeiou].*[aeiou].*[aeiou].*", line) &&
                Pattern.matches(".*([a-z])\\1.*", line) &&
                Pattern.matches("((?!ab|cd|pq|xy).)*", line)).count();
        System.out.println("Number of nice strings: " + numberOfNiceStrings);
    }

    private static void findNumberOfNiceStrings2(List<String> lines) {
        long numberOfNiceStrings = lines.stream().filter(line ->
                Pattern.matches(".*([a-z][a-z]).*\\1.*", line) &&
                Pattern.matches(".*([a-z]).\\1.*", line)).count();
        System.out.println("Number of nice strings: " + numberOfNiceStrings);
    }
}
