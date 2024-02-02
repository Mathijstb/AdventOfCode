package day16;

import java.util.HashSet;
import java.util.Set;

public class PointState {

    public PointState(PointType pointType) {
        this.pointType = pointType;
    }

    public PointType pointType;

    public final Set<Direction> directions = new HashSet<>();

    public String getDrawCharacter() {
        if (!pointType.equals(PointType.EMPTY)) {
            return String.valueOf(pointType.character);
        }
        else {
            return switch (directions.size()) {
                case 0 -> String.valueOf(pointType.character);
                case 1 -> switch (directions.stream().findFirst().orElseThrow()) {
                    case SOUTH -> "v";
                    case EAST -> ">";
                    case WEST -> "<";
                    case NORTH -> "^";
                };
                default -> String.valueOf(directions.size());
            };
        }
    }

    public String getEnlightenedCharacter() {
        return !directions.isEmpty() ? "#" : String.valueOf(pointType.character);
    }

    public boolean isEnlightened() {
        return !directions.isEmpty();
    }
}
