package day21;

import java.util.Optional;

public record Instruction(InstructionType type, Optional<String> param1, Optional<String> param2) {
}
