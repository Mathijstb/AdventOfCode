package day22;

import java.util.Optional;

public record Instruction(InstructionType type, Optional<Integer> value) {
}
