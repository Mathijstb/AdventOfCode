package day13;

import com.google.common.collect.Lists;
import fileUtils.FileReader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day13 {
    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input13.csv");

        //Part a
        var pairs = readPairs(lines);
        var inOrderList = pairs.stream().map(Day13::isInOrder).toList();
        var inOrderIndices = IntStream.range(0, inOrderList.size())
                .filter(x -> inOrderList.get(x).orElseThrow()).boxed().map(x -> x + 1).toList();
        System.out.println("Pairs that are in order: " + inOrderIndices);
        System.out.println("Sum of indices: " + inOrderIndices.stream().reduce(0, Integer::sum));

        //Part b
        var packets = readPackets(lines);
        var divider1 = readPacket("[[2]]");
        var divider2 = readPacket("[[6]]");
        packets.add(divider1);
        packets.add(divider2);
        sortPackets(packets);

        //Print
        System.out.println();
        System.out.println("Sorted packets:");
        packets.forEach(System.out::println);
        int indexDivider1 = packets.indexOf(divider1) + 1;
        int indexDivider2 = packets.indexOf(divider2) + 1;
        System.out.println("divider1 index: " + indexDivider1);
        System.out.println("divider2 index: " + indexDivider2);
        System.out.println("Multiplication: " + indexDivider1 * indexDivider2);
    }

    private static List<Packet> readPackets(List<String> lines) {
        lines.removeIf(String::isEmpty);
        return lines.stream().map(Day13::readPacket).collect(Collectors.toList());
    }

    private static List<Pair> readPairs(List<String> lines) {
        lines.removeIf(String::isEmpty);
        var listOfTuples = Lists.partition(lines, 2);
        return listOfTuples.stream().map(tuple -> new Pair(readPacket(tuple.get(0)), readPacket(tuple.get(1)))).toList();
    }

    private static Packet readPacket(String line) {
        if (line.isEmpty()) {
            return new Packet(Optional.empty(), Collections.emptyList());
        }
        var packets = new ArrayList<Packet>();
        int index = 0;
        while (index < line.length()) {
            var character = line.charAt(index);
            if (character == '[') {
                int closeIndex = findCloseIndex(line, index);
                var subLine = line.substring(index + 1, closeIndex);
                packets.add(readPacket(subLine));
                index = closeIndex + 2;
            }
            else {
                var subLine = line.substring(index).split(",")[0];
                packets.add(new Packet(Optional.of(Integer.parseInt(subLine)), Collections.emptyList()));
                index = index + subLine.length() + 1;
            }
        }
        return new Packet(Optional.empty(), packets);
    }

    private static int findCloseIndex(String line, int openIndex) {
        int level = 0;
        for (int index = openIndex; index < line.length(); index++) {
            level += switch (line.charAt(index)) {
                case '[' -> 1;
                case ']' -> -1;
                default -> 0;
            };
            if (level == 0) return index;
        }
        throw new IllegalStateException("Can not find close index");
    }

    private static Optional<Boolean> isInOrder(Pair pair) {
        var left = pair.left();
        var right = pair.right();
        if (left.isValue() && right.isValue()) {
            if (left.getValue() < right.getValue()) return Optional.of(Boolean.TRUE);
            if (left.getValue() > right.getValue()) return Optional.of(Boolean.FALSE);
            return Optional.empty();
        }
        else  {
            var leftPackets = left.isValue() ? List.of(left) : left.packets;
            var rightPackets = right.isValue() ? List.of(right) : right.packets;
            return isPacketsInOrder(leftPackets, rightPackets);
        }
    }

    private static Optional<Boolean> isPacketsInOrder(List<Packet> leftPackets, List<Packet> rightPackets) {
        for (int i = 0; i < Integer.max(leftPackets.size(), rightPackets.size()); i++) {
            if (i >= leftPackets.size()) {
                return Optional.of(Boolean.TRUE);
            }
            if (i >= rightPackets.size()) {
                return Optional.of(Boolean.FALSE);
            }
            var result = isInOrder(new Pair(leftPackets.get(i), rightPackets.get(i)));
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }

    private static void sortPackets(List<Packet> packets) {
        packets.sort((a, b) -> {
            var isInOrder = isInOrder(new Pair(a, b));
            return isInOrder.map(aBoolean -> aBoolean ? -1 : 1).orElse(0);
        });
    }

}
