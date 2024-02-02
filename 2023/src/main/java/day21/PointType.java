package day21;

import java.util.Arrays;

public enum PointType {
    PLOT('.'),
    ROCK('#'),
    START('S'),
    FINISH('O');

    public final Character character;

    PointType(Character character) {
        this.character = character;
    }

    public static PointType of(Character character) {
        return Arrays.stream(PointType.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }

}
