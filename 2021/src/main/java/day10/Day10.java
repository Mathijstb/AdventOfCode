package day10;

import fileUtils.FileReader;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

public class Day10 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input10.csv");

        printCorruptSyntaxScore(input);
    }

    private static void printCorruptSyntaxScore(List<String> input) {
        List<List<Character>> characterLines = input.stream().map(line -> line.chars().mapToObj(s -> (char) s).collect(Collectors.toList())).collect(Collectors.toList());

        List<List<Character>> corruptLines = characterLines.stream().filter(line -> getCorruptCharacter(line).isPresent()).collect(Collectors.toList());
        printCorruptionScore(corruptLines);

        List<List<Character>> incompleteLines = new ArrayList<>(characterLines);
        incompleteLines.removeAll(corruptLines);
        printMiddleCompletionScore(incompleteLines);
    }

    private static final Set<Character> OPEN_CHARS = Set.of('(', '[', '{', '<');
    private static final Set<Character> CLOSE_CHARS = Set.of(')', ']', '}', '>');

    private static Optional<Character> getCorruptCharacter(List<Character> characterLine) {
        Stack<Character> characterStack = new Stack<>();
        for (char character : characterLine) {
            if (OPEN_CHARS.contains(character)) {
                characterStack.push(character);
            } else if (CLOSE_CHARS.contains(character)) {
                if (characterStack.isEmpty()) return Optional.empty();
                if (!characterStack.peek().equals(getReverse(character))) return Optional.of(character);
                characterStack.pop();
            }
            else {
                throw new IllegalArgumentException("Invalid character");
            }
        }
        return Optional.empty();
    }

    private static char getReverse(char character) {
        switch (character) {
            case ')': return '(';
            case ']': return '[';
            case '}': return '{';
            case '>': return '<';
            case '(': return ')';
            case '[': return ']';
            case '{': return '}';
            case '<': return '>';
            default: throw new NotImplementedException("No reverse defined");
        }
    }

    private static void printCorruptionScore(List<List<Character>> corruptLines) {
        List<Character> corruptCharacters = corruptLines.stream().map(line -> getCorruptCharacter(line).orElseThrow()).collect(Collectors.toList());
        int totalCorruptionScore = corruptCharacters.stream().map(character -> {
            switch (character) {
                case ')': return 3;
                case ']': return 57;
                case '}': return 1197;
                case '>': return 25137;
                default: throw new NotImplementedException("No score defined");
            }
        }).mapToInt(v -> v).sum();
        System.out.println("Total corruption score: " + totalCorruptionScore);
    }

    private static void printMiddleCompletionScore(List<List<Character>> incompleteLines) {
        List<Long> completionScores = incompleteLines.stream().map(line ->
                getLineCompletion(line).stream().mapToLong(character -> {
                    switch (character) {
                        case ')': return 1;
                        case ']': return 2;
                        case '}': return 3;
                        case '>': return 4;
                        default: throw new NotImplementedException("No score defined");
                    }
                }).reduce(0, (long1, long2) -> (long1 * 5 + long2)))
                .sorted(Long::compareTo)
                .collect(Collectors.toList());
        long middleScore = completionScores.get(completionScores.size() / 2);
        System.out.println("Middle score: " + middleScore);
    }

    private static List<Character> getLineCompletion(List<Character> line) {
        Stack<Character> characterStack = new Stack<>();
        line.forEach(character -> {
            if (OPEN_CHARS.contains(character)) {
                characterStack.push(character);
            }
            else if (CLOSE_CHARS.contains(character)) {
                characterStack.pop();
            }
        });
        List<Character> lineCompletion = new ArrayList<>();
        while (!characterStack.isEmpty()) {
            Character character = characterStack.pop();
            lineCompletion.add(getReverse(character));
        }
        return lineCompletion;
    }
}