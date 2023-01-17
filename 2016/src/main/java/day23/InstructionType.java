package day23;

import java.util.Arrays;

public enum InstructionType {
    COPY("cpy"),
    INCREASE("inc"),
    DECREASE("dec"),
    JUMP("jnz"),
    TOGGLE("tgl");

    private final String shortName;

    InstructionType(String shortName) {
        this.shortName = shortName;
    }

    public static InstructionType getInstructionType(String shortName) {
        return Arrays.stream(InstructionType.values()).filter(type -> type.shortName.equals(shortName)).findFirst().orElseThrow();
    }
}
