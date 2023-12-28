package day15;

import java.util.Arrays;

public enum Operation {
    REMOVE('-'),
    INSERT('=');

    public final Character character;

    Operation(Character character) {
        this.character = character;
    }

    public static Operation of(Character character) {
        return Arrays.stream(Operation.values()).filter(v -> v.character.equals(character)).findFirst().orElseThrow();
    }

}
