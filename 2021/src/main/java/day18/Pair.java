package day18;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Pair {

    Optional<Integer> numericalValue;

    Optional<Pair> left;

    Optional<Pair> right;

    Optional<Pair> ancestor;

    public Pair(Optional<Integer> numericalValue, Optional<Pair> left, Optional<Pair> right) {
        this.numericalValue = numericalValue;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return numericalValue.isPresent() ? String.valueOf(numericalValue.get()) : String.format("[%s,%s]", left.orElseThrow(), right.orElseThrow());
    }
}
