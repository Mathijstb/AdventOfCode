package day22;

import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class Day22 {

    private static Point dataLocation;

    private static Point goalLocation;

    private static Point emptyLocation;

    private static Node data;

    private static Node goal;

    private static Node empty;

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input22.csv")
                .stream().filter(list -> list.startsWith("/dev/grid")).toList();
        var grid = readGrid(lists);
        goalLocation = new Point(0, 0);
        dataLocation = new Point(grid.getAllPoints().stream()
                .max(Comparator.comparing(Point::getX)).stream()
                .min(Comparator.comparing(Point::getY)).stream()
                .findFirst().orElseThrow());
        emptyLocation = new Point(grid.getAllPoints().stream().filter(p -> grid.getValue(p).getUsed() == 0).findFirst().orElseThrow());
        data = grid.getValue(dataLocation);
        goal = grid.getValue(goalLocation);
        empty = grid.getValue(emptyLocation);

        countViablePairs(grid);
        drawGrid(grid);
        moveStartDataToGoal(grid);
    }

    private static InfiniteGrid<Node> readGrid(List<String> lists) {
        InfiniteGrid<Node> grid = new InfiniteGrid<>();
        lists.forEach(list -> {
            var parts = list.split("[\\s]+");
            var coords = parts[0].split("/dev/grid/node-x")[1].split("-y");
            var point = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            var size = Integer.parseInt(parts[1].split("T")[0]);
            var used = Integer.parseInt(parts[2].split("T")[0]);
            grid.setValue(point, new Node(size, used));
        });
        return grid;
    }

    private static void countViablePairs(InfiniteGrid<Node> grid) {
        var numberOfViablePairs = grid.getAllPoints().stream()
                .filter(point -> grid.getValue(point).getUsed() != 0)
                .map(point -> {
                    var used = grid.getValue(point).getUsed();
                    return grid.getAllPoints().stream()
                            .filter(p -> !p.equals(point))
                            .filter(p -> grid.getValue(p).getAvailable() >= used)
                            .count();
                }).reduce(0L, Long::sum);
        System.out.println("Number of viable pairs: " + numberOfViablePairs);
    }

    private static void drawGrid(InfiniteGrid<Node> grid) {
        grid.draw(node -> {
            if (node.equals(data)) return "D";
            if (node.equals(goal)) return "G";
            if (node.equals(empty)) return "E";
            if (node.getSize() <= 100) return ".";
            return "#";
        }, " ");
        System.out.println();
    }

    private static void moveStartDataToGoal(InfiniteGrid<Node> grid) {
        int numberOfMoves = 0;
        for (int x = emptyLocation.x - 1; x >= 1 ; x--) {
            move(Direction.LEFT, grid);
            numberOfMoves += 1;
        }
        for (int y = emptyLocation.y - 1; y >= 0 ; y--) {
            move(Direction.UP, grid);
            numberOfMoves += 1;
        }
        for (int x = emptyLocation.x + 1; x <= dataLocation.x ; x++) {
            move(Direction.RIGHT, grid);
            numberOfMoves += 1;
        }
        while (!dataLocation.equals(goalLocation)) {
            move(Direction.DOWN, grid);
            move(Direction.LEFT, grid);
            move(Direction.LEFT, grid);
            move(Direction.UP, grid);
            move(Direction.RIGHT, grid);
            numberOfMoves += 5;
        }
        System.out.println("Number of moves: " + numberOfMoves);
    }

    private static void move(Direction direction, InfiniteGrid<Node> grid) {
        var sourceLocation = switch (direction) {
            case UP -> new Point(emptyLocation.x, emptyLocation.y - 1);
            case RIGHT -> new Point(emptyLocation.x + 1, emptyLocation.y);
            case DOWN -> new Point(emptyLocation.x, emptyLocation.y + 1);
            case LEFT -> new Point(emptyLocation.x - 1, emptyLocation.y);
        };
        var sourceNode = grid.getValue(sourceLocation);
        var dataUsed = sourceNode.removeUsed();

        if (sourceNode.equals(data)) {
            dataLocation = emptyLocation;
            data = grid.getValue(dataLocation);
        }

        empty.addUsed(dataUsed);
        emptyLocation = sourceLocation;
        empty = grid.getValue(emptyLocation);
        drawGrid(grid);
    }

}