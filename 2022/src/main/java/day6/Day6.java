package day6;

import fileUtils.FileReader;

import java.util.HashSet;
import java.util.List;

public class Day6 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input6.csv");
        var line = lines.stream().findFirst().orElseThrow();
        findFirstMarker(line, 4);
        findFirstMarker(line, 14);
    }

    private static void findFirstMarker(String line, int size) {
        for (int i = 0; i < line.length()-size; i++) {
            var sub = line.substring(i, i + size);
            var chars = new HashSet<>();
            for (int j = 0; j < sub.length(); j++) {
                chars.add(sub.charAt(j));
            }
            if (chars.size() == size) {
                System.out.println("Index: " + (i + size));
                break;
            }

        }
    }
}
