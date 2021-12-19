package day18;

import fileUtils.FileReader;

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
        return pair;
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