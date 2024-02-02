package day19;

import java.util.Optional;

public record Rule(Optional<Condition> conditionOpt, String result) {

    public Optional<String> determineResult(MachinePart machinePart) {
        return conditionOpt
                .filter(value -> !value.determineResult(machinePart))
                .<Optional<String>>map(value -> Optional.empty())
                .orElseGet(() -> Optional.of(result));
    }

    public Optional<Ranges> determineSuccessResult(Ranges ranges) {
        if (conditionOpt.isEmpty()) {
            return Optional.of(new Ranges(ranges.rangeMap(), result));
        }
        else {
            return conditionOpt.get().determineSuccessRanges(ranges, result);
        }
    }

    public Optional<Ranges> determineFailResult(Ranges ranges) {
        if (conditionOpt.isEmpty()) {
            return Optional.empty();
        }
        else {
            return conditionOpt.get().determineFailRanges(ranges);
        }
    }
}
