package day25;

import java.util.Optional;

public record Pointer(Optional<Register> register, Optional<Integer> value) {

    public int getValue() {
        return register.map(Register::getValue).orElseGet(value::orElseThrow);
    }

    public Register getRegister() {
        return register.orElseThrow(() -> new IllegalCallerException("No register found"));
    }
}
