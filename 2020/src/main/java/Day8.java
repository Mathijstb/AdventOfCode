import fileUtils.FileReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class Day8 {

    private enum Operation {
        NEXT, ACC, JMP;

        public static Operation fromString(String value) {
            switch (value) {
                case "nop" : return NEXT;
                case "acc" : return ACC;
                case "jmp" : return JMP;
                default : throw new IllegalArgumentException("invalid string value");
            }
        }
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    private static class Instruction {
        @NonNull
        Operation operation;
        int value;
        boolean executed = false;

        public Instruction(@NonNull Operation operation, int value) {
            this.operation = operation;
            this.value = value;
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input8.csv");
        List<Instruction> instructions = lines.stream().map(Day8::getInstruction).collect(Collectors.toList());
        for (int i = 0; i < instructions.size(); i++) {
            for (Instruction instruction: instructions) {
                instruction.setExecuted(false);
            }
            Instruction curInstruction = instructions.get(i);
            List<Instruction> changedInstructions = instructions.stream().map(instr -> {
                if (instr == curInstruction) {
                    if (instr.operation == Operation.NEXT) {
                        return new Instruction(Operation.JMP, curInstruction.value, curInstruction.executed);
                    }
                    else if (instr.operation == Operation.JMP) {
                        return new Instruction(Operation.NEXT, curInstruction.value, curInstruction.executed);
                    }
                }
                return instr;
            }).collect(Collectors.toList());
            Instruction changedInstruction = changedInstructions.get(i);

            if (executeInstructions(changedInstructions)) {
                System.out.printf(
                        "Succes! The following instruction was changed: oldInstruction: %s, newInstruction: %s, index: %s%n",
                        instructions.get(i), changedInstruction, i);
            };
        }
        //executeInstructions(instructions);
    }

    private static Instruction getInstruction(String line) {
        Operation operation = Operation.fromString(line.substring(0, 3));
        int value = Integer.parseInt(line.substring(4));
        return new Instruction(operation, value);
    }

    private static boolean executeInstructions(List<Instruction> instructions) {
        int accumulator = 0;
        int curIndex = 0;
        while (true) {
            // check curIndex is last index (succes)
            if (curIndex == instructions.size()) {
                System.out.println("Succes! Accumulator value: " + accumulator);
                return  true;
            }

            // check curIndex within bounds
            if (curIndex < 0 || curIndex > instructions.size()) {
                System.out.println("Index out of bounds: " + curIndex);
                return false;
            }

            // check loop
            Instruction instruction = instructions.get(curIndex);
            if (instruction.executed) {
                System.out.println("Instruction already executed, accumulator value: " + accumulator);
                return false;
            }

            //execute instruction
            instruction.executed = true;
            switch (instruction.operation) {
                case NEXT: {
                    curIndex += 1;
                    break;
                }
                case ACC: {
                    curIndex += 1;
                    accumulator += instruction.value;
                    break;
                }
                case JMP: {
                    curIndex += instruction.value;
                    break;
                }
            }
        }
    }
}
