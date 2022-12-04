package day4;

import fileUtils.FileReader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Day4 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input4.csv");
        var rangePairs = lines.stream().map(Day4::readRangePair).toList();
        determineNumberOfCoverings(rangePairs);
        determineNumberOfOverlaps(rangePairs);
    }

    private static Pair<Range, Range> readRangePair(String line) {
        var ranges = line.split(",");
        var range1 = ranges[0].split("-");
        var range2 = ranges[1].split("-");
        return Pair.of(new Range(Integer.parseInt(range1[0]), Integer.parseInt(range1[1])),
                       new Range(Integer.parseInt(range2[0]), Integer.parseInt(range2[1])));
    }

    private static void determineNumberOfCoverings(List<Pair<Range, Range>> rangePairs) {
        var coveringRangePairs = rangePairs.stream().filter(rangePair -> {
            var range1 = rangePair.getLeft();
            var range2 = rangePair.getRight();
            return (range1.min() <= range2.min() && range1.max() >= range2.max()) ||
                    (range2.min() <= range1.min() && range2.max() >= range1.max());
        }).toList();
        System.out.println("Number of coverings: " + coveringRangePairs.size());
    }

    private static void determineNumberOfOverlaps(List<Pair<Range, Range>> rangePairs) {
        var overlappingPairs = rangePairs.stream().filter(rangePair -> {
            var range1 = rangePair.getLeft();
            var range2 = rangePair.getRight();
            return (range1.min() <= range2.max() && range1.max() >= range2.min());
        }).toList();
        System.out.println("Number of overlaps: " + overlappingPairs.size());
    }

}
