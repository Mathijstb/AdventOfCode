import fileUtils.FileReader;

import java.util.List;

public class Day1 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");
        String line = lines.stream().findFirst().orElseThrow();
        determineFloor(line);
    }

    private static void determineFloor(String line) {
        int floor = 0;
        Integer firstBasementIndex = null;
        for (int i = 0; i < line.length(); i++) {
            switch (line.charAt(i)) {
                case '(': floor += 1; break;
                case ')': {
                    floor -= 1;
                    if (firstBasementIndex == null && floor < 0) {
                        firstBasementIndex = i + 1;
                    }
                } break;
            }
        }
        System.out.println("Santa entered basement on step: " + firstBasementIndex);
        System.out.println("Santa ended on floor: " + floor);
    }
}
