package day19;

import java.util.Map;

public record Ranges(Map<RangeType, Range> rangeMap, String target) {
}
