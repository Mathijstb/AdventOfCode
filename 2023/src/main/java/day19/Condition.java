package day19;

import java.util.HashMap;
import java.util.Optional;

public record Condition(RangeType rangeType, Operator operator, int rightOperand) {

    public boolean determineResult(MachinePart machinePart) {
        var propertyValue = machinePart.getProperty(rangeType);
        return switch (operator) {
            case LESS -> propertyValue < rightOperand;
            case GREATER -> propertyValue > rightOperand;
        };
    }

    public Optional<Ranges> determineSuccessRanges(Ranges ranges, String target) {
        var range = ranges.rangeMap().get(rangeType);
        return getSuccesRange(range).map(succesRange -> {
            var map = new HashMap<>(ranges.rangeMap());
            map.put(rangeType, succesRange);
            return Optional.of(new Ranges(map, target));
        }).orElse(Optional.empty());
    }

    public Optional<Ranges> determineFailRanges(Ranges ranges) {
        var range = ranges.rangeMap().get(rangeType);
        return getFailRange(range).map(failRange -> {
            var map = new HashMap<>(ranges.rangeMap());
            map.put(rangeType, failRange);
            return Optional.of(new Ranges(map, ranges.target()));
        }).orElse(Optional.empty());
    }

    private Optional<Range> getSuccesRange(Range range) {
        return switch (operator) {
            case LESS -> range.from() < rightOperand ? Optional.of(new Range(range.from(), Math.min(range.to(), rightOperand - 1))) : Optional.empty();
            case GREATER -> range.to() > rightOperand ? Optional.of(new Range(Math.max(range.from(), rightOperand + 1), range.to())) : Optional.empty();
        };
    }

    private Optional<Range> getFailRange(Range range) {
        return switch (operator) {
            case LESS -> range.to() >= rightOperand ? Optional.of(new Range(Math.max(range.from(), rightOperand), range.to())) : Optional.empty();
            case GREATER -> range.from() <= rightOperand ? Optional.of(new Range(range.from(), Math.min(range.to(), rightOperand))) : Optional.empty();
        };
    }
}
