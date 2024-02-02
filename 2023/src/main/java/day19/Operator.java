package day19;

import java.util.Arrays;

public enum Operator {
    GREATER('>'),
    LESS('<');

    Operator(Character character) {
        this.character = character;
    }

    public final Character character;

    public static Operator of(Character character) {
        return Arrays.stream(Operator.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }
}
