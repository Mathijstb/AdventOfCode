package day3;

import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;
import fileUtils.FileReader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3 {

    public static void execute() {
        List<RuckSack> ruckSacks = new FileReader().readFile("input3.csv").stream()
                .map(line -> new RuckSack(line,
                                          line.substring(0, line.length() / 2),
                                          line.substring(line.length() / 2)))
                .toList();

        //Part 1
        determineSumOfPriorities(ruckSacks);

        //Part 2
        determineSumOfGroupedPriorities(ruckSacks);
    }

    private static void determineSumOfGroupedPriorities(List<RuckSack> ruckSacks) {
        var groupedRuckSacks = Lists.partition(ruckSacks, 3);
        var sumOfPriorities = groupedRuckSacks.stream()
                .map(Day3::findCommonItem)
                .map(Day3::getPriority)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("Sum of grouped priorities: " + sumOfPriorities);
    }

    private static int getPriority(Character item) {
        return (item <= 'Z') ? (item - 'A') + 27 : item - 'a' + 1;
    }

    private static Character findCommonItem(RuckSack ruckSack) {
        Set<Character> leftCharacters = new HashSet<>(Chars.asList(ruckSack.leftContainer().toCharArray()));
        Set<Character> rightCharacters = new HashSet<>(Chars.asList(ruckSack.rightContainer().toCharArray()));
        leftCharacters.retainAll(rightCharacters);
        return leftCharacters.stream().findFirst().orElseThrow();
    }

    private static Character findCommonItem(List<RuckSack> ruckSacks) {
        var ruckSack = ruckSacks.get(0);
        Set<Character> commonItems = new HashSet<>(Chars.asList(ruckSack.allItems().toCharArray()));
        for (int i = 1; i < ruckSacks.size(); i++) {
            commonItems.retainAll(new HashSet<>(Chars.asList(ruckSacks.get(i).allItems().toCharArray())));
        }
        return commonItems.stream().findFirst().orElseThrow();
    }

    private static void determineSumOfPriorities(List<RuckSack> ruckSacks) {
        var commonItems = ruckSacks.stream().map(Day3::findCommonItem).toList();
        var sumOfPriorities = commonItems.stream()
                .map(Day3::getPriority)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("Sum of priorities: " + sumOfPriorities);
    }
}
