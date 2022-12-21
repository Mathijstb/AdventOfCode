package day21;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 {

    private static final HashMap<String, Monkey> monkeyMap = new HashMap<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input21.csv");
        var monkeys = readMonkeys(lines);


        //part a
        var monkeysCopy = monkeys.stream().map(Monkey::copy).toList();
        var rootMonkey = findRootValue(monkeysCopy);
        System.out.println("Root value: " + rootMonkey.value.orElseThrow());

        // part b
        for (long humnValue = 3006_709_232_000L; humnValue <= 3006_709_233_000L; humnValue++) {
            monkeysCopy = monkeys.stream().map(Monkey::copy).toList();
            if (findRootEquationResult(monkeysCopy, humnValue)) {
                System.out.println();
                System.out.println("Result found! Humn value: " + humnValue);
                break;
            }
        }

    }

    private static List<Monkey> readMonkeys(List<String> lines) {
        return lines.stream().map(line -> {
            var nameAndRest = line.split(": ");
            var name = nameAndRest[0];
            var rest = nameAndRest[1];
            Expression expression;
            if (rest.contains("*")) {
                var names = rest.split(" \\* ");
                expression = new Expression(names[0], names[1], (a, b) -> a * b);
            } else if (rest.contains("/")) {
                var names = rest.split(" / ");
                expression = new Expression(names[0], names[1], (a, b) -> a / b);
            } else if (rest.contains("+")) {
                var names = rest.split(" \\+ ");
                expression = new Expression(names[0], names[1], Long::sum);
            } else if (rest.contains("-")) {
                var names = rest.split(" - ");
                expression = new Expression(names[0], names[1], (a, b) -> a - b);
            } else {
                return new Monkey(name, Optional.of(Long.parseLong(rest)), Optional.empty());
            }
            return new Monkey(name, Optional.empty(), Optional.of(expression));
        }).toList();
    }

    private static Monkey findRootValue(List<Monkey> monkeys) {
        monkeyMap.clear();
        monkeys.forEach(m -> monkeyMap.put(m.name, m));
        var rootMonkey = monkeyMap.get("root");
        Set<Monkey> remaining = monkeyMap.values().stream().filter(monkey -> monkey.expression.isPresent()).collect(Collectors.toSet());
        var improved = true;
        while (improved && !remaining.isEmpty()) {
            improved = false;
            for (Monkey monkey : remaining) {
                var expression = monkey.expression.orElseThrow();
                var monkey1 = monkeyMap.get(expression.monkey1());
                var monkey2 = monkeyMap.get(expression.monkey2());
                if (monkey1.value.isPresent() && monkey2.value.isPresent()) {
                    var value = expression.function().apply(monkey1.value.get(), monkey2.value.get());
                    monkey.value = Optional.of(value);
                    remaining.remove(monkey);
                    improved = true;
                    break;
                }
            }
        }
        return rootMonkey;
    }

    private static boolean findRootEquationResult(List<Monkey> monkeys, long humnValue) {
        monkeyMap.clear();
        monkeys.forEach(m -> monkeyMap.put(m.name, m));
        adjustMonkeys(monkeys, humnValue);
        var rootMonkey = findRootValue(monkeys);
        var rootExpression = rootMonkey.expression.orElseThrow();
        var rootValue = rootMonkey.value.orElseThrow();
        var monkey1Value = monkeyMap.get(rootExpression.monkey1()).value.orElseThrow();
        var monkey2Value = monkeyMap.get(rootExpression.monkey2()).value.orElseThrow();
        if (rootValue == 0) {
            //System.out.printf("Humn value %s: %s does not equal %s%n", humnValue, monkey1Value, monkey2Value);
            return false;
        }
        else {
            System.out.printf("Humn value %s: %s does equal %s!%n", humnValue, monkey1Value, monkey2Value);
            return true;
        }
    }

    private static void adjustMonkeys(List<Monkey> monkeys, long humnValue) {
        var humnMonkey = monkeyMap.get("humn");
        var rootMonkey = monkeyMap.get("root");
        humnMonkey.value = Optional.of(humnValue);
        var rootExpression = rootMonkey.expression.orElseThrow();
        rootMonkey.expression = Optional.of(new Expression(rootExpression.monkey1(), rootExpression.monkey2(), (a, b) -> a.equals(b) ? 1L : 0L));
    }
}