package day11;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;

public record Monkey (
    Integer number,
    LinkedList<Long> items,
    Function<Long, Long> operation,
    Predicate<Long> check,
    Long divisor,
    int throwToIfTrue,
    int throwToIfFalse) {

    int getMonkeyNumberToThrowTo(long item) {
        return check.test(item) ? throwToIfTrue : throwToIfFalse;
    }

    public Monkey copy() {
        return new Monkey(number, new LinkedList<>(items), operation, check, divisor, throwToIfTrue, throwToIfFalse);
    }
}
