package day11;

import com.google.common.collect.Lists;
import fileUtils.FileReader;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day11 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input11.csv");
        var lists = Lists.partition(lines, 7).stream().map(list -> {
            List<String> newList = new ArrayList<>(list);
            newList.removeIf(String::isEmpty);
            return newList;
        }).toList();
        var monkeys = lists.stream().map(Day11::readMonkey).toList();
        var productOfDivisors = monkeys.stream().map(Monkey::divisor).reduce(1L, (a,b) -> a * b);

        //part a
        System.out.println("Part a");
        var monkeysCopy = monkeys.stream().map(Monkey::copy).toList();
        executeRounds(monkeysCopy, x -> x / 3, 20);

        //part b
        System.out.println();
        System.out.println("Part b");
        monkeysCopy= monkeys.stream().map(Monkey::copy).toList();
        executeRounds(monkeysCopy, x -> x % productOfDivisors , 10000);
    }

    private static Monkey readMonkey(List<String> list) {
        int number = Integer.parseInt(list.get(0).split(" ")[1].split(":")[0]);
        var startingItems = new LinkedList<>(Arrays.stream(list.get(1).split("Starting items: ")[1].split(", ")).map(Long::parseLong).toList());
        var operation = readOperation(list.get(2));
        var check = readCheck(list.get(3));
        var divisor = Long.parseLong(list.get(3).split("Test: divisible by ")[1]);
        var throwToIfTrue = Integer.parseInt(list.get(4).split("If true: throw to monkey ")[1]);
        var throwToIfFalse = Integer.parseInt(list.get(5).split("If false: throw to monkey ")[1]);
        return new Monkey(number, startingItems, operation, check, divisor, throwToIfTrue, throwToIfFalse);
    }

    private static Function<Long, Long> readOperation(String line){
        var parameters = line.split("Operation: new = ")[1].split(" ");
        var operation = parameters[1];
        var operand2 = parameters[2];
        if (operation.equals("*")) {
            return operand2.equals("old") ? x -> x * x : x -> x * Long.parseLong(operand2);
        }
        else if (operation.equals("+")) {
            return x -> x + Long.parseLong(operand2);
        }
        else {
            throw new IllegalArgumentException("Can not parse operation");
        }
    }

    private static Predicate<Long> readCheck(String line){
        int amount = Integer.parseInt(line.split("Test: divisible by ")[1]);
        return x -> Math.floorMod(x, amount) == 0;
    }

    private static void executeRounds(List<Monkey> monkeys, Function<Long, Long> manageFunction, int numberOfRounds) {
        List<Long> monkeyBusiness = LongStream.range(0, monkeys.size()).mapToObj(x -> 0L).collect(Collectors.toList());
        for (int i = 0; i < numberOfRounds; i++) {
            monkeys.forEach(monkey -> play(monkey, monkeys, monkeyBusiness, manageFunction));
        }
        System.out.println("Monkey business:" + monkeyBusiness);
        var max2 = monkeyBusiness.stream().sorted(Comparator.reverseOrder()).limit(2).toList();
        System.out.println("Monkey business level: " + max2.get(0) * max2.get(1));
    }

    private static void play(Monkey monkey, List<Monkey> monkeys, List<Long> monkeyBusiness, Function<Long, Long> manageFunction) {
        //System.out.println("Monkey 0:");
        while (!monkey.items().isEmpty()) {
            monkeyBusiness.set(monkey.number(), monkeyBusiness.get(monkey.number()) + 1);
            var item = monkey.items().removeFirst();
            //System.out.printf("  Monkey inspects an item with a worry level of %s.%n", item);
            var newItem = manageFunction.apply(monkey.operation().apply(item));
            //System.out.println("    New item value: " + newItem);
            Monkey monkeyToThrowTo = monkeys.get(monkey.getMonkeyNumberToThrowTo(newItem));
            //System.out.println("    Throw to monkey: " + monkeyToThrowTo.number());
            monkeyToThrowTo.items().add(newItem);
        }
    }
}
