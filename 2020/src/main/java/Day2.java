import fileUtils.FileReader;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

public class Day2 {

    @Value
    private static class Line {

        int MinValue;
        int MaxValue;
        char Character;
        String password;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");
        List<Line> parsedLines = lines.stream().map(line -> {
            int minValueEndIndex = line.indexOf("-");

            int maxValueStartIndex = line.indexOf("-") + 1;
            int maxValueEndIndex = line.indexOf(" ");

            int minValue = Integer.parseInt(line.substring(0, minValueEndIndex));
            int maxValue = Integer.parseInt(line.substring(maxValueStartIndex, maxValueEndIndex));

            int characterIndex = maxValueEndIndex + 1;
            char character = line.charAt(characterIndex);

            int passwordIndex = line.indexOf(":") + 2;
            String password = line.substring(passwordIndex);

            return new Line(minValue, maxValue, character, password);
        }).collect(Collectors.toList());

        System.out.println(parsedLines.stream().map(Day2::isValidPassword)
                .filter(x -> x).count());
    }

    private static boolean isValidPassword(Line line) {
        String password = line.getPassword();
        int count = 0;
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) == line.getCharacter()) {
                count++;
            }
        }
        return count >= line.getMinValue() && count <= line.getMaxValue();
    }

    private static boolean isValidPassword2(Line line) {
        String password = line.getPassword();
        int count = 0;
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) == line.getCharacter() && (i+1 == line.getMinValue() || i+1 == line.getMaxValue())) {
                count++;
            }
        }
        return count == 1;
    }
}
