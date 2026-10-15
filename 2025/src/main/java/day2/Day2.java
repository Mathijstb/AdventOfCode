package day2;

import fileUtils.FileReader;
import java.util.*;
import java.util.function.IntPredicate;

public class Day2 {

    private record Range(long first, long last) {}

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input2.csv");
        List<Range> ranges = Arrays.stream(String.join("", lines).split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    var parts = s.split("-");
                    return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                }).toList();
        System.out.println("Sum of invalid IDs: " + sumInvalidIds(ranges, repeats -> repeats == 2));
    }

    private static long sumInvalidIds(List<Range> ranges, IntPredicate allowedRepeats) {
        return ranges.stream()
                .mapToLong(range -> findInvalidIds(range, allowedRepeats).stream().mapToLong(Long::longValue).sum())
                .sum();
    }

    // An invalid ID of length seedLength * repeats is seed * (1 + 10^seedLength + 10^(2 * seedLength) + ...)
    private static Set<Long> findInvalidIds(Range range, IntPredicate allowedRepeats) {
        Set<Long> invalidIds = new HashSet<>();
        for (int length = digits(range.first()); length <= digits(range.last()); length++) {
            for (int repeats = 2; repeats <= length; repeats++) {
                if (length % repeats != 0 || !allowedRepeats.test(repeats)) continue;
                int seedLength = length / repeats;
                long multiplier = 0;
                for (int i = 0; i < repeats; i++) {
                    multiplier = multiplier * pow10(seedLength) + 1;
                }
                long minSeed = Math.max(pow10(seedLength - 1), ceilDiv(range.first(), multiplier));
                long maxSeed = Math.min(pow10(seedLength) - 1, range.last() / multiplier);
                for (long seed = minSeed; seed <= maxSeed; seed++) {
                    invalidIds.add(seed * multiplier);
                }
            }
        }
        return invalidIds;
    }

    private static int digits(long number) {
        return Long.toString(number).length();
    }

    private static long pow10(int exponent) {
        long result = 1;
        for (int i = 0; i < exponent; i++) result *= 10;
        return result;
    }

    private static long ceilDiv(long dividend, long divisor) {
        return (dividend + divisor - 1) / divisor;
    }
}
