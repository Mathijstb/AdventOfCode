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

    Optional<Pair> ancestor = Optional.empty();

    public Pair(Optional<Integer> numericalValue, Optional<Pair> left, Optional<Pair> right) {
        this.numericalValue = numericalValue;
        this.left = left;
        this.right = right;
    }

    public void addValue(int value) {
        setNumericalValue(Optional.of(numericalValue.orElseThrow() + value));
    }

    public int getNestLevel() {
        return ancestor.map(a -> a.getNestLevel() + 1).orElse(0);
    }

    public boolean isLeftPair() {
        return ancestor.isPresent() && ancestor.get().left.orElseThrow().equals(this);
    }

    public boolean isRightPair() {
        return ancestor.isPresent() && ancestor.get().right.orElseThrow().equals(this);
    }

    private Pair findLeftMostNestedValue() {
        if (numericalValue.isPresent()) return this;
        return left.orElseThrow().findLeftMostNestedValue();
    }

    private Pair findRightMostNestedValue() {
        if (numericalValue.isPresent()) return this;
        return right.orElseThrow().findRightMostNestedValue();
    }

    public Optional<Pair> findLeftNumericalValue(boolean isRoot) {
        if (!isRoot && numericalValue.isPresent()) {
            return Optional.of(this);
        }
        else {
            if (isLeftPair()) {
                return ancestor.orElseThrow().findLeftNumericalValue(false);
            }
            else if (isRightPair()) {
                return Optional.of(ancestor.orElseThrow().left.orElseThrow().findRightMostNestedValue());
            }
            else {
                return Optional.empty();
            }
        }
    }

    public Optional<Pair> findRightNumericalValue(boolean isRoot) {
        if (!isRoot && numericalValue.isPresent()) {
            return Optional.of(this);
        }
        else {
            if (isLeftPair()) {
                return Optional.of(ancestor.orElseThrow().right.orElseThrow().findLeftMostNestedValue());
            }
            else if (isRightPair()) {
                return ancestor.orElseThrow().findRightNumericalValue(false);
            }
            else {
                return Optional.empty();
            }
        }
    }

    @Override
    public String toString() {
        return numericalValue.isPresent() ? String.valueOf(numericalValue.get()) : String.format("[%s,%s]", left.orElseThrow(), right.orElseThrow());
    }
}
