package day6;

import fileUtils.FileReader;

import java.util.List;
import java.util.stream.Collectors;

public class Day6 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input6.csv");
        var line = lines.stream().findFirst().orElseThrow();
        findFirstMarker(line, 4);
        findFirstMarker(line, 14);
    }

    private static void findFirstMarker(String line, int size) {
        for (int i = 0; i < line.length()-size; i++) {
            var characterSet = line.substring(i, i + size)
                    .chars()
                    .mapToObj(chr -> (char) chr)
                    .collect(Collectors.toSet());
            if (characterSet.size() == size) {
                System.out.println("Index: " + (i + size));
                break;
            }
        }
    }
}
