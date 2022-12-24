package day24;

import java.util.Arrays;

public enum PointType {
    WALL('#'),
    AIR('.'),
    BLIZZARD_LEFT('<'),
    BLIZZARD_RIGHT('>'),
    BLIZZARD_UP('^'),
    BLIZZARD_DOWN('v'),
    START('S'),
    GOAL('G');

    private final Character character;

    PointType(Character character) {
        this.character = character;
    }

    public static PointType of(Character character) {
        return Arrays.stream(PointType.values()).filter(pointType -> pointType.character.equals(character)).findFirst().orElseThrow();
    }
}
