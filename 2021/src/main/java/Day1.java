import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day1 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input1.csv");
        List<Integer> depths = lines.stream().map(Integer::parseInt).collect(Collectors.toList());

        int numberOfIncreases = getNumberOfIncrements(depths);
        System.out.println("number of increases:  " + numberOfIncreases);

        printNumberOfSlidingIncrements(depths);
    }

    private static int getNumberOfIncrements(List<Integer> depths) {
        int numberOfIncreases = 0;
        for (int i = 0; i < depths.size(); i++) {
            if (i > 0 && depths.get(i) > depths.get(i-1)) {
                numberOfIncreases += 1;
            }
        }
        return numberOfIncreases;
    }

    private static void printNumberOfSlidingIncrements(List<Integer> depths) {
        List<Integer> result = new ArrayList<>();
        IntStream.range(0, depths.size() - 2).forEach(i -> result.add(depths.get(i) + depths.get(i + 1) + depths.get(i + 2)));

        int numberOfIncreases = getNumberOfIncrements(result);
        System.out.println("number of increases:  " + numberOfIncreases);
    }
}
