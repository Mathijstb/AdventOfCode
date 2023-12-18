package day12;

import com.google.common.primitives.Chars;
import fileUtils.FileReader;

import java.util.*;

public class Day12 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input12.csv");

        //part 1
        var springRows = parseSpringRows(lines);
        var numberOfCombinations = findNumberOfCombinations(springRows);
        System.out.println("Number of combinations: " + numberOfCombinations);

        //part2
        var springRows2 = parseSpringRows2(lines);
        var numberOfCombinations2 = findNumberOfCombinations(springRows2);
        System.out.println("Number of combinations 2: " + numberOfCombinations2);
    }

    private static List<SpringRow> parseSpringRows(List<String> lines) {
        return lines.stream().map(line -> {
            var springRowAndRecord = line.split(" ");
            var conditions = Chars.asList(springRowAndRecord[0].toCharArray()).stream().map(Condition::of).toList();
            var record = Arrays.stream(springRowAndRecord[1].split(",")).map(Integer::parseInt).toList();
            return new SpringRow(conditions, record);
        }).toList();
    }

    private static List<SpringRow> parseSpringRows2(List<String> lines) {
        return lines.stream().map(line -> {
            var springRowAndRecord = line.split(" ");
            var conditions = Chars.asList(springRowAndRecord[0].toCharArray()).stream().map(Condition::of).toList();
            var unfoldedConditions = new ArrayList<>(conditions);
            for (int i = 0; i < 4; i++) {
                unfoldedConditions.add(Condition.UNKNOWN);
                unfoldedConditions.addAll(conditions);
            }
            var record = Arrays.stream(springRowAndRecord[1].split(",")).map(Integer::parseInt).toList();
            var unfoldedRecord = new ArrayList<>(record);
            for (int i = 0; i < 4; i++) {
                unfoldedRecord.addAll(record);
            }
            return new SpringRow(unfoldedConditions, unfoldedRecord);
        }).toList();
    }

    private static long findNumberOfCombinations(List<SpringRow> springRows) {
        return springRows.stream().map(Day12::findNumberOfCombinations).reduce(0L, Long::sum);
    }

    private static long findNumberOfCombinations(SpringRow springRow) {
        var conditions = springRow.conditions();
        var record = springRow.record();
        Map<String, Long> combinationMap = new HashMap<>();
        var result = findNumberOfCombinations(combinationMap, conditions, record);
        System.out.println("Conditions: " + conditions.stream().map(Condition::toString).reduce("", (a,b) -> a + b)
                + " Record: " + record + " Combinations: " + result);
        System.out.println();
        return result;
    }

    private static long findNumberOfCombinations(Map<String, Long> combinationMap, List<Condition> conditions, List<Integer> record) {
        long result;
        var conditionsRecordHash = hash(conditions, record);
        if (combinationMap.containsKey(conditionsRecordHash)) {
            return combinationMap.get(conditionsRecordHash);
        }
        if (record.isEmpty()) {
            result = conditions.stream().anyMatch(c -> c.equals(Condition.DAMAGED)) ? 0 : 1;
        }
        else {
            var nextIndexOpt = findNextIndex(conditions, record);
            if (nextIndexOpt.isEmpty()) return 0;
            var nextIndex = nextIndexOpt.get();
            var nextSize = record.get(0);
            if (nextIndex + nextSize == conditions.size()) {
                result =  1;
            }
            else if (conditions.get(nextIndex).equals(Condition.DAMAGED)) {
                result =  findNumberOfCombinations(combinationMap, conditions.subList(nextIndex + nextSize + 1, conditions.size()), record.subList(1, record.size()));
            }
            else {
                result =  findNumberOfCombinations(combinationMap, conditions.subList(nextIndex + nextSize + 1, conditions.size()), record.subList(1, record.size()))
                        + findNumberOfCombinations(combinationMap, conditions.subList(nextIndex + 1, conditions.size()), record);
            }
        }
        combinationMap.put(conditionsRecordHash, result);
        return result;
    }

    private static String hash(List<Condition> conditions, List<Integer> record) {
        return conditions.stream().map(Condition::toString).reduce("", (a, b) -> (a+b)) +
                record.stream().map(String::valueOf).reduce("", (a, b) -> (a+b));
    }

    private static Optional<Integer> findNextIndex(List<Condition> conditions, List<Integer> record) {
        var damagedRequired = record.stream().reduce(0, Integer::sum);
        var operationalRequired = record.size() - 1;
        var operationalLeft = conditions.stream().filter(c -> c.equals(Condition.OPERATIONAL)).count();
        var damagedLeft = conditions.stream().filter(c -> c.equals(Condition.DAMAGED)).count();
        var damagedOrUnknownLeft = conditions.size() - operationalLeft;
        var operationalOrUnknownLeft = conditions.size() - damagedLeft;
        if (damagedRequired > damagedOrUnknownLeft) return Optional.empty();
        if (operationalRequired > operationalOrUnknownLeft) return Optional.empty();
        var nextSize = record.get(0);
        if (nextSize.equals(conditions.size())) {
            return conditions.subList(0, nextSize).stream().noneMatch(c -> c.equals(Condition.OPERATIONAL)) ? Optional.of(0) : Optional.empty();
        }
        switch (conditions.get(0)) {
            case OPERATIONAL -> {
                return findNextIndex(conditions.subList(1, conditions.size()), record).map(i -> i + 1);
            }
            case UNKNOWN -> {
                if (conditions.subList(0, nextSize).stream().noneMatch(c -> c.equals(Condition.OPERATIONAL)) &&
                        !conditions.get(nextSize).equals(Condition.DAMAGED))
                {
                    return Optional.of(0);
                }
                else {
                    return findNextIndex(conditions.subList(1, conditions.size()), record).map(i -> i + 1);
                }
            }
            case DAMAGED -> {
                if (conditions.subList(0, nextSize).stream().noneMatch(c -> c.equals(Condition.OPERATIONAL)) &&
                        !conditions.get(nextSize).equals(Condition.DAMAGED))
                {
                    return Optional.of(0);
                }
                else {
                    return Optional.empty();
                }
            }
            default -> throw new IllegalStateException();
        }
    }


}
