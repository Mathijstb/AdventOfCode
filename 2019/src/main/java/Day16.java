import com.google.common.collect.Lists;
import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input16.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<Integer> input = Arrays.stream(line.split("")).map(Integer::parseInt).collect(Collectors.toList());

        //executePhases(input, 100);
        calculatePart2(line, input);
    }

    private static void calculatePart2(String line, List<Integer> inputList) {
        int inputSize = inputList.size();
        int offSetIndex = Integer.parseInt(line.substring(0,7));
        int[] input = new int[inputSize * 10000 - offSetIndex];


        for (int i = input.length - 1; i >= 0; i--) {
            int index = Math.floorMod(offSetIndex + i, inputSize);
            input[i] = inputList.get(index);
        }

        for (int i = 0; i < 100; i++) {
            int cumValue = 0;
            int[] output = new int[input.length];
            for (int j = input.length - 1; j >= 0; j--) {
                output[j] = Math.floorMod(Math.abs(input[j] + cumValue), 10);
                cumValue += input[j];
            }
            input = output;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(input[i]);
        }
        System.out.println("Output: " + sb.toString());
        //25274708 too low
    }


    private static final List<Integer> basePattern = Lists.newArrayList(0, 1, 0, -1);

    private static void executePhases(List<Integer> input, int numberOfPhases) {
        List<Integer> output = input;
        for (int i = 0; i < numberOfPhases; i++) {
            input = output;
            output = determineOutput(input);
        }
        printOutput(output, "Output: ");
        printOutput(output.subList(0, 8), "First 8 digits: ");
    }


    private static List<Integer> determineOutput(List<Integer> input) {
        List<Integer> output = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            List<Integer> pattern = getPattern(i, input.size());
            int multiplication = IntStream.range(0, pattern.size())
                    .map(n -> pattern.get(n) * input.get(n))
                    .reduce(0, Integer::sum);
            output.add(Math.floorMod(Math.abs(multiplication), 10));
        }
        return output;
    }

    private static void printOutput(List<Integer> output, String message) {
        StringBuilder sb = new StringBuilder();
        for (Integer integer : output) {
            sb.append(integer);
        }
        System.out.println(message + sb.toString());
    }

    private static List<Integer> getPattern(int index, int size) {
        List<Integer> pattern = new ArrayList<>();
        int offSet = 1;
        int baseNumberIndex = 0;
        do {
            int baseNumber = basePattern.get(baseNumberIndex % basePattern.size());
            baseNumberIndex ++;
            for (int j = 0; j < index + 1; j++) {
                pattern.add(baseNumber);
                if (pattern.size() == size + offSet) break;
            }
        } while (pattern.size() != size + offSet);
        return pattern.subList(offSet, size + offSet);
    }

}
