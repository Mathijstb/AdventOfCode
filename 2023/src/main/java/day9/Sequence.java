package day9;

import java.util.List;

public record Sequence(List<Long> numbers) {

    public long getFirst() {
        return numbers.get(0);
    }

    public long getLast() {
        return numbers.get(numbers.size() - 1);
    }
}
