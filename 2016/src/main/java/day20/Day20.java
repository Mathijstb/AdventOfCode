package day20;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class Day20 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input20.csv");
        var ranges = readRanges(lists);

        //part a
        var lowestIpAllowed = determineLowestIpAllowed(ranges);
        System.out.println("Lowest allowed ip: " + lowestIpAllowed);

        //part b
        var mergedRanges = getMergedRanges(ranges);
        determineNumberOfIpsAllowed(mergedRanges);
    }

    private static List<Range> readRanges(List<String> lists) {
        return lists.stream().map(list -> {
            var ranges = list.split("-");
            return new Range(Long.parseLong(ranges[0]), Long.parseLong(ranges[1]));
        }).toList();
    }

    private static long determineLowestIpAllowed(List<Range> ranges) {
        var sortedRanges = new ArrayList<>(ranges);
        sortedRanges.sort(Comparator.comparing(range -> range.min));
        long lowestRangeMin = sortedRanges.get(0).min;
        if (lowestRangeMin > 0) {
            return 0;
        }
        long lowestRangeMax = sortedRanges.get(0).max;
        for (int i = 1; i < sortedRanges.size(); i++) {
            var range = sortedRanges.get(i);
            if (range.min <= lowestRangeMax + 1) {
                lowestRangeMax = range.max;
            }
            else {
                break;
            }
        }
        return lowestRangeMax + 1;
    }

    private static List<Range> getMergedRanges(List<Range> ranges) {
        var sortedRanges = new ArrayList<>(ranges);
        sortedRanges.sort(Comparator.comparing(range -> range.min));

        List<Range> result = new ArrayList<>();
        int startIndex = 0;
        while (startIndex < sortedRanges.size()) {
            var currentRange = new Range(sortedRanges.get(startIndex).min, sortedRanges.get(startIndex).max);
            startIndex += 1;
            for (int i = startIndex; i < sortedRanges.size(); i++) {
                var range = sortedRanges.get(i);
                if (range.min <= currentRange.max + 1) {
                    currentRange.max = Math.max(currentRange.max, range.max);
                    startIndex += 1;
                }
                else {
                    break;
                }
            }
            result.add(currentRange);
        }
        return result;
    }

    private static void determineNumberOfIpsAllowed(List<Range> mergedRanges) {
        mergedRanges.add(0, new Range(-1, -1));
        mergedRanges.add(new Range(4294967296L, 0));
        long numberAllowed = IntStream.range(0, mergedRanges.size() - 1).mapToLong(i -> {
            long rangeMax = mergedRanges.get(i).max;
            long nextRangeMin = mergedRanges.get(i + 1).min;
            return nextRangeMin - rangeMax - 1;
        }).reduce(0L, Long::sum);
        System.out.println("Number of Ips allowed: " + numberAllowed);
    }
}