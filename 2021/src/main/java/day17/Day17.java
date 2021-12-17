package day17;

import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static grids.InfiniteGrid.DrawConfiguration.REVERSE_Y;

public class Day17 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input17.csv");
        TargetArea targetArea = readTargetArea(input);

        Point start = new Point(0, 0);
        List<Integer> validHorizontalVelocities = getValidHorizontalVelocities(start, targetArea);
        List<Point> validVelocities = new ArrayList<>();
        for (int horizontalVelocity: validHorizontalVelocities) {
            for (int verticalVelocity = targetArea.getMinY(); verticalVelocity < Math.abs(targetArea.getMinY()); verticalVelocity++) {
                Point velocity = new Point(horizontalVelocity, verticalVelocity);
                if (launchProbe(start, velocity, targetArea)) {
                    validVelocities.add(velocity);
                }
            }
        }
        System.out.println("Number of velocities: " + validVelocities.size());
    }

    private static List<Integer> getValidHorizontalVelocities(Point start, TargetArea targetArea){
        List<Integer> velocities = new ArrayList<>();
        for (int startVelocity = 1; startVelocity <= targetArea.getMaxX(); startVelocity++) {
            int x = start.x;
            int velocity = startVelocity;
            while (velocity > 0) {
                x += velocity;
                velocity -= 1;
                if (x >= targetArea.getMinX() && x <= targetArea.getMaxX()) {
                    velocities.add(startVelocity);
                    break;
                }
                else if (x > targetArea.getMaxX()) {
                    break;
                }
            }
        }
        return velocities;
    }

    private static TargetArea readTargetArea(List<String> input) {
        assert input.size() == 1;
        String[] ranges = input.get(0).split("target area: ")[1].split(", ");
        String[] rangeX = ranges[0].split("x=")[1].split("\\.\\.");
        String[] rangeY = ranges[1].split("y=")[1].split("\\.\\.");
        return new TargetArea(Integer.parseInt(rangeX[0]), Integer.parseInt(rangeX[1]), Integer.parseInt(rangeY[0]), Integer.parseInt(rangeY[1]));
    }

    private static int determineVelocityWithMaxHeight(List<Integer> verticalVelocities) {
        return verticalVelocities.stream().max(Comparator.comparingInt(v -> v * (v+1) / 2)).orElseThrow();
    }

    private static boolean launchProbe(Point start, Point velocity, TargetArea targetArea) {
        InfiniteGrid<PointType> grid = new InfiniteGrid<>();
        setupGridPositions(grid, start, targetArea);

        Point position = new Point(start);
        while (true) {
            executeStep(position, velocity);
            grid.setValue(new Point(position), PointType.STEP);
            if (targetHit(position, targetArea)) return true;
            if (targetMissed(position, targetArea)) return false;
        }
    }

    private static void setupGridPositions(InfiniteGrid<PointType> grid, Point start, TargetArea targetArea) {
        grid.setValue(new Point(start), PointType.START);
        for (int x = targetArea.getMinX(); x <= targetArea.getMaxX() ; x++) {
            for (int y = targetArea.getMinY(); y <= targetArea.getMaxY() ; y++) {
                grid.setValue(new Point(x, y), PointType.TARGET);
            }
        }
    }

    private static void drawGrid(InfiniteGrid<PointType> grid) {
        grid.setDrawConfiguration(Set.of(REVERSE_Y));
        grid.draw(pointType -> {
            switch (pointType) {
                case START: return "S";
                case STEP: return "#";
                case TARGET: return "T";
                default: throw new IllegalArgumentException("Invalid point type");
            }
        }, " ");
    }

    private static void executeStep(Point position, Point velocity) {
        position.translate(velocity.x, velocity.y);
        if (velocity.x > 0) {
            velocity.translate(-1, 0);
        }
        else if (velocity.x < 0) {
            velocity.translate(1, 0);
        }
        velocity.translate(0, -1);
    }

    private static boolean targetMissed(Point position, TargetArea targetArea) {
        return position.x > targetArea.getMaxX() || position.y < targetArea.getMinY();
    }

    private static boolean targetHit(Point position, TargetArea targetArea) {
        return position.x >= targetArea.getMinX() && position.x <= targetArea.getMaxX()
                && position.y >= targetArea.getMinY() && position.y <= targetArea.getMaxY();
    }
}