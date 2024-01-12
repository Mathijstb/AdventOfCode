package day18;

import java.util.Arrays;

public enum Direction {
    L("L"),
    R("R"),
    D("D"),
    U("U");

    public final String direction;

    Direction(String direction) {
        this.direction = direction;
    }

    public static Direction of(String direction) {
        return Arrays.stream(Direction.values()).filter(v -> v.direction.equals(direction)).findFirst().orElseThrow();
    }
}
