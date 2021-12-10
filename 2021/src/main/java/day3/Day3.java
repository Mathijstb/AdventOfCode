package day3;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class Day3 {

    private static int NUMBER_OF_BITS = 0;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input3.csv");
        NUMBER_OF_BITS = lines.get(0).length();
        List<BitSet> bitSets = lines.stream().map(Day3::bitSetfromString).collect(Collectors.toList());

        long gammaRate = convert(getGammaRate(bitSets));
        long epsilonRate = getEpsilonRate(bitSets).toLongArray()[0];
        System.out.println("Gamma rate: " + gammaRate);
        System.out.println("Epsilon rate: " + epsilonRate);
        System.out.println("Power consumption: " + gammaRate * epsilonRate);

        long oxygenRating = getOxygenRating(bitSets).toLongArray()[0];
        long co2ScrubberRating = getCo2ScrubberRating(bitSets).toLongArray()[0];
        System.out.println("Oxygen rating: " + oxygenRating);
        System.out.println("CO2 scrubber rating: " + co2ScrubberRating);
        System.out.println("Life support rating: " + oxygenRating * co2ScrubberRating);

    }

    private static BitSet bitSetfromString(String binary) {
        BitSet bitset = new BitSet(binary.length());
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                bitset.set(NUMBER_OF_BITS-i-1);
            }
        }
        return bitset;
    }

    public static long convert(BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }


    private static BitSet getOxygenRating(List<BitSet> bitSets) {
        List<BitSet> result = new ArrayList<>(bitSets);
        while (result.size() != 1) {
            for (int i = 0; i < NUMBER_OF_BITS; i++) {
                int index = NUMBER_OF_BITS - i -1;
                BitSet gammaRate = getGammaRate(result);
                boolean isOneCommon = gammaRate.get(index);
                result = result.stream().filter(bitSet -> bitSet.get(index) == isOneCommon).collect(Collectors.toList());
            }
        }
        return result.get(0);
    }

    private static BitSet getCo2ScrubberRating(List<BitSet> bitSets) {
        List<BitSet> result = new ArrayList<>(bitSets);
        while (result.size() != 1) {
            for (int i = 0; i < NUMBER_OF_BITS; i++) {
                int index = NUMBER_OF_BITS - i -1;
                BitSet epsilonRate = getEpsilonRate(result);
                boolean isZeroCommon = epsilonRate.get(index);
                result = result.stream().filter(bitSet -> bitSet.get(index) == isZeroCommon).collect(Collectors.toList());
                if (result.size() <= 1) break;
            }
        }
        return result.get(0);
    }

    private static BitSet getGammaRate(List<BitSet> bitSets) {
        BitSet result = new BitSet();
        for (int i = 0; i < NUMBER_OF_BITS; i++) {
            if (isOneCommon(bitSets, i)) {
                result.set(i);
            }
        }
        return result;
    }

    private static BitSet getEpsilonRate(List<BitSet> bitSets) {
        BitSet result = new BitSet();
        for (int i = 0; i < NUMBER_OF_BITS; i++) {
            if (!isOneCommon(bitSets, i)) {
                result.set(i);
            }
        }
        return result;
    }

    private static long numberOfBitsTrue(List<BitSet> bitSets, int position) {
        return bitSets.stream().filter(bitSet -> bitSet.get(position)).count();
    }

    private static boolean isOneCommon(List<BitSet> bitSets, int position) {
        return numberOfBitsTrue(bitSets, position) >= (double) bitSets.size() / 2;
    }
}
