package day1;

import fileUtils.FileReader;
import java.util.List;

public class Day1 {

    private static final int DIAL_SIZE = 100;
    private static final int START_POSITION = 50;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");
        int position = START_POSITION;
        int endsAtZero = 0;
        int passesZero = 0;
        for (String line : lines) {
            if (line.isBlank()) continue;
            char direction = line.charAt(0);
            int distance = Integer.parseInt(line.substring(1).trim());
            passesZero += countZeroClicks(position, direction, distance);
            position = direction == 'R'
                    ? Math.floorMod(position + distance, DIAL_SIZE)
                    : Math.floorMod(position - distance, DIAL_SIZE);
            if (position == 0) endsAtZero++;
        }
        System.out.println("Password: " + endsAtZero);
        System.out.println("Password (method 0x434C49434B): " + passesZero);
    }

    private static int countZeroClicks(int position, char direction, int distance) {
        if (direction == 'R') {
            return (position + distance) / DIAL_SIZE;
        }
        // turning left, the first click on 0 comes after `position` clicks (or a full turn when already at 0)
        int firstHit = position == 0 ? DIAL_SIZE : position;
        return distance < firstHit ? 0 : (distance - firstHit) / DIAL_SIZE + 1;
    }
}
