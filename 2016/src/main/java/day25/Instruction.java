package day25;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Instruction {
    private InstructionType type;

    private final Pointer param1;

    private final Optional<Pointer> param2;

    public Instruction(InstructionType type, Pointer param1, Optional<Pointer> param2) {
        this.type = type;
        this.param1 = param1;
        this.param2 = param2;
    }

    public InstructionType getType() {
        return type;
    }

    public void setType(InstructionType type) {
        this.type = type;
    }

    public Pointer getParam1() {
        return param1;
    }

    public Optional<Pointer> getParam2() {
        return param2;
    }
}
