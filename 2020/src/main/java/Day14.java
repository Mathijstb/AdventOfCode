import fileUtils.FileReader;
import lombok.Data;
import lombok.Value;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {

    @Value
    private static class Operation {
        long index;
        long value;
    }

    @Data
    private static class Instruction {

        String bitMask;
        List<Operation> operations = new ArrayList<>();
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input14.csv");
        List<Instruction> instructions = readInstructions(lines);
        executeInstructions2(instructions);
    }

    private static List<Instruction> readInstructions(List<String> lines) {
        List<Instruction> instructions = new ArrayList<>();
        Instruction instruction = new Instruction();
        for (String line: lines) {
            if (line.contains("mask")) {
                if (!instruction.operations.isEmpty()) instructions.add(instruction);
                instruction = new Instruction();
                instruction.setBitMask(line.substring(line.indexOf("=") + 2));
            }
            else {
                int index = Integer.parseInt(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
                int value = Integer.parseInt(line.substring(line.indexOf("=") + 2));
                instruction.operations.add(new Operation(index, value));
            }
        }
        if (!instruction.operations.isEmpty()) instructions.add(instruction);
        return instructions;
    }

    private static void executeInstructions(List<Instruction> instructions) {
        Map<Long, BigInteger> memory = new HashMap<>();
        for (Instruction instruction: instructions) {
            String bitmask = instruction.bitMask;
            for (Operation operation: instruction.operations) {
                memory.put(operation.index, applyBitMask(operation.value, bitmask));
            }
        }
        BigInteger total = memory.values().stream().reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println("total: " + total);
    }

    private static void executeInstructions2(List<Instruction> instructions) {
        Map<BigInteger, Long> memory = new HashMap<>();
        for (Instruction instruction: instructions) {
            String bitmask = instruction.bitMask;
            bitmask = bitmask.replace('0', '2');
            for (Operation operation: instruction.operations) {
                List<String> bitmasks = getBitMasks(bitmask);
                bitmasks.forEach(bm -> {
                    BigInteger memoryAddres = applyBitMask(operation.index, bm);
                    memory.put(memoryAddres, operation.value);
                });
            }
        }
        Long total = memory.values().stream().reduce(0L, Long::sum);
        System.out.println("total: " + total);
    }

    private static List<String> getBitMasks(String bitmask) {
        int index = bitmask.indexOf('X');
        if (index < 0) {
            return Collections.singletonList(bitmask);
        }
        else {
            String bitmask1 = bitmask.replaceFirst("[X]", "0");
            String bitmask2 = bitmask.replaceFirst("[X]", "1");
            return Stream.concat(getBitMasks(bitmask1).stream(), getBitMasks(bitmask2).stream()).collect(Collectors.toList());
        }
    }

    private static BigInteger applyBitMask(long value, String bitmask) {
        BigInteger bigValue = new BigInteger(String.valueOf(value));
        for (int i = 0; i < bitmask.length(); i++) {
            char character = bitmask.charAt(bitmask.length() - 1 - i);
            if (character == '1') {
                bigValue = bigValue.setBit(i);
            } else if (character == '0') {
                bigValue = bigValue.clearBit(i);
            }
        }
        return bigValue;
    }

}
