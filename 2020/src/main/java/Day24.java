import fileUtils.FileReader;
import lombok.Value;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 {

    private enum Direction {
        NORTH_WEST("nw"),
        NORTH_EAST("ne"),
        EAST("e"),
        SOUTH_EAST("se"),
        SOUTH_WEST("sw"),
        WEST("w");

        private final String identifier;

        Direction(String identifier) {this.identifier = identifier;}
    }

    @Value
    private static class Coordinate {
        int x;
        int y;
        int z;
    }

    @Value
    private static class Path {
        List<Direction> directions;
    }

    private static final Set<Coordinate> blackTiles = new HashSet<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input24.csv");
        List<Path> paths = getPaths(lines);
        executePaths(paths);
        executeFlips();
        long numberOfBlackTiles = blackTiles.size();
        System.out.println("Number of black tiles: " + numberOfBlackTiles);
    }

    private static void executeFlips() {
        for (int i = 0; i < 100; i++) {
            List<Coordinate> blackTilesToBeFlipped = getBlackTilesWithZeroOrMoreThanTwoBlackNeighbours();
            List<Coordinate> whiteTilesToBeFlipped = getWhiteTilesWithExacltyTwoBlackNeighbours();
            blackTiles.addAll(whiteTilesToBeFlipped);
            blackTiles.removeAll(blackTilesToBeFlipped);
        }
    }

    private static void executePaths(List<Path> paths) {
        paths.forEach(path -> {
            Coordinate coordinate = new Coordinate(0, 0, 0);
            for (int i = 0; i < path.directions.size(); i++) {
                coordinate = walkDirection(coordinate, path.directions.get(i));
            }
            if (blackTiles.contains(coordinate)) {
                blackTiles.remove(coordinate);
            }
            else {
                blackTiles.add(coordinate);
            }
        });
    }

    private static List<Coordinate> getNeighbours(Coordinate coordinate) {
        return Arrays.stream(Direction.values()).map(direction -> walkDirection(coordinate, direction)).collect(Collectors.toList());
    }

    private static List<Coordinate> getWhiteNeighbours(Coordinate coordinate) {
        return getNeighbours(coordinate).stream().filter(c -> !blackTiles.contains(c)).collect(Collectors.toList());
    }

    private static List<Coordinate> getBlackNeighbours(Coordinate coordinate) {
        return getNeighbours(coordinate).stream().filter(blackTiles::contains).collect(Collectors.toList());
    }

    private static List<Coordinate> getBlackTilesWithZeroOrMoreThanTwoBlackNeighbours() {
        return blackTiles.stream().filter(b -> {
            long numberOfBlackNeighbours = getBlackNeighbours(b).size();
            return numberOfBlackNeighbours == 0 || numberOfBlackNeighbours > 2;
        }).collect(Collectors.toList());
    }

    private static List<Coordinate> getWhiteTilesWithExacltyTwoBlackNeighbours() {
        Set<Coordinate> whiteNeighbours = new HashSet<>();
        blackTiles.forEach(b -> whiteNeighbours.addAll(getWhiteNeighbours(b)));
        return whiteNeighbours.stream().filter(w -> getBlackNeighbours(w).size() == 2).collect(Collectors.toList());
    }


    private static Coordinate walkDirection(Coordinate coordinate, Direction direction) {
        int x = coordinate.x;
        int y = coordinate.y;
        int z = coordinate.z;
        switch (direction) {
            case NORTH_WEST: return new Coordinate(x, y + 1, z - 1);
            case NORTH_EAST: return new Coordinate(x + 1, y, z - 1);
            case EAST:       return new Coordinate(x + 1, y - 1, z);
            case SOUTH_EAST: return new Coordinate(x, y - 1, z + 1);
            case SOUTH_WEST: return new Coordinate(x - 1, y, z + 1);
            case WEST:       return new Coordinate(x - 1, y + 1, z);
            default: throw new IllegalStateException("invalid direction");
        }
    }

    private static List<Path> getPaths(List<String> lines) {
        Pattern pattern = Pattern.compile("(nw)|(ne)|(e)|(se)|(sw)|(w)");
        return lines.stream().map(line -> {
            List<Direction> directions = new ArrayList<>();
            Matcher m = pattern.matcher(line);
            int index = 0;
            while (m.find(index)) {
                String identifier = m.group(0);
                directions.add(Arrays.stream(Direction.values()).filter(d -> d.identifier.equals(identifier)).findFirst().orElseThrow());
                index += identifier.length();
            }
            return new Path(directions);
        }).collect(Collectors.toList());
    }
}
