package day15;

import algorithms.ChineseRemainder;
import fileUtils.FileReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day15 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input15.csv");
        var discMap = readDiscs(lists);

        //part a
        findButtonPressTime(discMap);

        //part b
        discMap.put(discMap.size() + 1, new Disc(11, 0));
        findButtonPressTime(discMap);
    }

    private static Map<Integer, Disc> readDiscs(List<String> lists) {
        Map<Integer, Disc> discMap = new HashMap<>();
        lists.forEach(list -> {
            var idAndRest = list.split("Disc #")[1].split(" has ");
            int id = Integer.parseInt(idAndRest[0]);
            var numberOfPositionsAndRest = idAndRest[1].split(" positions; at time=0, it is at position ");
            int numberOfPositions = Integer.parseInt(numberOfPositionsAndRest[0]);
            int position = Integer.parseInt(numberOfPositionsAndRest[1].split("\\.")[0]);
            discMap.put(id, new Disc(numberOfPositions, position));
        });
        return discMap;
    }

    private static void findButtonPressTime(Map<Integer, Disc> discMap) {
        List<Integer> numberOfPositions = discMap.values().stream().map(Disc::numberOfPositions).toList();
        List<Integer> positions = discMap.values().stream().map(Disc::position).toList();

        List<Integer> remainders = IntStream.range(0, discMap.size())
                .mapToObj(i -> numberOfPositions.get(i) - positions.get(i) - (i + 1)).toList();
        var result = ChineseRemainder.execute(numberOfPositions, remainders);

        System.out.println("result: " + result);
    }
}