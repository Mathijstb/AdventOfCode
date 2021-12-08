package day8;

import java.util.Arrays;

public enum Segment {
    a('a'), b('b'), c('c'), d('d'), e('e'), f('f'), g('g');

    char character;

    Segment(char character) {
        this.character = character;
    }

    public static Segment of(char character) {
        return Arrays.stream(Segment.values()).filter(s -> s.character == character).findFirst().orElseThrow();
    }
}
