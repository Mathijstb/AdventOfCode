package day5;

import lombok.Value;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Value
public class Line {

    Point start;
    Point end;

    public boolean isStraight() {
        return start.x == end.x || start.y == end.y;
    }

    public boolean isHorizontal() {
        return start.y == end.y;
    }

    public boolean isVertical() {
        return start.x == end.x;
    }

    public boolean isDiagonal() {
        return start.x != end.x && start.y != end.y;
    }

    public List<Point> getPoints() {
        List<Point> points = new ArrayList<>();
        int minX = Math.min(start.x, end.x);
        int maxX = Math.max(start.x, end.x);
        int minY = Math.min(start.y, end.y);
        int maxY = Math.max(start.y, end.y);

        if (isHorizontal()) {
            for (int i = minX; i <= maxX; i++) {
                points.add(new Point(i, minY));
            }
        }
        else if (isVertical()) {
            for (int j = minY; j <= maxY; j++) {
                points.add(new Point(minX, j));
            }
        }
        else {
            int dx = start.x < end.x ? 1 : -1;
            int dy = start.y < end.y ? 1 : -1;

            points.add(new Point(start));
            Point c = new Point(start);
            while (!c.equals(end)) {
                c = new Point(c);
                c.translate(dx, dy);
                points.add(c);
            }
        }

        return points;
    }
}
