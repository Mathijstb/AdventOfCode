package day5;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.IntStream;

public class Day5 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input5.csv");
        int emptyLineIndex = IntStream.range(0, lines.size()).filter(i -> lines.get(i).isEmpty()).findFirst().orElseThrow();
        var stacks = readStacks(lines.subList(0, emptyLineIndex));
        var instructions = readInstructions(lines.subList(emptyLineIndex + 1, lines.size()));

        executeInstructions(copyStacks(stacks), instructions);
        executeInstructions2(copyStacks(stacks), instructions);
    }

    private static List<Stack<Character>> copyStacks(List<Stack<Character>> stacks) {
        List<Stack<Character>> result = new ArrayList<>();
        stacks.forEach(stack -> {
            var newStack = new Stack<Character>();
            newStack.addAll(stack);
            result.add(newStack);
        });
        return result;
    }

    private static void executeInstructions(List<Stack<Character>> stacks, List<Instruction> instructions) {
        instructions.forEach(instruction -> {
            var fromStack = stacks.get(instruction.from() - 1);
            var toStack = stacks.get(instruction.to() - 1);
            for (int i = 0; i < instruction.amount(); i++) {
                toStack.push(fromStack.pop());
            }
        });
        printResult(stacks);
    }

    private static void executeInstructions2(List<Stack<Character>> stacks, List<Instruction> instructions) {
        instructions.forEach(instruction -> {
            var fromStack = stacks.get(instruction.from() - 1);
            var toStack = stacks.get(instruction.to() - 1);
            var intermediateStack = new Stack<Character>();
            for (int i = 0; i < instruction.amount(); i++) {
                intermediateStack.push(fromStack.pop());
            }
            while (!intermediateStack.isEmpty()) {
                toStack.push(intermediateStack.pop());
            }
        });
        printResult(stacks);
    }

    private static void printResult(List<Stack<Character>> stacks) {
        var result = stacks.stream().map(Stack::peek).toList();
        var resultString = result.stream().map(Object::toString).reduce((acc, e) -> acc  + e).orElseThrow();
        System.out.println("Result: " + resultString);
    }

    private static List<Stack<Character>> readStacks(List<String> lines) {
        int numberOfStacks = lines.get(lines.size() - 1).split("\\s+").length - 1;
        var newLines = lines.subList(0, lines.size() - 1);
        List<Stack<Character>> stacks = new ArrayList<>();
        for (int stackIndex = 0; stackIndex < numberOfStacks; stackIndex++) {
            stacks.add(readStack(newLines, stackIndex));
        }
        return stacks;
    }

    private static Stack<Character> readStack(List<String> lines, int stackIndex) {
        var stack = new Stack<Character>();
        for (int i = lines.size() - 1; i >= 0; i--) {
            var line = lines.get(i);
            if (stackIndex * 4 + 2 > line.length() - 1) break;
            var character = line.substring(stackIndex * 4 + 1, stackIndex * 4 + 2);
            if (character.isBlank()) {
                break;
            }
            else {
                stack.push(character.charAt(0));
            }
        }
        return stack;
    }

    private static List<Instruction> readInstructions(List<String> lines) {
        return lines.stream().map(line -> {
            var amountAndMove = line.split(" from ");
            var move = amountAndMove[1].split(" to ");
            int amount = Integer.parseInt(amountAndMove[0].split(" ")[1]);
            return new Instruction(amount, Integer.parseInt(move[0]), Integer.parseInt(move[1]));
        }).toList();
    }

}
