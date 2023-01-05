package day12;

import java.util.Optional;

public record Instruction(InstructionType type, Pointer param1, Optional<Pointer> param2) {
}
