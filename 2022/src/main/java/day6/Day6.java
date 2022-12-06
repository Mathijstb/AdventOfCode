package day6;

import fileUtils.FileReader;

import java.util.List;
import java.util.regex.Pattern;

public class Day6 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input6.csv");
        var line = lines.stream().findFirst().orElseThrow();
        findFirstMarker(line, 4);
        findFirstMarker(line, 14);
    }

    private static void findFirstMarker(String line, int size) {
        var regex = createRegex(size);
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("Index: " + matcher.end());
        }
    }

    private static String createRegex(int size) {
        //e.g. for size 4: "(.)(?!\\1)(.)(?!\\1|\\2)(.)(?!\\1|\\2|\\3)(.)"
        StringBuilder sb = new StringBuilder("(.)");
        for (int i = 1; i < size; i++) {
            sb.append("(?!\\1");
            for (int j = 2; j < i+1; j++) {
                sb.append("|\\").append(j);
            }
            sb.append(")(.)");
        }
        return sb.toString();
    }
}
