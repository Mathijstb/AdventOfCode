package day6;

import fileUtils.FileReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day6 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input6.csv");
        findMaxMessage(lists);
        findMinMessage(lists);
    }

    private static void findMaxMessage(List<String> lists) {
        var size = lists.get(0).length();
        var message = IntStream.range(0, size)
                .mapToObj(i -> {
                    var groups = lists.stream()
                            .map(list -> list.charAt(i))
                            .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
                    return String.valueOf(groups.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey());
                }).collect(Collectors.joining());
        System.out.println("Max message: " + message);
    }

    private static void findMinMessage(List<String> lists) {
        var size = lists.get(0).length();
        var message = IntStream.range(0, size)
                .mapToObj(i -> {
                    var groups = lists.stream()
                            .map(list -> list.charAt(i))
                            .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
                    return String.valueOf(groups.entrySet().stream().min(Map.Entry.comparingByValue()).orElseThrow().getKey());
                }).collect(Collectors.joining());
        System.out.println("Min message: " + message);
    }

}