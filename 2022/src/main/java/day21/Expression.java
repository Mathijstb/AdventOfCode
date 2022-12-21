package day21;

import java.util.function.BiFunction;

public record Expression(String monkey1, String monkey2, BiFunction<Long, Long, Long> function) {
}
