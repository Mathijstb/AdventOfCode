package day7;

import java.util.Arrays;

public enum Card {
    A(14, 'A'),
    K(13, 'K'),
    Q(12, 'Q'),
    J(11, 'J'),
    T(10, 'T'),
    NINE(9, '9'),
    EIGHT(8, '8'),
    SEVEN(7, '7'),
    SIX(6, '6'),
    FIVE(5, '5'),
    FOUR(4, '4'),
    THREE(3, '3'),
    TWO(2, '2');

    public final int rank;

    public int alternativeRank() {
        return this.equals(J) ? 1 : rank;
    }

    private final Character symbol;

    Card(Integer rank, Character symbol) {
        this.rank = rank;
        this.symbol = symbol;
    }

    public static Card of(Character character) {
        return Arrays.stream(Card.values()).filter(card -> card.symbol.equals(character)).findFirst().orElseThrow();
    }
}
