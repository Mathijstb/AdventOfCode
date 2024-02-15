package day23;

import java.util.Arrays;

public enum PointType {
    PATH('.'),
    FOREST('#'),
    SLOPE_UP('^'),
    SLOPE_LEFT('<'),
    SLOPE_RIGHT('>'),
    SLOPE_DOWN('v'),
    ROUTE('O');

    public final Character character;

    PointType(Character character) {
        this.character = character;
    }

    public static PointType of(Character character) {
        return Arrays.stream(PointType.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }

}
