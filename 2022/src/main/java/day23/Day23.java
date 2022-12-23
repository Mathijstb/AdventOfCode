package day23;

import fileUtils.FileReader;
import grids.InfiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Day23 {

    private static final List<Decision> decisions = List.of(Decision.NORTH, Decision.SOUTH, Decision.WEST, Decision.EAST);

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input23.csv");
        var grid = readGrid(lines);

        //Part a
        playRounds(grid, 10);
        countEmptyGround(grid);

        //part b
        var grid2 = readGrid(lines);
        playRounds(grid, 1000);
    }

    private static InfiniteGrid<PointType> readGrid(List<String> lines) {
        var grid = new InfiniteGrid<PointType>();
        IntStream.range(0, lines.size()).forEach(y -> {
            var line = lines.get(y);
            IntStream.range(0, line.length()).forEach(x -> {
                if (lines.get(y).charAt(x) == '#') {
                    grid.setValue(new Point(x, y), PointType.ELF);
                }
            });
        });
        return grid;
    }

    private static void playRounds(InfiniteGrid<PointType> grid, int numberOfRounds) {
        int id = 1;
        List<Point> elfPositions = grid.getAllPoints().stream().filter(p -> grid.getValue(p).equals(PointType.ELF)).toList();
        List<Elf> elves = new ArrayList<>();
        for(Point position : elfPositions) {
            elves.add(new Elf(id, position));
            id += 1;
        }

        System.out.println("Start: ");
        //drawGrid(grid);
        for (int round = 0; round < numberOfRounds; round++) {
            System.out.println("Round: " + (round + 1));
            boolean elfMoved = playRound(grid, elves);
            //drawGrid(grid);
            if (!elfMoved) {
                System.out.println("No elf moved for the first time in round: " + (round + 1));
                break;
            }
        }
    }

    private static boolean playRound(InfiniteGrid<PointType> grid, List<Elf> elves) {
        determineProposals(grid, elves);
        return moveElves(grid, elves);
    }

    private static boolean moveElves(InfiniteGrid<PointType> grid, List<Elf> elves) {
        Map<Integer, Point> proposals = new HashMap<>();
        for(Elf elf : elves) {
            proposals.put(elf.id, elf.proposal);
        }
        var movingElves = elves.stream()
                .filter(elf -> !elf.proposal.equals(elf.position))
                .filter(elf -> proposals.entrySet().stream()
                        .noneMatch(entry ->
                                entry.getValue().equals(elf.proposal) &&
                                        !entry.getKey().equals(elf.id)))
                .toList();
        movingElves.forEach(elf -> {
                    grid.clearValue(elf.position);
                    grid.setValue(elf.proposal, PointType.ELF);
                    elf.position = elf.proposal;
                });
        return movingElves.size() > 0;
    }

    private static void determineProposals(InfiniteGrid<PointType> grid, List<Elf> elves) {
        elves.forEach(elf -> {
            var position = elf.position;
            var neighbours = grid.getNeighbours(position, true);
            if (neighbours.stream().noneMatch(grid::containsPoint)) {
                elf.proposal = elf.position;
            }
            else {
                var decisionIndex = elf.getDecisionIndex();
                for (int i = 0; i < 4; i++) {
                    int index = (decisionIndex + i) % 4;
                    var decision = decisions.get(index);
                    if (canMove(grid, elf, decision)) {
                        elf.proposal = getNextPosition(elf.position, decision);
                        break;
                    }
                    elf.proposal = position;
                }
            }
            elf.decisionIndex = (elf.decisionIndex + 1) % 4;
        });
    }

    private static boolean canMove(InfiniteGrid<PointType> grid, Elf elf, Decision decision) {
        var x = elf.position.x;
        var y = elf.position.y;
        var otherPositions = switch (decision) {
            case NORTH -> List.of(new Point(x - 1, y - 1), new Point(x, y - 1), new Point(x + 1, y - 1));
            case SOUTH -> List.of(new Point(x - 1, y + 1), new Point(x, y + 1), new Point(x + 1, y + 1));
            case WEST -> List.of(new Point(x - 1, y - 1), new Point(x - 1, y), new Point(x - 1, y + 1));
            case EAST -> List.of(new Point(x + 1, y - 1), new Point(x + 1, y), new Point(x + 1, y + 1));
        };
        return otherPositions.stream().noneMatch(grid::containsPoint);
    }

    private static Point getNextPosition(Point position, Decision decision) {
        return switch (decision) {
            case NORTH -> new Point(position.x, position.y - 1);
            case SOUTH -> new Point(position.x, position.y + 1);
            case WEST -> new Point(position.x - 1, position.y);
            case EAST -> new Point(position.x + 1, position.y);
        };
    }

    private static void drawGrid(InfiniteGrid<PointType> grid) {
        grid.draw(pointType -> switch (pointType) {
            case ELF -> "#";
            case EMPTY -> ".";
        }, ".");
        System.out.println();
    }

    private static void countEmptyGround(InfiniteGrid<PointType> grid) {
        var width = grid.getMaxX() - grid.getMinX() + 1;
        var height = grid.getMaxY() - grid.getMinY() + 1;
        var numberOfEmptyPoints = width * height - grid.getAllPoints().size();
        System.out.println("Number of empty ground tiles: " + numberOfEmptyPoints);
    }

}