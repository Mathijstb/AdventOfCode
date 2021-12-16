package day16;

import fileUtils.FileReader;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Day16 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input16.csv");
        assert input.size() == 1;

        BigInteger code = new BigInteger(input.get(0), 16);
        int length = input.get(0).length() * 4;
        List<Packet> packets = findPackages(code, new Cursor(length), Optional.empty());
        printSumOfVersions(packets);
        printValue(packets);
    }

    private static List<Packet> findPackages(BigInteger code, Cursor cursor, Optional<Integer> packageLimit) {
        List<Packet> packets = new ArrayList<>();
        int numberOfPackages = 0;
        do {
            int version = readNextBits(code, cursor, 3).intValue();
            PacketType packetType = PacketType.of(readNextBits(code, cursor, 3).intValue());

            if (packetType == PacketType.LITERAL) {
                packets.add(new Packet(packetType, version, readLiteralValue(code, cursor), new ArrayList<>()));
            } else {
                packets.add(new Packet(packetType, version, 0, readSubPackages(code, cursor)));
            }
            numberOfPackages += 1;
            if (packageLimit.isPresent() && packageLimit.get() <= numberOfPackages) break;
        } while (hasMorePackages(code, cursor));
        return packets;
    }

    private static boolean hasMorePackages(BigInteger code, Cursor cursor) {
        return readNextBits(code, new Cursor(cursor.getValue()), cursor.getValue()).intValue() != 0;
    }

    private static List<Packet> readSubPackages(BigInteger code, Cursor cursor) {
        int lengthTypeId = readNextBits(code, cursor,1).intValue();
        switch(lengthTypeId) {
            case 0: {
                int subPacketsBitlength = readNextBits(code, cursor, 15).intValue();
                int length = (int)(Math.ceil(subPacketsBitlength / 4.0) * 4);
                BigInteger subCode = readNextBits(code, cursor, subPacketsBitlength).shiftLeft(length - subPacketsBitlength);
                return findPackages(subCode, new Cursor(length), Optional.empty());
            }
            case 1:  {
                int numberOfSubPackets = readNextBits(code, cursor, 11).intValue();
                return findPackages(code, cursor, Optional.of(numberOfSubPackets));
            }
            default: throw new IllegalArgumentException("Invalid length type id");
        }
    }

    private static long readLiteralValue(BigInteger code, Cursor cursor) {
        BigInteger number = BigInteger.ZERO;
        while (true) {
            BigInteger bitGroup = readNextBits(code, cursor, 5);
            number = number.shiftLeft(4).add(readNextBits(bitGroup, new Cursor(4), 4));
            if (!bitGroup.testBit(4)) break;
        }
        return number.longValue();
    }

    private static BigInteger readNextBits(BigInteger code, Cursor cursor, int number) {
        int cursorValue = cursor.getValue();
        BigInteger result = code.subtract(code.shiftRight(cursorValue).shiftLeft(cursorValue))
                                .shiftRight(cursorValue - number);
        cursor.move(number);
        return result;
    }

    private static void printSumOfVersions(List<Packet> packets) {
        int sumOfVersions = packets.stream().map(Packet::getSumOfVersions).mapToInt(v -> v).sum();
        System.out.println("Sum of versions: " + sumOfVersions);
    }

    private static void printValue(List<Packet> packets) {
        long value = packets.stream().map(Packet::getValue).mapToLong(v -> v).sum();
        System.out.println("Total value: " + value);
    }
}