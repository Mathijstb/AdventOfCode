package day19;

import fileUtils.FileReader;

import java.math.BigInteger;

public class Day19 {

    public static void execute() {
        long numberOfElves = Long.parseLong(FileReader.getFileReader().readFile("input19.csv").stream().findFirst().orElseThrow());

        //part a
        determineLast(numberOfElves);

        //part b
        determineLast2(numberOfElves);
    }

    private static void determineLast(long numberOfElves) {
        // - every even elf gets removed
        // - if numberOfElves = 2^a + r, with 2^a such that it is maximal : the remaining elf number = 2 * r + 1, where
        var amount = BigInteger.valueOf(numberOfElves);
        var newAmount = amount.shiftLeft(1);
        var remainingElfNumber = newAmount.clearBit(newAmount.bitLength() - 1).setBit(0).longValue();
        System.out.printf("Number of elves: %d, last elf: %d", numberOfElves, remainingElfNumber);
        System.out.println();
    }

    private static void determineLast2(long numberOfElves) {
        // - if 3^k + 1 <= n <= 2 * 3^k, remaining elf number = n - 3^k
        // - if 2 * 3^k + 1 <= n <= 3^(k+1), remaining elf number 3^k + 2 * (n - 2 * 3^k) = 2 * n - 3^(k+1)

        long remainingElfNumber;
        if (numberOfElves == 1) {
            remainingElfNumber = 1;
        }
        else {
            var amountBase3 = BigInteger.valueOf(numberOfElves - 1).toString(3);
            var power = amountBase3.length() - 1;

            if (Integer.parseInt(String.valueOf(amountBase3.charAt(0))) < 2) {
                remainingElfNumber = numberOfElves - (long) Math.pow(3, power);
            }
            else {
                remainingElfNumber = 2 * numberOfElves - (long) Math.pow(3, power + 1);
            }
        }
        System.out.printf("Number of elves: %d, last elf: %d", numberOfElves, remainingElfNumber);
        System.out.println();
    }
}