package day1;

import fileUtils.FileReader;
import java.util.*;

public class Day1 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        lines.forEach(line -> {
            var numbers = line.split("   ");
            list1.add(Integer.parseInt(numbers[0]));
            list2.add(Integer.parseInt(numbers[1]));
        });
        findTotalDistance(list1, list2);
        findSimilarityScore(list1, list2);
    }

    private static void findTotalDistance(List<Integer> list1, List<Integer> list2) {
        int totalDistance  = 0;
        list1.sort(Comparator.comparingInt(i -> i));
        list2.sort(Comparator.comparingInt(i -> i));
        for (int i = 0; i < list1.size(); i++) {
            totalDistance += Math.abs(list1.get(i) - list2.get(i));
        }
        System.out.println("Total distance: " + totalDistance);
    }

    private static void findSimilarityScore(List<Integer> list1, List<Integer> list2) {
        var totalScore = list1.stream()
                .map(i -> i * list2.stream().filter(j -> j.equals(i)).count()
        ).reduce(Long::sum).orElseThrow();
        System.out.println("Total score: " + totalScore);
    }
}
