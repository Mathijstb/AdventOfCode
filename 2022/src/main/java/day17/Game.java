package day17;

import grids.InfiniteGrid;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Game {

    private final List<Shape> shapes;

    private int shapeIndex;

    private final JetStream jetStream;

    private int jetStreamIndex;

    private InfiniteGrid<PointType> grid;

    private int startX;

    private int minY;

    public Game(List<Shape> shapes, JetStream jetStream) {
        this.shapes = shapes;
        this.jetStream = jetStream;
    }

    public int getTowerHeight() {
        return Math.abs(minY);
    }

    public void playRounds(int numberOfRounds) {
        this.shapeIndex = 0;
        this.jetStreamIndex = 0;
        this.grid = setupGrid();
        this.startX = 3;
        this.minY = 0;

        Map<ShapeAndJetStreamIndex, RoundStats> map = new HashMap<>();
        for (int i = 0; i < numberOfRounds; i++) {
            // For finding repetitions
//            var shapeAndJetStreamIndex = new ShapeAndJetStreamIndex(shapeIndex, jetStreamIndex);
//            var currentHeight = getTowerHeight();
//            if (map.containsKey(shapeAndJetStreamIndex)) {
//                var stats = map.get(shapeAndJetStreamIndex);
//                System.out.println("Repetition!");
//                System.out.println("Previous round: " + stats.round());
//                System.out.println("Current round: " + i);
//                System.out.println("Difference: " + (i - stats.round()));
//
//                System.out.println("Current Height: " + currentHeight);
//                System.out.println("Previous height: " + stats.height());
//                System.out.println("Difference: " + (currentHeight - stats.height()));
//                //drawGrid(grid);
//                System.out.println();
//            }
//            map.put(shapeAndJetStreamIndex, new RoundStats(i, currentHeight));
            playRound();
        }
    }

//    private boolean topRowFilled() {
//        var topY = getTopRockY();
//        return IntStream.range(1, 9).allMatch(x -> grid.getValue(new Point(x, topY)).equals(PointType.ROCK));
//    }

    private void playRound() {
        var shape = getNextShape();
        var startX = this.startX;
        int bottomStartY = minY - 4;
        var startY = bottomStartY - shape.getHeight() + 1;
        var diffStartYGridY = Math.max(0, grid.getMinY() - startY);
        for (int i = 0; i < diffStartYGridY; i++) {
            addNewRow(grid);
        }

        var oldLocation = new Point(startX, startY);
        setLocation(shape, oldLocation);
        //drawGrid(grid);
        var blocked = false;
        while (!blocked) {
            var newLocation = pushByJet(oldLocation);
            if (canMove(shape, oldLocation, newLocation)) {
                moveShape(shape, oldLocation, newLocation);
                //drawGrid(grid);
                oldLocation = newLocation;
            }

            newLocation = new Point(oldLocation.x, oldLocation.y + 1);
            if (canMove(shape, oldLocation, newLocation)) {
                moveShape(shape, oldLocation, newLocation);
                //drawGrid(grid);
                oldLocation = newLocation;
            }
            else {
                blocked = true;
                if (oldLocation.y < minY) {
                    minY = oldLocation.y;
                }
                //drawGrid(grid);
            }
        }
    }

    private Point pushByJet(Point point) {
        var direction = getNextJetStreamDirection();
        return switch (direction) {
            case LEFT -> new Point(point.x - 1, point.y);
            case RIGHT -> new Point(point.x + 1, point.y);
        };
    }

    private boolean canMove(Shape shape, Point oldLocation, Point newLocation) {
        clearLocation(shape, oldLocation);
        boolean canMove = shape.getRockPoints().stream()
                .allMatch(point -> grid.getValue(new Point(newLocation.x + point.x, newLocation.y + point.y)).equals(PointType.AIR));
        setLocation(shape, oldLocation);
        return canMove;
    }

    private void moveShape(Shape shape, Point oldGridLocation, Point newGridLocation) {
        clearLocation(shape, oldGridLocation);
        setLocation(shape, newGridLocation);
    }

    private void clearLocation(Shape shape, Point location) {
        shape.getRockPoints().forEach(shapePoint -> {
            var gridPoint = new Point(location.x + shapePoint.x, location.y + shapePoint.y);
            grid.setValue(gridPoint, PointType.AIR);
        });
    }

    private void setLocation(Shape shape, Point location) {
        shape.getRockPoints().forEach(shapePoint -> {
            var gridPoint = new Point(location.x + shapePoint.x, location.y + shapePoint.y);
            grid.setValue(gridPoint, PointType.ROCK);
        });
    }

    private static InfiniteGrid<PointType> setupGrid() {
        InfiniteGrid<PointType> grid = new InfiniteGrid<>();
        for (int x = 0; x < 9; x++) {
            grid.setValue(new Point(x, 0), PointType.HORIZONTAL_WALL);
        }
        for (int i = 0; i < 10; i++) {
            addNewRow(grid);
        }
        return grid;
    }

    private static void addNewRow(InfiniteGrid<PointType> grid) {
        var minY = grid.getMinY();
        grid.setValue(new Point(0, minY - 1), PointType.VERTICAL_WALL);
        grid.setValue(new Point(8, minY - 1), PointType.VERTICAL_WALL);
        for (int x = 1; x < 8; x++) {
            grid.setValue(new Point(x, minY - 1), PointType.AIR);
        }
    }

    public Shape getNextShape() {
        var result = shapes.get(shapeIndex % shapes.size());
        shapeIndex = (shapeIndex + 1) % shapes.size();
        return result;
    }

    public Direction getNextJetStreamDirection() {
        var result = jetStream.directions().get(jetStreamIndex % jetStream.directions().size());
        jetStreamIndex = (jetStreamIndex + 1) % jetStream.directions().size();
        return result;
    }

    private static void drawGrid(InfiniteGrid<PointType> grid) {
        grid.draw(pointType -> switch (pointType) {
            case ROCK -> "#";
            case AIR -> ".";
            case VERTICAL_WALL -> "|";
            case HORIZONTAL_WALL -> "-";
        }, ".");
        System.out.println();
    }
}
