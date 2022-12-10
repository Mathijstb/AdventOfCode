package day10;

import java.util.Optional;

public record Instruction(InstructionType type, Optional<Integer> parameter) {
}
