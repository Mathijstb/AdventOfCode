package day1;

import fileUtils.FileReader;

import java.util.*;

public class Day1 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");

        List<List<Integer>> caloriesList = new ArrayList<>();

        List<Integer> calories = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                caloriesList.add(calories);
                calories = new ArrayList<>();
            } else {
                calories.add(Integer.parseInt(line));
            }
        }
        determineMax(caloriesList);
        determineMaxThree(caloriesList);
    }

    private static void determineMax(List<List<Integer>> caloriesList) {
        var max = caloriesList.stream()
                .map(calories -> calories.stream().reduce(0, Integer::sum))
                .max(Comparator.comparingInt(x -> x)).orElseThrow();
        System.out.println("Max: " + max);
    }

    private static void determineMaxThree(List<List<Integer>> caloriesList) {
        var sums = caloriesList.stream()
                .map(calories -> calories.stream().reduce(0, Integer::sum)).toList();
        var maxThreeList = sums.stream()
                .sorted(Comparator.reverseOrder())
                .limit(3).toList();

        System.out.println("MaxThree: " + maxThreeList);
        System.out.println("Sum of max three: " + maxThreeList.stream().reduce(0, Integer::sum));
    }
}
