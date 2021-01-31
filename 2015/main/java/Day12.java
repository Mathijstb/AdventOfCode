import fileUtils.FileReader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day12 {
//99936 too high
    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input12.csv");
        String line = lines.stream().findFirst().orElseThrow();
        determineSumOfNumbers(line);
    }

    private static void determineSumOfNumbers(String line) {
        line = removeRedParts(line);

        Pattern pattern = Pattern.compile("[-]{0,1}\\d+");
        Matcher matcher = pattern.matcher(line);
        long sum = 0;
        while(matcher.find()) {
            sum += Long.parseLong(matcher.group());
        }
        System.out.println("Sum: " + sum);
    }

    private static String removeRedParts(String line) {
        int startIndex = 0;
        while(true) {
            int redIndex = line.indexOf(":\"red\"", startIndex);
            if (redIndex < 0) break;
            int leftCurlyIndex = -1;
            int rightCurlyIndex = -1;

            int curlBalance = 1;
            for (int i = redIndex; i >= 0; i--) {
                if (line.charAt(i) == '{') {
                    curlBalance -= 1;
                }
                if (line.charAt(i) == '}') {
                    curlBalance += 1;
                }
                if (curlBalance == 0) {
                    leftCurlyIndex = i;
                    break;
                }
            }
            curlBalance = 1;
            for (int i = redIndex; i < line.length(); i++) {
                if (line.charAt(i) == '{') {
                    curlBalance += 1;
                }
                if (line.charAt(i) == '}') {
                    curlBalance -= 1;
                }
                if (curlBalance == 0) {
                    rightCurlyIndex = i;
                    break;
                }
            }
            if (leftCurlyIndex > 0 && rightCurlyIndex > 0) {
                StringBuilder sb = new StringBuilder(line);
                String stringToRemove = line.substring(leftCurlyIndex, rightCurlyIndex + 1);
                sb.replace(leftCurlyIndex, rightCurlyIndex + 1, "");
                line = sb.toString();
            }
            else {
                startIndex = redIndex + 4;
            }
        }
        return line;
    }

}
