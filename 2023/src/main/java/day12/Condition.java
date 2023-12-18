package day12;

import java.util.Arrays;

public enum Condition {
    OPERATIONAL('.'),
    DAMAGED('#'),
    UNKNOWN('?');

    public final Character character;

    Condition(Character character) {
        this.character = character;
    }

    public static Condition of(Character character) {
        return Arrays.stream(Condition.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
