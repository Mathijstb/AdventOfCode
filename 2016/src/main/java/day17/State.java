package day17;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class State {

    private final Point currentPoint;

    private final List<Door> openedDoors;

    public State(Point currentPoint, List<Door> openedDoors) {
        this.currentPoint = currentPoint;
        this.openedDoors = openedDoors;
    }

    public State copy() {
        return new State(new Point(currentPoint), new ArrayList<>(openedDoors));
    }

    public Set<Door> getAvailableDoors() {
        Set<Door> result = new HashSet<>();
        if (currentPoint.x >= 1) result.add(Door.LEFT);
        if (currentPoint.x <= 2) result.add(Door.RIGHT);
        if (currentPoint.y >= 1) result.add(Door.UP);
        if (currentPoint.y <= 2) result.add(Door.DOWN);
        return result;
    }

    public void addDoor(Door door) {
        openedDoors.add(door);
        switch (door) {
            case LEFT -> currentPoint.translate(-1, 0);
            case RIGHT -> currentPoint.translate(1, 0);
            case UP -> currentPoint.translate(0, -1);
            case DOWN -> currentPoint.translate(0, 1);
        }
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public String getPath() {
        return openedDoors.stream().map(door -> door.shortName).collect(Collectors.joining());
    }
}
