package day17;

import algorithms.Dijkstra;
import com.google.common.primitives.Chars;
import fileUtils.FileReader;
import grids.FiniteGrid;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Day17 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input17.csv");
        var grid = parseGrid(lines);
        var goal = new Point(grid.getWidth() - 1, grid.getHeight() - 1);

        //Part 1
        System.out.println("Part 1: ");
        var pathResult1 = Dijkstra.calculateMinimalPath(
                new Node(new Point(0, 0), Direction.WEST, 0), //start node
                node -> node.point().equals(goal),                        //goal condition
                node -> Long.valueOf(grid.getValue(node.point()).heat),   //get cost function
                node -> getNeighbours(node, grid)                         //get neighbour function
        );
        printCost(grid, pathResult1);

        //Part 2
        System.out.println("Part 2: ");
        var pathResult2 = Dijkstra.calculateMinimalPath(
                new Node(new Point(0, 0), Direction.WEST, 0),
                node -> node.point().equals(goal) && node.steps() >= 4,
                node -> Long.valueOf(grid.getValue(node.point()).heat),
                node -> getNeighbours2(node, grid)
        );
        printCost(grid, pathResult2);
    }

    private static void printCost(FiniteGrid<PointState> grid, Dijkstra.PathResult<Node> pathResult) {
        var path = pathResult.path();
        var cost = pathResult.cost();
        path.forEach(n -> grid.getValue(n.point()).pathCharacter = Optional.of("."));
        System.out.println();
        grid.draw(PointState::getDrawCharacter);
        System.out.println();
        System.out.println("Cost: " + cost);
        System.out.println();
    }

    private static FiniteGrid<PointState> parseGrid(List<String> lines) {
        var result = new FiniteGrid<PointState>();
        lines.stream().map(line -> Chars.asList(line.toCharArray()))
                .forEach(line -> result.addRow(new ArrayList<>(line.stream().map(p ->
                        new PointState(Integer.parseInt(String.valueOf(p)))).toList())));
        return result;
    }

    private static List<Node> getNeighbours(Node current, FiniteGrid<PointState> grid) {
        var currentPoint = current.point();
        var neighbours = grid.getNeighbours(currentPoint, false);
        var north = new Point(currentPoint.x, currentPoint.y - 1);
        var west = new Point(currentPoint.x - 1, currentPoint.y);
        var south = new Point(currentPoint.x, currentPoint.y + 1);
        var east = new Point(currentPoint.x + 1, currentPoint.y);
        switch (current.source()) {
            case NORTH -> neighbours.remove(north);
            case WEST -> neighbours.remove(west);
            case SOUTH -> neighbours.remove(south);
            case EAST -> neighbours.remove(east);
        }
        if (current.steps() == 3) {
            switch (current.source()) {
                case NORTH -> neighbours.remove(south);
                case WEST -> neighbours.remove(east);
                case SOUTH -> neighbours.remove(north);
                case EAST -> neighbours.remove(west);
            }
        }
        return neighbours.stream().map(neighbour -> {
            if (neighbour.equals(north)) return new Node(neighbour, Direction.SOUTH, current.source().equals(Direction.SOUTH) ? current.steps() + 1 : 1);
            else if (neighbour.equals(west)) return new Node(neighbour, Direction.EAST, current.source().equals(Direction.EAST) ? current.steps() + 1 : 1);
            else if (neighbour.equals(south)) return new Node(neighbour, Direction.NORTH, current.source().equals(Direction.NORTH) ? current.steps() + 1 : 1);
            else if (neighbour.equals(east)) return new Node(neighbour, Direction.WEST, current.source().equals(Direction.WEST) ? current.steps() + 1 : 1);
            else throw new IllegalStateException("undefined neighbour");
        }).toList();
    }

    private static List<Node> getNeighbours2(Node current, FiniteGrid<PointState> grid) {
        var currentPoint = current.point();
        var neighbours = grid.getNeighbours(currentPoint, false);
        var north = new Point(currentPoint.x, currentPoint.y - 1);
        var west = new Point(currentPoint.x - 1, currentPoint.y);
        var south = new Point(currentPoint.x, currentPoint.y + 1);
        var east = new Point(currentPoint.x + 1, currentPoint.y);
        switch (current.source()) {
            case NORTH -> neighbours.remove(north);
            case WEST -> neighbours.remove(west);
            case SOUTH -> neighbours.remove(south);
            case EAST -> neighbours.remove(east);
        }
        if (current.steps() >= 10) {
            switch (current.source()) {
                case NORTH -> neighbours.remove(south);
                case WEST -> neighbours.remove(east);
                case SOUTH -> neighbours.remove(north);
                case EAST -> neighbours.remove(west);
            }
        }
        if (current.steps() < 4) {
            switch (current.source()) {
                case NORTH -> neighbours.removeAll(List.of(north, east, west));
                case WEST -> neighbours.removeAll(List.of(north, south, west));
                case SOUTH -> neighbours.removeAll(List.of(east, south, west));
                case EAST -> neighbours.removeAll(List.of(north, east, south));
            }
        }
        return neighbours.stream().map(neighbour -> {
            if (neighbour.equals(north)) return new Node(neighbour, Direction.SOUTH, current.source().equals(Direction.SOUTH) ? current.steps() + 1 : 1);
            else if (neighbour.equals(west)) return new Node(neighbour, Direction.EAST, current.source().equals(Direction.EAST) ? current.steps() + 1 : 1);
            else if (neighbour.equals(south)) return new Node(neighbour, Direction.NORTH, current.source().equals(Direction.NORTH) ? current.steps() + 1 : 1);
            else if (neighbour.equals(east)) return new Node(neighbour, Direction.WEST, current.source().equals(Direction.WEST) ? current.steps() + 1 : 1);
            else throw new IllegalStateException("undefined neighbour");
        }).toList();
    }

}
