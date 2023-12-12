package day10;

import java.util.Arrays;

public enum Pipe {
    V('|'),
    H('-'),
    NE('L'),
    NW('J'),
    SW('7'),
    SE('F'),
    GROUND('.'),
    START('S');

    public final Character character;

    Pipe(Character character) {
        this.character = character;
    }

    public static Pipe of(Character character) {
        return Arrays.stream(Pipe.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }

}
