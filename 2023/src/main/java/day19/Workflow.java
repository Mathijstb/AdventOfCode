package day19;

import java.util.List;
import java.util.Optional;

public record Workflow(String name, List<Rule> rules) {

    public String determineResult(MachinePart machinePart) {
        return rules.stream().map(rule -> rule.determineResult(machinePart))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst().orElseThrow(() -> new IllegalStateException("Can not find result"));
    }
}
