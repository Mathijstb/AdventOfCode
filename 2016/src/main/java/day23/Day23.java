package day23;

import fileUtils.FileReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input23.csv");
        var registers = new HashMap<String, Register>();

        //part a
//        Stream.of("a", "b", "c", "d").forEach(name -> registers.put(name, new Register(0)));
//        registers.get("a").setValue(7);
//        var instructions = readInstructions(lists, registers);
//        executeInstructions(instructions);
//        var valueOfRegisterA = registers.get("a").getValue();
//        System.out.println("Value of register a: " + valueOfRegisterA);

        //part b
        Stream.of("a", "b", "c", "d").forEach(name -> registers.put(name, new Register(0)));
        registers.get("a").setValue(12);
        var instructions2 = readInstructions(lists, registers);
        executeInstructions(instructions2, registers);
        var valueOfRegisterA2 = registers.get("a").getValue();
        System.out.println("Value of register a: " + valueOfRegisterA2);
    }

    private static List<Instruction> readInstructions(List<String> lists, Map<String, Register> registers) {
        return lists.stream().map(list -> {
            var instructionType = InstructionType.getInstructionType(list.substring(0 , 3));
            var params = list.substring(4).split(" ");
            var pointer1 = getPointer(params[0], registers);
            Optional<Pointer> pointer2 = (params.length > 1) ? Optional.of(getPointer(params[1], registers)) : Optional.empty();
            return new Instruction(instructionType, pointer1, pointer2);
        }).collect(Collectors.toList());
    }

    private static Pointer getPointer(String parameter, Map<String, Register> registers) {
        return Pattern.compile("[\\-0-9]+").matcher(parameter).matches()
                ? new Pointer(Optional.empty(), Optional.of(Integer.parseInt(parameter)))
                : new Pointer(Optional.of(registers.get(parameter)), Optional.empty());
    }

    private static void executeInstructions(List<Instruction> instructions, Map<String, Register> registers) {
        int instructionIndex = 0;
        while (instructionIndex >= 0 && instructionIndex < instructions.size()) {
            //optimization
            if (instructionIndex == 3) {
                registers.get("a").setValue(registers.get("b").getValue() * registers.get("d").getValue());
                registers.get("c").setValue(0);
                registers.get("d").setValue(0);
                instructionIndex = 10;
                continue;
            }
            System.out.println("Index: " + instructionIndex);
            printRegisters(registers);
            System.out.println();
            var instruction = instructions.get(instructionIndex);
            switch (instruction.getType()) {
                case COPY -> {
                    int value = instruction.getParam1().getValue();
                    instruction.getParam2().orElseThrow().getRegister().setValue(value);
                    instructionIndex += 1;
                }
                case JUMP -> {
                    int value = instruction.getParam1().getValue();
                    instructionIndex += value != 0 ? instruction.getParam2().orElseThrow().getValue() : 1;
                }
                case INCREASE -> {
                    var register = instruction.getParam1().getRegister();
                    register.setValue(register.getValue() + 1);
                    instructionIndex += 1;
                }
                case DECREASE -> {
                    var register = instruction.getParam1().getRegister();
                    register.setValue(register.getValue() - 1);
                    instructionIndex += 1;
                }
                case TOGGLE -> {
                    int toggleIndex = instructionIndex + instruction.getParam1().getValue();
                    if (toggleIndex != 0 && toggleIndex < instructions.size()) {
                        var toggleInstruction = instructions.get(toggleIndex);
                        InstructionType newType = switch (toggleInstruction.getType()) {
                            case INCREASE -> InstructionType.DECREASE;
                            case DECREASE -> InstructionType.INCREASE;
                            case JUMP -> InstructionType.COPY;
                            case COPY -> InstructionType.JUMP;
                            case TOGGLE -> InstructionType.INCREASE;
                        };
                        toggleInstruction.setType(newType);
                    }
                    instructionIndex += 1;
                }
            }
        }
    }

    private static void printRegisters(Map<String, Register> registers) {
        StringBuilder sb = new StringBuilder();
        registers.forEach((name, value1) -> {
            var value = value1.getValue();
            sb.append(String.format("%s %d  ", name, value));
        });
        System.out.println(sb);
    }

}