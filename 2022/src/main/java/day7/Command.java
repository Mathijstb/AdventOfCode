package day7;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Command {
    public CommandType type;
    public Optional<String> parameter;

    public Command(CommandType type, Optional<String> parameter) {
        this.type = type;
        this.parameter = parameter;
    }
}