package day15;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public record Sensor(int x, int y, Beacon beacon) {

    public int getDistanceToBeacon() {
        return Math.abs(x - beacon.x()) + Math.abs(y - beacon.y());
    }

    public boolean isInRange(Point point) {
        var distanceToBeacon = getDistanceToBeacon();
        var distanceToPoint = Math.abs(x - point.x) + Math.abs(y - point.y);
        return distanceToPoint <= distanceToBeacon;
    }

    public Set<Point> getPointsJustOutsideBoundary(int smallestX, int largestX, int smallestY, int largestY) {
        Set<Point> result = new HashSet<>();
        var distance = getDistanceToBeacon();
        var minX = x() - distance - 1;
        for (int x = Math.max(minX, smallestX); x <= x(); x++) {
            if (y() + (x - minX) <= largestY) result.add(new Point(x, y() + (x - minX)));
            if (y() - (x - minX) >- smallestY) result.add(new Point(x, y() - (x - minX)));
        }
        var maxX = x() + distance + 1;
        for (int x = x(); x <= Math.min(maxX, largestX); x++) {
            if (y() + (maxX - x) <= largestY) result.add(new Point(x, y() + (maxX - x)));
            if (y() - (maxX - x) >= smallestY) result.add(new Point(x, y() - (maxX - x)));
        }
        return result;
    }
}
