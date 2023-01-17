package day24;

import java.util.Optional;

public record GridPoint(PointType pointType, Optional<Integer> goalIndex) {}
