package day22;

import java.util.Arrays;

public enum Facing {
    RIGHT(0),
    DOWN(1),
    LEFT(2),
    UP(3);

    final int index;

    Facing(int index) {
        this.index = index;
    }

    public static Facing of(int index) {
        return Arrays.stream(Facing.values()).filter(value -> value.index == index).findFirst().orElseThrow();
    }
}
