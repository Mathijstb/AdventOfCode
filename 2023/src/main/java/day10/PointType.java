package day10;

import java.util.Arrays;

public enum PointType {
    V('|'),
    H('-'),
    NE('L'),
    NW('J'),
    SW('7'),
    SE('F'),
    GROUND('.'),
    LOOP_GROUND(','),
    START('S'),
    LOOP_V('1'),
    LOOP_H('2'),
    LOOP_NE('3'),
    LOOP_NW('4'),
    LOOP_SE('5'),
    LOOP_SW('6');

    public final Character character;

    PointType(Character character) {
        this.character = character;
    }

    public static PointType of(Character character) {
        return Arrays.stream(PointType.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }

}
