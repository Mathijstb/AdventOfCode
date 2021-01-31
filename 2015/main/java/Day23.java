import fileUtils.FileReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Day23 {

    private enum Type {
        HALF("hlf"),
        TRIPLE("tpl"),
        INCREASE("inc"),
        JUMP("jmp"),
        JUMP_IF_EVEN("jie"),
        JUMP_IF_ONE("jio");

        private final String text;

        Type(String text) {
            this.text = text;
        }

        public static Type fromText(String text) {
            return Arrays.stream(values()).filter(t -> t.text.equalsIgnoreCase(text)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid value"));
        }
    }

    @Value
    private static class Instruction {
        Type type;
        Register register;
        Integer amount;
    }

    @Data
    @AllArgsConstructor
    private static class Register {
        String name;
        long value = 0;
    }

    private static Register registerA = new Register("a", 1);
    private static Register registerB = new Register("b", 0);
    private static List<Instruction> instructions;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input23.csv");
        readInstructions(lines);
        executeInstructions(instructions);
    }

    private static void executeInstructions(List<Instruction> instructions) {
        int index = 0;
        while (true) {
            Instruction instruction = instructions.get(index);
            executeInstruction(instruction);

            Optional<Integer> nextIndex = getNextInstructionIndex(instruction, index);
            if (nextIndex.isEmpty()) break;
            index = nextIndex.get();
        }
        System.out.println("Register a value: " + registerA.value);
        System.out.println("Register b value: " + registerB.value);
    }

    public static void executeInstruction(Instruction instruction) {
        Type type = instruction.type;
        Register register = instruction.register;
        Integer amount = instruction.amount;
        switch (instruction.type) {
            case HALF: register.value /= 2; break;
            case TRIPLE: register.value *= 3; break;
            case INCREASE: register.value +=1; break;
        }
    }

    public static Optional<Integer> getNextInstructionIndex(Instruction instruction, int index) {
        Register register = instruction.register;
        Integer amount = instruction.amount;
        switch (instruction.type) {
            case JUMP: index += amount; break;
            case JUMP_IF_EVEN: index += (register.value % 2 == 0) ? amount : 1; break;
            case JUMP_IF_ONE: index += (register.value == 1) ? amount : 1; break;
            default: index += 1;
        }
        return (index >= 0 && index < instructions.size()) ? Optional.of(index) : Optional.empty();
    }

    private static void readInstructions(List<String> lines) {
        instructions = lines.stream().map(line -> {
            String[] parts = line.split(" ");
            Type type = Type.fromText(parts[0]);
            String amount = null;
            String reg = null;
            switch (type) {
                case JUMP: amount = parts[1]; break;
                case JUMP_IF_EVEN:
                case JUMP_IF_ONE: {
                    reg = parts[1].split(",")[0];
                    amount = parts[2]; } break;
                default: reg = parts[1].split(",")[0];
            }
            Register register = reg == null ? null : reg.equals(registerA.name) ? registerA : registerB;
            Integer a = amount != null ? Integer.parseInt(amount) : null;
            return new Instruction(type, register, a);
        }).collect(Collectors.toList());
    }

}
