package day2;

import java.util.Arrays;

public enum Direction {
    FORWARD("forward"),
    UP("up"),
    DOWN("down");

    String dir;

    Direction(String dir) {
        this.dir = dir;
    }

    public static Direction of(String dir) {
        return Arrays.stream(Direction.values()).filter(d -> d.dir.equals(dir)).findFirst().orElseThrow();
    }
}
