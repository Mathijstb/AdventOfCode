package day15;

import java.util.Optional;

public record Instruction(String label, Operation operation, Optional<Integer> focalLength) {
}
