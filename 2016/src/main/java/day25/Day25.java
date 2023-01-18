package day25;

import fileUtils.FileReader;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day25 {

    private static final Map<String, Register> registers = new HashMap<>();

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input25.csv");
        Stream.of("a", "b", "c", "d").forEach(name -> registers.put(name, new Register(0)));
        var instructions = readInstructions(lists);

        for (int i = 0; i <= 196; i++) {
            int instructionIndex = 0;
            registers.values().forEach(register -> register.setValue(0));
            registers.get("a").setValue(i);
            List<Integer> responses = new ArrayList<>();
            while (true) {
                var responseOpt = executeInstructions(instructions, instructionIndex);
                if (responseOpt.isPresent()) {
                    var response = responseOpt.get();
                    responses.add(response.outValue());
                    instructionIndex = response.nextInstructionIndex();
                }
                else {
                    break;
                }
            }
            System.out.printf("For input %d, the response is: %s%n", i, responses);
            if (IntStream.range(0, responses.size()).filter(x -> (x % 2) == 0).mapToObj(responses::get).allMatch(x -> x == 0) &&
                    IntStream.range(0, responses.size()).filter(x -> (x % 2) == 1).mapToObj(responses::get).allMatch(x -> x == 1)) {
                System.out.println();
                System.out.println("Match found for input: " + i);
                break;
            }
        }

    }

    private static List<Instruction> readInstructions(List<String> lists) {
        return lists.stream().map(list -> {
            var instructionType = InstructionType.getInstructionType(list.substring(0 , 3));
            var params = list.substring(4).split(" ");
            var pointer1 = getPointer(params[0]);
            Optional<Pointer> pointer2 = (params.length > 1) ? Optional.of(getPointer(params[1])) : Optional.empty();
            return new Instruction(instructionType, pointer1, pointer2);
        }).collect(Collectors.toList());
    }

    private static Pointer getPointer(String parameter) {
        return Pattern.compile("[\\-0-9]+").matcher(parameter).matches()
                ? new Pointer(Optional.empty(), Optional.of(Integer.parseInt(parameter)))
                : new Pointer(Optional.of(registers.get(parameter)), Optional.empty());
    }

    private static Optional<Response> executeInstructions(List<Instruction> instructions, int instructionIndex) {
        while (instructionIndex >= 0 && instructionIndex < instructions.size()) {
            if (instructionIndex == 3) {
                registers.get("d").setValue(registers.get("d").getValue() + registers.get("b").getValue() * registers.get("c").getValue());
                registers.get("b").setValue(0);
                registers.get("c").setValue(0);
                instructionIndex = 8;
                continue;
            }
            if (instructionIndex == 13) {
                registers.get("a").setValue(registers.get("b").getValue() / registers.get("c").getValue());
                registers.get("b").setValue(registers.get("b").getValue() % registers.get("c").getValue());
                registers.get("c").setValue(0);
                instructionIndex = 27;
                continue;
            }
            if (instructionIndex == 29) {
                return Optional.empty();
            }
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
                            case OUT -> InstructionType.INCREASE;
                        };
                        toggleInstruction.setType(newType);
                    }
                    instructionIndex += 1;
                }
                case OUT -> {
                    instructionIndex += 1;
                    var outValue = instruction.getParam1().getValue();
                    return Optional.of(new Response(outValue, instructionIndex));
                }
            }
        }
        return Optional.empty();
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