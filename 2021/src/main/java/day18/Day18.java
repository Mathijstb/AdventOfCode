package day18;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Day18 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input18.csv");
        List<Pair> pairs = input.stream().map(Day18::readPair).collect(Collectors.toList());
        executePairAddition(pairs);
    }

    private static void executePairAddition(List<Pair> pairs) {
        Pair result = pairs.stream().reduce((pair1, pair2) -> reducePair(combinePair(pair1, pair2))).orElseThrow();
        System.out.println(result);
    }

    private static Pair reducePair(Pair pair) {
        Optional<Pair> explodable = findExplodable(pair);
        Optional<Pair> splittable = findSplittable(pair);
        while (explodable.isPresent() || splittable.isPresent()) {
            while (explodable.isPresent()) {
                explode(explodable.get());
                explodable = findExplodable(pair);
            }
            while (splittable.isPresent()) {
                split(splittable.get());
                splittable = findSplittable(pair);
            }
            explodable = findExplodable(pair);
            splittable = findSplittable(pair);
        }
        return pair;
    }

    private static Optional<Pair> findExplodable(Pair pair) {
        return getPairs(pair, false).stream()
                .filter(p -> p.getNestLevel() >= 4)
                .filter(p -> p.left.orElseThrow().numericalValue.isPresent())
                .filter(p -> p.right.orElseThrow().numericalValue.isPresent()).findFirst();
    }

    private static Optional<Pair> findSplittable(Pair pair) {
        return getPairs(pair, true).stream()
                .filter(p -> p.numericalValue.isPresent() && p.numericalValue.get() >= 10).findFirst();
    }

    private static void explode(Pair pair) {
        pair.findLeftNumericalValue(true).ifPresent(value -> value.addValue(pair.left.orElseThrow().numericalValue.orElseThrow()));
        pair.findRightNumericalValue(true).ifPresent(value -> value.addValue(pair.right.orElseThrow().numericalValue.orElseThrow()));
        Pair ancestor = pair.ancestor.orElseThrow();
        if (ancestor.left.orElseThrow().equals(pair)) {
            ancestor.setLeft(Optional.of(new Pair(Optional.of(0), Optional.empty(), Optional.empty(), Optional.of(ancestor))));
        }
        else {
            ancestor.setRight(Optional.of(new Pair(Optional.of(0), Optional.empty(), Optional.empty(), Optional.of(ancestor))));
        }
    }

    private static void split(Pair pair) {
        int value = pair.getNumericalValue().orElseThrow();
        int leftValue = value / 2;
        int rightValue = (value / 2) * 2 == value ? value / 2 : value / 2 + 1;
        Pair newPair = combinePair(new Pair(Optional.of(leftValue), Optional.empty(), Optional.empty()),
                                   new Pair(Optional.of(rightValue), Optional.empty(), Optional.empty()));
        Pair ancestor = pair.getAncestor().orElseThrow();
        newPair.setAncestor(Optional.of(ancestor));
        if (ancestor.left.orElseThrow() == pair) {
            ancestor.setLeft(Optional.of(newPair));
        }
        else {
            ancestor.setRight(Optional.of(newPair));
        }
    }

    private static List<Pair> getPairs(Pair pair, boolean includeNumerical) {
        List<Pair> result = new ArrayList<>();
        if (pair.numericalValue.isPresent()) {
            if (includeNumerical) result.add(pair);
        }
        else {
            result.add(pair);
            result.addAll(getPairs(pair.left.orElseThrow(), includeNumerical));
            result.addAll(getPairs(pair.right.orElseThrow(), includeNumerical));
        }
        return result;
    }

    private static Pair readPair(String expression) {
        char character = expression.charAt(0);
        if (character == '[') {
            // return Pair with left Pair and right Pair
            int commaIndex = findCommaIndex(expression);
            int closingIndex = findClosingIndex(expression);
            return combinePair(readPair(expression.substring(1, commaIndex)), readPair(expression.substring(commaIndex + 1, closingIndex)));
        }
        else {
            // return Pair with numerical value
            return new Pair(Optional.of(Integer.parseInt(String.valueOf(character))), Optional.empty(), Optional.empty());
        }
    }

    private static Pair combinePair(Pair leftPair, Pair rightPair) {
        Pair result = new Pair(Optional.empty(), Optional.of(leftPair), Optional.of(rightPair));
        leftPair.setAncestor(Optional.of(result));
        rightPair.setAncestor(Optional.of(result));
        return result;
    }

    private static int findCommaIndex(String expression) {
        int balance = 0;
        for (int i = 0; i < expression.length(); i++) {
            char character = expression.charAt(i);
            balance += (character == '[') ? 1 : (character == ']') ? - 1 : 0;
            if (balance == 1 && character == ',') return i;
        }
        throw new IllegalArgumentException("Invalid expression");
    }

    private static int findClosingIndex(String expression) {
        int balance = 0;
        for (int i = 0; i < expression.length(); i++) {
            char character = expression.charAt(i);
            balance += (character == '[') ? 1 : (character == ']') ? - 1 : 0;
            if (balance == 0) return i;
        }
        throw new IllegalArgumentException("Invalid expression");
    }
}