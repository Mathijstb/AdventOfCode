package day12;

import fileUtils.FileReader;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.regex.qual.Regex;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input12.csv");
        var registers = new HashMap<String, Register>();

        //part a
        Stream.of("a", "b", "c", "d").forEach(name -> registers.put(name, new Register(0)));
        var instructions = readInstructions(lists, registers);
        executeInstructions(instructions, registers);
        System.out.println("Value of register a: " + registers.get("a").getValue());

        //part b
        registers.values().forEach(register -> register.setValue(0));
        registers.get("c").setValue(1);
        executeInstructions(instructions, registers);
        System.out.println("Value of register a: " + registers.get("a").getValue());
    }

    private static List<Instruction> readInstructions(List<String> lists, Map<String, Register> registers) {
        return lists.stream().map(list -> {
            var instructionType = InstructionType.getInstructionType(list.substring(0 , 3));
            var params = list.substring(4).split(" ");
            var pointer1 = getPointer(params[0], registers);
            Optional<Pointer> pointer2 = (params.length > 1) ? Optional.of(getPointer(params[1], registers)) : Optional.empty();
            return new Instruction(instructionType, pointer1, pointer2);
        }).toList();
    }

    private static Pointer getPointer(String parameter, Map<String, Register> registers) {
        return Pattern.compile("[\\-0-9]+").matcher(parameter).matches()
                ? new Pointer(Optional.empty(), Optional.of(Integer.parseInt(parameter)))
                : new Pointer(Optional.of(registers.get(parameter)), Optional.empty());
    }

    private static void executeInstructions(List<Instruction> instructions, Map<String, Register> registers) {
        int instructionIndex = 0;
        while (instructionIndex >= 0 && instructionIndex < instructions.size()) {
            var instruction = instructions.get(instructionIndex);
            switch (instruction.type()) {
                case COPY -> {
                    int value = instruction.param1().getValue();
                    instruction.param2().orElseThrow().getRegister().setValue(value);
                    instructionIndex += 1;
                }
                case JUMP -> {
                    int value = instruction.param1().getValue();
                    instructionIndex += value != 0 ? instruction.param2().orElseThrow().getValue() : 1;
                }
                case INCREASE -> {
                    var register = instruction.param1().getRegister();
                    register.setValue(register.getValue() + 1);
                    instructionIndex += 1;
                }
                case DECREASE -> {
                    var register = instruction.param1().getRegister();
                    register.setValue(register.getValue() - 1);
                    instructionIndex += 1;
                }
            }
        }
    }
}