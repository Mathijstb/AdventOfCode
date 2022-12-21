package day21;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Monkey {

    public String name;

    public Optional<Long> value;

    public Optional<Expression> expression;

    public Monkey(String name, Optional<Long> value, Optional<Expression> expression) {
        this.name = name;
        this.value = value;
        this.expression = expression;
    }

    public Monkey copy() {
        return new Monkey(name, value, expression);
    }
}
