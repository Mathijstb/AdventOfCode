package day9;

import drawUtils.DrawGrid;
import drawUtils.Images;
import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Day9 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input9.csv");
        var instructions = readInstructions(lines);
        //executeInstructions(instructions, 1);
        executeInstructions(instructions, 9);
    }

    private static List<Instruction> readInstructions(List<String> lines) {
        return lines.stream().map(line -> {
            var directionAndAmount = line.split(" ");
            return new Instruction(Direction.of(directionAndAmount[0]), Integer.parseInt(directionAndAmount[1]));
        }).toList();
    }


    private static void executeInstructions(List<Instruction> instructions, int numberOfTails) {
        var start = new Point(0, 0);
        var grid = new InfiniteGrid<PointType>();
        grid.setValue(new Point(start), PointType.CROSS);

        //For drawing movie
        Map<PointType, Consumer<DrawGrid.DrawParameters>> paintMap = new HashMap<>();
        paintMap.put(PointType.CROSS, (dp) -> dp.getG2d().drawImage(Images.getImage("ball.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(PointType.START, (dp) -> dp.getG2d().drawImage(Images.getImage("arrowUp.png"), dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
        paintMap.put(PointType.HEAD, (dp) -> {
            dp.getG2d().setColor(Color.RED);
            dp.getG2d().fillOval(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
        });
        paintMap.put(PointType.TAIL, (dp) -> {
            dp.getG2d().setColor(Color.GREEN);
            dp.getG2d().fillRect(dp.getDrawPoint().x, dp.getDrawPoint().y, dp.getBlockSize(), dp.getBlockSize());
        });
        var drawGrid = new DrawGrid<>("Snake", PointType.class, grid.points, PointType.EMPTY, paintMap);

        try {
            Thread.sleep(20000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted");
        }

        var head = new Point(start);
        List<Point> tails = IntStream.range(0, numberOfTails).mapToObj(i -> new Point(start)).toList();
        for (Instruction instruction : instructions) {
            for (int i = 0; i < instruction.amount(); i++) {
                head = moveHead(head, instruction);
                tails = moveTails(head, tails, grid);
            }
            repaint(drawGrid, grid, head, tails, start);
        }
        long numberOfMarks = grid.getAllPoints().size();
        System.out.println("Number of points visited by tail: " + numberOfMarks);
        drawGrid(grid.copy(), head, tails, start);
    }

    private static void repaint(DrawGrid<PointType> drawGrid, InfiniteGrid<PointType> grid, Point head, List<Point> tails, Point start) {
        var pointMap = grid.copy().points;
        pointMap.put(head, PointType.HEAD);
        tails.forEach(tail -> pointMap.put(tail, PointType.TAIL));
        pointMap.put(start, PointType.START);
        drawGrid.setPointTypeMap(pointMap);
        drawGrid.repaint(10);
    }

    private static void drawGrid(InfiniteGrid<PointType> grid, Point head, List<Point> tails, Point start) {
        grid.setValue(start, PointType.START);
        tails.forEach(tail -> grid.setValue(tail, PointType.TAIL));
        grid.setValue(head, PointType.HEAD);
        grid.draw(value -> switch (value) {
                    case EMPTY -> " ";
                    case CROSS -> "#";
                    case HEAD -> "H";
                    case TAIL -> "T";
                    case START -> "s";
                }
                , " ");
    }

    private static Point moveHead(Point head, Instruction instruction) {
        return switch (instruction.direction()) {
            case UP -> new Point(head.x, head.y - 1);
            case DOWN -> new Point(head.x, head.y + 1);
            case LEFT -> new Point(head.x - 1, head.y);
            case RIGHT -> new Point(head.x + 1, head.y);
        };
    }

    private static List<Point> moveTails(Point head, List<Point> tails, InfiniteGrid<PointType> grid) {
        var newTails = new ArrayList<>(tails);
        var parent = head;
        for (int j = 0; j < tails.size(); j++) {
            var tail = tails.get(j);
            var newTail = moveTail(parent, tail);
            if (!newTail.equals(tail)) {
                newTails.set(j, newTail);
                if (j == tails.size() - 1)  {
                    grid.setValue(newTail, PointType.CROSS);
                }
            }
            parent = newTail;
        }
        return newTails;
    }

    private static Point moveTail(Point head, Point tail) {
        if (!isAdjacent(head, tail)) {
            var diffX = head.x - tail.x;
            var diffY = head.y - tail.y;
            int newX = tail.x;
            int newY = tail.y;
            if (diffX > 0) newX += 1;
            if (diffX < 0) newX -= 1;
            if (diffY > 0) newY += 1;
            if (diffY < 0) newY -=1;
            return new Point(newX, newY);
        }
        return tail;
    }

    private static boolean isAdjacent(Point head, Point tail) {
        var absDiffX = Math.abs(head.x - tail.x);
        var absDiffY = Math.abs(head.y - tail.y);
        return (absDiffX <= 1 && absDiffY <= 1);
    }
}
