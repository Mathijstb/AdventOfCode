package day7;

import java.util.Arrays;

public enum Strength {
    FIVE_OF_A_KIND(7),
    FOUR_OF_A_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_A_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1);

    public final int value;

    Strength(int value) {
        this.value = value;
    }
}
