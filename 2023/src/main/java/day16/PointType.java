package day16;

import java.util.Arrays;

public enum PointType {
    EMPTY('.'),
    SPLITTER_V('|'),
    SPLITTER_H('-'),
    MIRROR_F('/'),
    MIRROR_B('\\');

    public final Character character;

    PointType(Character character) {
        this.character = character;
    }

    public static PointType of(Character character) {
        return Arrays.stream(PointType.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }

}
