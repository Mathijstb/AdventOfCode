package day13;

import java.util.Arrays;

public enum FoldDirection {
    HORIZONTAL("y"), VERTICAL("x");

    String direction;

    FoldDirection(String direction) {
        this.direction = direction;
    }

    public static FoldDirection of(String direction) {
        return Arrays.stream(FoldDirection.values()).filter(s -> s.direction.equals(direction)).findFirst().orElseThrow();
    }
}