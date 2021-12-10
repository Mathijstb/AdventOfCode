package day21;

import fileUtils.FileReader;
import intCode.IntCodeComputer;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day21 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input21.csv");
        String line = lines.stream().findFirst().orElseThrow();
        List<String> numbers = Arrays.stream(line.split(",")).collect(Collectors.toList());
        executeProgram(numbers);
    }

    private static void executeProgram(List<String> numbers) {
        IntCodeComputer.start(numbers);
        System.out.println(IntCodeComputer.getOutput());
//        addInstruction(new Instruction(Operator.NOT, Register.C, Register.T));
//        addInstruction(new Instruction(Operator.OR, Register.T, Register.J));
//        addInstruction(new Instruction(Operator.NOT, Register.B, Register.T));
//        addInstruction(new Instruction(Operator.OR, Register.T, Register.J));
//        addInstruction(new Instruction(Operator.NOT, Register.A, Register.T));
//        addInstruction(new Instruction(Operator.OR, Register.T, Register.J));
//        addInstruction(new Instruction(Operator.AND, Register.D, Register.J));
//        addInstruction("WALK");


        addInstruction(new Instruction(Operator.NOT, Register.C, Register.T));
        addInstruction(new Instruction(Operator.OR, Register.T, Register.J));
        addInstruction(new Instruction(Operator.NOT, Register.B, Register.T));
        addInstruction(new Instruction(Operator.OR, Register.T, Register.J));
        addInstruction(new Instruction(Operator.NOT, Register.A, Register.T));
        addInstruction(new Instruction(Operator.OR, Register.T, Register.J));
        addInstruction(new Instruction(Operator.AND, Register.D, Register.J));
        addInstruction(new Instruction(Operator.AND, Register.E, Register.T));
        addInstruction(new Instruction(Operator.OR, Register.H, Register.T));
        addInstruction(new Instruction(Operator.AND, Register.T, Register.J));

        addInstruction("RUN");
        System.out.println(IntCodeComputer.getOutput());
    }

    private enum Operator {
        OR, AND, NOT
    }
    private enum Register {
        A, B, C, D, E, F, G, H, I, T, J
    }

    @Value
    private static class Instruction {
        Operator operator;
        Register firstRegister;
        Register secondRegister;
    }

    private static void addInstruction(String instruction) {
        List<Integer> input = new ArrayList<>();
        instruction.chars().forEach(input::add);
        input.add((int) '\n');
        IntCodeComputer.addInput(input);
        System.out.printf("Input: %s%n", instruction);
    }

    private static void addInstruction(Instruction instruction) {
        addInstruction(instruction.operator.name() + " " + instruction.firstRegister.name() + " " + instruction.secondRegister.name());
    }

}
