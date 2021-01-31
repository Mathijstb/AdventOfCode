import fileUtils.FileReader;
import lombok.Value;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day7 {

    private enum Operation {
        AND,
        OR,
        LSHIFT,
        RSHIFT,
        NOT,
        APPLY
    }

    final static String strAnd = " AND ";
    final static String strOr = " OR ";
    final static String strLeft = " LSHIFT ";
    final static String strRight = " RSHIFT ";
    final static String strNot = "NOT ";

    @Value
    private static class Operand {
        BigInteger value;
        String reference;

        public Optional<BigInteger> getValue() {
            if (value != null) return Optional.of(value);
            return wireMap.containsKey(reference) ? Optional.of(wireMap.get(reference)) : Optional.empty();
        }
    }

    @Value
    private static class Instruction {
        Operation operation;
        Operand op1;
        Operand op2;
        String reference;
    }

    private static Map<String, BigInteger> wireMap = new HashMap<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input7b.csv");
        List<Instruction> instructions = getInstructions(lines);
        executeInstructions(instructions);
        System.out.println("Value of wire a: " + wireMap.get("a"));
    }

    private static void executeInstructions(List<Instruction> instructions) {
        while(instructions.size() > 0) {
            List<Instruction> executeInstructions = new ArrayList<>(instructions);
            for (Instruction instruction : executeInstructions) {
                Optional<BigInteger> optionalValue1 = instruction.op1.getValue();
                if (optionalValue1.isEmpty()) continue;

                BigInteger value1 = optionalValue1.get();
                Operation operation = instruction.operation;
                if (operation.equals(Operation.APPLY)) {
                    wireMap.put(instruction.reference, value1);
                    instructions.remove(instruction);
                    continue;
                }
                else if (operation.equals(Operation.NOT)) {
                    for (int i = 0; i < 16; i++) {
                        value1 = value1.flipBit(i);
                    }
                    wireMap.put(instruction.reference, value1);
                    instructions.remove(instruction);
                    continue;
                }

                Optional<BigInteger> optionalValue2 = instruction.op2.getValue();
                if (optionalValue2.isEmpty()) continue;

                BigInteger value2 = optionalValue2.get();
                BigInteger result;
                switch (operation) {
                    case AND: result = value1.and(value2) ; break;
                    case OR: result = value1.or(value2); break;
                    case LSHIFT: result = value1.shiftLeft(value2.intValue()); break;
                    case RSHIFT: result = value1.shiftRight(value2.intValue()); break;
                    default: throw new IllegalStateException("invalid operation");
                }
                wireMap.put(instruction.reference, result);
                instructions.remove(instruction);
            }
        }
    }

    private static List<Instruction> getInstructions(List<String> lines) {
        return lines.stream().map(line -> {
            String[] leftAndRightSide = line.split(" -> ");
            String leftSide = leftAndRightSide[0];
            String rightSide = leftAndRightSide[1];
            Operation operation = getOperation(leftSide);
            String[] operands;
            switch (operation) {
                case AND: operands = leftSide.split(strAnd); break;
                case OR: operands = leftSide.split(strOr); break;
                case LSHIFT: operands = leftSide.split(strLeft) ; break;
                case RSHIFT: operands = leftSide.split(strRight); break;
                case NOT: return new Instruction(operation, getOperand(leftSide.substring(strNot.length())), null, rightSide);
                case APPLY: return new Instruction(operation, getOperand(leftSide), null, rightSide);
                default: throw new IllegalStateException("invalid operation");
            }
            return new Instruction(operation, getOperand(operands[0]), getOperand(operands[1]), rightSide);
        }).collect(Collectors.toList());
    }

    private static Operand getOperand(String line) {
        return Pattern.matches("[0-9]*", line) ? new Operand(new BigInteger(line), null) : new Operand(null, line);
    }

    private static Operation getOperation(String line) {
        if (line.contains(strAnd)) return Operation.AND;
        if (line.contains(strOr)) return Operation.OR;
        if (line.contains(strLeft)) return Operation.LSHIFT;
        if (line.contains(strRight)) return Operation.RSHIFT;
        if (line.contains(strNot)) return Operation.NOT;
        return Operation.APPLY;
    }

}
