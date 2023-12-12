package day6;

import fileUtils.FileReader;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day6 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input6.csv");
        var races = parseLines(lines);
        var numberOfWays = races.stream().map(Day6::findNumberOfWaysToBeatRecord).toList();
        System.out.println("Number of ways: " + numberOfWays);
        var multiplications = numberOfWays.stream().reduce(1L, (a, b) -> a * b);
        System.out.println("Multiplication: " + multiplications);

        var race = parseLines2(lines);
        var result2 = findNumberOfWaysToBeatRecord2(race);
        System.out.println("Number of ways: " + result2);
    }

    private static List<Race> parseLines(List<String> lines) {
        var times = Arrays.stream(lines.get(0).split("Time:\\s+")[1].split("\\s+"))
                .map(Integer::parseInt).toList();
        var distances = Arrays.stream(lines.get(1).split("Distance:\\s+")[1].split("\\s+"))
                .map(Integer::parseInt).toList();
        assert(times.size() == distances.size());
        return IntStream.range(0, times.size()).mapToObj(i -> new Race(times.get(i), distances.get(i))).toList();
    }

    private static Race parseLines2(List<String> lines) {
        var time = Long.parseLong(StringUtils.remove(lines.get(0).split("Time:\\s+")[1], " "));
        var distance = Long.parseLong(StringUtils.remove(lines.get(1).split("Distance:\\s+")[1], " "));
        return new Race(time, distance);
    }

    private static long findNumberOfWaysToBeatRecord(Race race) {
        return LongStream.range(0, race.time() + 1)
                .mapToObj(pushTime -> (race.time() - pushTime) * pushTime)
                .filter(value -> value > race.recordDistance())
                .count();
    }

    private static long findNumberOfWaysToBeatRecord2(Race race) {
        var time = race.time();
        var distance = race.recordDistance();
        var sqrt = Math.sqrt(time * time - 4 * distance);
        var lowerBound = (long) Math.ceil((time - sqrt) / 2);
        var upperBound = (long) Math.floor((time + sqrt) / 2);
        return upperBound - lowerBound + 1;

    }
}
