package day7;

import fileUtils.FileReader;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day7 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input7.csv");
        assert (input.size() == 1);
        List<Integer> positions = Arrays.stream(input.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());

        //Print best alignment when using linear cost function
        IntFunction<Long> linearCostFunction = m -> (long) m;
        printBestAlignment(positions, linearCostFunction);

        //Print best alignment when using increasing cost function
        IntFunction<Long> increasingCostFunction = m -> ((long) m * (m + 1)) / 2;
        printBestAlignment(positions, increasingCostFunction);
    }

    private static void printBestAlignment(List<Integer> positions, IntFunction<Long> costFunction) {
        int minX = positions.stream().mapToInt(v -> v).min().orElseThrow();
        int maxX = positions.stream().mapToInt(v -> v).max().orElseThrow();

        long minFuel = IntStream.range(minX, maxX + 1)
                .mapToLong(alignmentPosition -> positions.stream().map(p -> costFunction.apply(Math.abs(p - alignmentPosition))).mapToLong(v -> v).sum())
                .min().orElseThrow();

        System.out.println("Min fuel: " + minFuel);
    }

}
