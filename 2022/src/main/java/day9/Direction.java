package day9;

import java.util.Arrays;

public enum Direction {
    LEFT("L"),
    RIGHT("R"),
    UP("U"),
    DOWN("D");

    final String dir;

    Direction(String dir) {
        this.dir = dir;
    }

    public static Direction of(String input) {
        return Arrays.stream(Direction.values())
                .filter(d -> d.dir.equals(input)).findFirst().orElseThrow();
    }
}
