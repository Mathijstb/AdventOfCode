import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Day10 {

    @Data
    private static class Adapter {
        long NumberOfPaths = 0;
        List<Adapter> nextAdapters;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input10.csv");
        List<Integer> integers = lines.stream().map(Integer::parseInt).collect(Collectors.toList());
        integers.add(0);
        integers.add(Collections.max(integers) + 3);
        Collections.sort(integers);
        //countDifferences(integers);
        List<Adapter> adapters = getConnections(integers);

        System.out.println("number: " + adapters.get(0).NumberOfPaths);
    }

    private static List<Adapter> getConnections(List<Integer> integers) {
        List<Adapter> adapters = integers.stream().map(integer -> new Adapter()).collect(Collectors.toList());
        for (int i = 0; i < integers.size(); i++) {
            Adapter adapter = adapters.get(i);
            List<Integer> nextIndices = getNextIndices(integers, i);
            adapter.setNextAdapters(nextIndices.stream().map(adapters::get).collect(Collectors.toList()));
        }
        adapters.get(adapters.size() -1).setNumberOfPaths(1);
        for (int i = adapters.size() -2; i >= 0; i--) {
            Adapter adapter = adapters.get(i);
            adapter.setNumberOfPaths(adapter.nextAdapters.stream().map(Adapter::getNumberOfPaths).reduce(0L, Long::sum));
        }
        return adapters;
    }

    private static List<Integer> getNextIndices(List<Integer> integers, int index) {
        int currentNumber = integers.get(index);
        List<Integer> nextIndices = new ArrayList<>();
        for (int i = index + 1; i < integers.size(); i++) {
            if (integers.get(i) <= currentNumber + 3) {
                nextIndices.add(i);
            }
        }
        return nextIndices;
    }

    private static void countDifferences(List<Integer> integers) {
        System.out.println(integers);
        int numberOf1Stepdifference = 0;
        int numberOf3Stepdifference = 0;
        for (int i = 1; i < integers.size(); i++) {
            int first = integers.get(i - 1);
            int second = integers.get(i);
            int diff = second - first;
            if (diff == 1) {
                numberOf1Stepdifference += 1;
            }
            else if (diff == 3) {
                numberOf3Stepdifference += 1;
            }
        }
        System.out.println("number of 1 step diffs: " + numberOf1Stepdifference);
        System.out.println("number of 3 step diffs: " + numberOf3Stepdifference);
        System.out.println("multiplication: " + numberOf1Stepdifference * numberOf3Stepdifference);
    }

}
