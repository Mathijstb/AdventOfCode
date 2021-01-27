import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input8.csv");
        long codeLength = getCodeLength(lines);
        long memoryLength = getMemoryLength(lines);
        System.out.println("code length: " + codeLength);
        System.out.println("memory length: " + memoryLength);
        System.out.println("difference: " + (codeLength - memoryLength));
        List<String> codeRepresentations = getCodeRepresentations(lines);
        long newCodeLength = getCodeLength(codeRepresentations);
        System.out.println();
        System.out.println("new code length: " + newCodeLength);
        System.out.println("old code length: " + codeLength);
        System.out.println("difference: " + (newCodeLength - codeLength));
    }

    private static List<String> getCodeRepresentations(List<String> lines) {
        return lines.stream().map(line ->
                "\"" + line.replaceAll("\\\\", "\\\\\\\\")
                           .replaceAll("\"","\\\\\"") + "\""
        ).collect(Collectors.toList());
    }

    public static long getCodeLength(List<String> lines) {
        return lines.stream().map(String::length).reduce(0, Integer::sum);
    }

    public static long getMemoryLength(List<String> lines) {
        Pattern pattern1 = Pattern.compile("\\\\\\\\");
        Pattern pattern2 = Pattern.compile("\\\\\"");
        Pattern pattern3 = Pattern.compile("\\\\x[0-9a-f]{2}");

        return lines.stream().map(line -> line.substring(1, line.length() - 1))
                             .map(line -> {
            Matcher matcher1 = pattern1.matcher(line);
            Matcher matcher2 = pattern2.matcher(line);
            Matcher matcher3 = pattern3.matcher(line);
            int count1 = 0, count2 = 0, count3 = 0;
            while (matcher1.find()) { count1++; }
            while (matcher2.find()) { count2++; }
            while (matcher3.find()) { count3++; }
            return line.length() - count1 - count2 - count3 * 3;
        }).reduce(0, Integer::sum);
    }
    // 1340 too high

}
