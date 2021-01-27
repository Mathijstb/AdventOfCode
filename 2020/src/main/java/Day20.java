import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day20 {

    private enum Side {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    private enum Order {
        NORMAL,
        FLIPPED
    }

    @Value
    private static class Connection {
        int tileId;
        Side side;
        Order order;
        List<Boolean> values;
    }

    @Data
    @AllArgsConstructor
    private static class Tile {
        int id;
        List<List<Boolean>> grid;

        public List<Boolean> getSide(Side side, Order order) {
            List<Boolean> result = getSideFromGrid(grid, side);

            if (order.equals(Order.FLIPPED)) {
                List<Boolean> flippedResult = new ArrayList<>(result);
                Collections.reverse(flippedResult);
                result = flippedResult;
            }
            return result;
        }

        public List<List<Boolean>> getGrid(Side side, Order order) {
            List<List<Boolean>> result;
            switch (side) {
                case NORTH: result = grid; break;
                case EAST: {
                    result = new ArrayList<>();
                    for (int i = 0; i < grid.size(); i++) {
                        result.add(new ArrayList<>());
                        for (int j = 0; j < grid.size(); j++) {
                            result.get(i).add(grid.get(j).get(grid.size() - i - 1));
                        }
                    }
                } break;
                case SOUTH: {
                    result = new ArrayList<>();
                    for (int i = 0; i < grid.size(); i++) {
                        result.add(new ArrayList<>());
                        for (int j = 0; j < grid.size(); j++) {
                            result.get(i).add(grid.get(grid.size() - i - 1).get(grid.size() - j - 1));
                        }
                    }
                } break;
                case WEST: {
                    result = new ArrayList<>();
                    for (int i = 0; i < grid.size(); i++) {
                        result.add(new ArrayList<>());
                        for (int j = 0; j < grid.size(); j++) {
                            result.get(i).add(grid.get(grid.size() - j - 1).get(i));
                        }
                    }

                } break;
                default: throw new IllegalStateException("invalid side");
            }
            if (order.equals(Order.FLIPPED)) {
                List<List<Boolean>> flippedResult = new ArrayList<>(result);
                Collections.reverse(flippedResult);
                result = flippedResult;
            }
            return result;
        }
    }

    private static List<Boolean> getSideFromGrid(List<List<Boolean>> grid, Side side) {
        switch (side) {
            case NORTH: return grid.get(0);
            case EAST: return grid.stream().map(row -> row.get(row.size() - 1)).collect(Collectors.toList());
            case SOUTH: return grid.get(grid.size() -1);
            case WEST: return grid.stream().map(row -> row.get(0)).collect(Collectors.toList());
            default: throw new IllegalStateException("invalid side");
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input20.csv");
        List<Tile> tiles = getTiles(lines);
        Map<Tile, List<Connection>> connectionsMap = getConnectionsMap(tiles);
        Map<Tile, List<Connection>> connectionsFitMap = getConnectionsThatFit(connectionsMap);
        //List<Tile> cornerTiles = findTilesWithOnly2Connections(connectionsFitMap);
        Tile[][] tileOrder = findTileOrder(connectionsFitMap);
        setOrientatedTileOrder(tileOrder);
        Tile giantTile = getGiantTile(tileOrder);
        List<String> seaMonsterlines = FileReader.getFileReader().readFile("input20b.csv");
        List<Point> seaMonsterCoordinates = getSeaMonsterPattern(seaMonsterlines);
        findSeaMonsters(giantTile, seaMonsterCoordinates);
    }

    private static List<List<List<Boolean>>> getGrids(Tile giantTile) {
        List<List<List<Boolean>>> grids = new ArrayList<>();
        for (int i = 0; i < Side.values().length; i++) {
            Side side = Side.values()[i];
            for (int j = 0; j < Order.values().length; j++) {
                Order order = Order.values()[j];
                grids.add(giantTile.getGrid(side, order));
            }
        }
        return grids;
    }

    private static void findSeaMonsters(Tile giantTile, List<Point> seaMonsterCoordinates) {
        int maxX = seaMonsterCoordinates.stream().max(Comparator.comparingInt(p -> p.x)).orElseThrow().x;
        int maxY = seaMonsterCoordinates.stream().max(Comparator.comparingInt(p -> p.y)).orElseThrow().y;

        List<List<List<Boolean>>> grids = getGrids(giantTile);
        grids.forEach(grid -> {
            int numberOfSeaMonsters = 0;
            List<Point> seaMonsterStartPoints = new ArrayList<>();
            for (int i = 0; i < grid.size() - maxY; i++) {
                List<Boolean> line = grid.get(i);
                for (int j = 0; j < line.size() - maxX; j++) {
                    if (hasSeaMonster(grid, new Point(j, i), seaMonsterCoordinates)) {
                        numberOfSeaMonsters += 1;
                        seaMonsterStartPoints.add(new Point(j,i));
                    }
                }
            }
            if (numberOfSeaMonsters > 0) {
                giantTile.setGrid(grid);
                printFinalSea(grid, seaMonsterStartPoints, seaMonsterCoordinates);

                int numberOfDebris = 0;
                for (List<Boolean> line : grid) {
                    for (Boolean point : line) {
                        if (point) {
                            numberOfDebris += 1;
                        }
                    }
                }
                int numberOfDebrisWithoutMonsters = numberOfDebris - numberOfSeaMonsters * 15;
                System.out.println("Found number of sea monsters: " + numberOfSeaMonsters);
                System.out.println("Number of Debris: " + numberOfDebris);
                System.out.println("Number of Debris without monster: " + numberOfDebrisWithoutMonsters);
            }
        });
    }

    private static void printFinalSea(List<List<Boolean>> grid, List<Point> seaMonsterStartPoints, List<Point> seaMonsterCoordinates) {
        List<List<String>> newGrid = new ArrayList<>();
        for (int i = 0; i < grid.size(); i++) {
            newGrid.add(new ArrayList<>());
            List<String> line = newGrid.get(i);
            for (int j = 0; j < grid.get(i).size(); j++) {
                line.add(grid.get(i).get(j) ? "#" : " ");
            }
        }
        for (Point seaMonsterStartPoint : seaMonsterStartPoints) {
            for (Point seaMonsterCoordinate : seaMonsterCoordinates) {
                newGrid.get(seaMonsterStartPoint.y + seaMonsterCoordinate.y).set(seaMonsterStartPoint.x + seaMonsterCoordinate.x, "O");
            }
        }
        for (List<String> strings : newGrid) {
            StringBuilder builder = new StringBuilder();
            for (String value : strings) {
                builder.append(value);
            }
            System.out.println(builder.toString());
        }
    }

    private static boolean hasSeaMonster(List<List<Boolean>> grid, Point coordinate, List<Point> seaMonsterCoordinates) {
        for (Point seaMonsterCoordinate : seaMonsterCoordinates) {
            int y = coordinate.y + seaMonsterCoordinate.y;
            int x = coordinate.x + seaMonsterCoordinate.x;
            if (!grid.get(y).get(x)) {
                return false;
            }
        }
        return true;
    }

    private static List<Point> getSeaMonsterPattern(List<String> seaMonsterlines) {
        List<Point> coordinates = new ArrayList<>();
        for (int i = 0; i < seaMonsterlines.size(); i++) {
            String line = seaMonsterlines.get(i);
            for (int j = 0; j < line.length(); j++) {
                if(line.charAt(j) == '#') {
                    coordinates.add(new Point(j,i));
                }
            }
        }
        return coordinates;
    }

    private static void setOrientatedTileOrder(Tile[][] tileOrder) {
        int height = tileOrder.length;
        int width = tileOrder[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Tile tile = tileOrder[i][j];
                List<List<Boolean>> grid;
                if (i == 0 && j == 0) {
                    grid = tile.getGrid(Side.WEST, Order.FLIPPED);
                }
                else {
                    if (j > 0) {
                        grid = rotateAndFlipGridToMatchWest(tileOrder[i][j - 1], tile);
                    } else {
                        grid = rotateAndFlipGridToMatchNorth(tileOrder[i - 1][j], tile);
                    }
                }
                tile.grid = grid;
            }
        }
    }

    private static Tile getGiantTile(Tile[][] tileOrder) {
        int height = tileOrder.length;
        int width = tileOrder[0].length;
        Tile giantTile = new Tile(0, new ArrayList<>());
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Tile tile = tileOrder[i][j];
                List<List<Boolean>> grid = tile.grid;
                resizeGrid(grid);
                int gridHeight = grid.size();
                int gridWidth = grid.get(0).size();
                List<List<Boolean>> giantGrid = giantTile.grid;
                for (int k = 0; k < gridHeight; k++) {
                    int row = i * gridHeight + k;
                    if (j==0) {
                        giantGrid.add(new ArrayList<>());
                    }
                    giantGrid.get(row).addAll(grid.get(k));
                }
            }
        }
        return giantTile;
    }

    private static List<List<Boolean>> rotateAndFlipGridToMatchWest(Tile tileWest, Tile tileEast) {
        List<List<List<Boolean>>> grids = getGrids(tileEast).stream()
                .filter(gridEast -> getSideFromGrid(gridEast, Side.WEST).equals(getSideFromGrid(tileWest.grid, Side.EAST))).collect(Collectors.toList());
        return grids.stream().findFirst().orElseThrow();
    }

    private static List<List<Boolean>> rotateAndFlipGridToMatchNorth(Tile tileNorth, Tile tileSouth) {
        List<List<List<Boolean>>> grids = getGrids(tileSouth).stream()
                .filter(gridSouth -> getSideFromGrid(gridSouth, Side.NORTH).equals(getSideFromGrid(tileNorth.grid, Side.SOUTH))).collect(Collectors.toList());
        return grids.stream().findFirst().orElseThrow();
    }

    private static void resizeGrid(List<List<Boolean>> grid) {
        for (List<Boolean> row : grid) {
            row.remove(0);
            row.remove(row.size() - 1);
        }
        grid.remove(0);
        grid.remove(grid.size() - 1);
    }

    private static Tile[][] findTileOrder(Map<Tile, List<Connection>> connectionsFitMap) {
        List<Tile> cornerTiles = connectionsFitMap.entrySet().stream().filter(entry -> entry.getValue().size() == 2).map(Map.Entry::getKey).collect(Collectors.toList());
        List<Tile> sideTiles = connectionsFitMap.entrySet().stream().filter(entry -> entry.getValue().size() == 3).map(Map.Entry::getKey).collect(Collectors.toList());
        List<Tile> otherTiles = connectionsFitMap.entrySet().stream().filter(entry -> entry.getValue().size() == 4).map(Map.Entry::getKey).collect(Collectors.toList());
        Set<Tile> remainingSideTiles = new HashSet<>(sideTiles);
        Set<Tile> remainingCornerTiles = new HashSet<>(cornerTiles);
        Set<Tile> remainingOtherTiles = new HashSet<>(otherTiles);

        //define tileOrder
        int length = sideTiles.size() / 4 + 2;
        Tile[][] tileOrder = new Tile[length][length];

        //define first corner tile
        Tile tile = cornerTiles.get(2);
        remainingCornerTiles.remove(tile);
        tileOrder[0][0] = tile;

        //first row
        for (int i = 0; i < length - 2; i++) {
            tile = getNextTile(tile, remainingSideTiles, connectionsFitMap);
            tileOrder[0][i+1] = tile;
        }

        //second corner tile
        tile = getNextTile(tile, remainingCornerTiles, connectionsFitMap);
        tileOrder[0][length - 1] = tile;

        //last column
        for (int i = 0; i < length - 2; i++) {
            tile = getNextTile(tile, remainingSideTiles, connectionsFitMap);
            tileOrder[i+1][length-1] = tile;
        }

        //third corner tile
        tile = getNextTile(tile, remainingCornerTiles, connectionsFitMap);
        tileOrder[length - 1][length - 1] = tile;

        //last row
        for (int i = 0; i < length - 2; i++) {
            tile = getNextTile(tile, remainingSideTiles, connectionsFitMap);
            tileOrder[length-1][length-1-i-1] = tile;
        }

        //fourth corner tile
        tile = getNextTile(tile, remainingCornerTiles, connectionsFitMap);
        tileOrder[length - 1][0] = tile;

        //first column
        for (int i = 0; i < length - 2; i++) {
            tile = getNextTile(tile, remainingSideTiles, connectionsFitMap);
            tileOrder[length-1-i-1][0] = tile;
        }

        //other tiles
        for (int i = 1; i < length - 1; i++) {
            for (int j = 1; j < length - 1; j++) {
                Tile tile1 = tileOrder[i][j-1];
                Tile tile2 = tileOrder[i-1][j];
                tile = getNextInnerTile(Arrays.asList(tile1, tile2), remainingOtherTiles, connectionsFitMap);
                tileOrder[i][j] = tile;
            }
        }

        return tileOrder;
    }

    private static Tile getNextTile(Tile tile, Set<Tile> remainingTiles, Map<Tile, List<Connection>> connectionsFitMap) {
        Tile result = remainingTiles.stream().filter(nextTile -> connectionsFitMap.get(tile).stream()
                .anyMatch(connection -> connection.tileId == nextTile.getId()))
                .findFirst().orElseThrow();
        remainingTiles.remove(result);
        return result;
    }

    private static Tile getNextInnerTile(List<Tile> tiles, Set<Tile> remainingTiles, Map<Tile, List<Connection>> connectionsFitMap) {
        Tile tile1 = tiles.get(0);
        Tile tile2 = tiles.get(1);
        List<Tile> firstMatch = remainingTiles.stream().filter(nextTile -> connectionsFitMap.get(tile1).stream()
                .anyMatch(connection -> connection.tileId == nextTile.getId())).collect(Collectors.toList());
        Tile result = firstMatch.stream().filter(nextTile ->
                connectionsFitMap.get(tile2).stream().anyMatch(connection -> connection.tileId == nextTile.getId())).findFirst().orElseThrow();
        remainingTiles.remove(result);
        return result;
    }

    private static Map<Tile, List<Connection>> filterNormalConnections(Map<Tile, List<Connection>> connectionsMap) {
        Map<Tile, List<Connection>> result = new HashMap<>();
        connectionsMap.forEach((tile, value) -> {
            List<Connection> connections = value.stream().filter(connection -> connection.order == Order.NORMAL).collect(Collectors.toList());
            result.put(tile, connections);
        });
        return result;
    }

    private static List<Tile> findTilesWithOnly2Connections(Map<Tile, List<Connection>> connectionsFitMap) {
        List<Tile> cornerTiles = connectionsFitMap.entrySet().stream().filter(entry -> entry.getValue().size() <= 2)
                .map(Map.Entry::getKey).collect(Collectors.toList());
        cornerTiles.forEach(cornerTile -> {
            int numberOfConnections = connectionsFitMap.get(cornerTile).size();
            System.out.printf("Tile %s has only %s connections%n", cornerTile.id, numberOfConnections);
        });
        long multiplication = cornerTiles.stream().mapToLong(tile -> tile.id).reduce(1, (a, b) -> a * b);
        System.out.println("Multiplication: " + multiplication);
        return cornerTiles;
    }

    private static Map<Tile, List<Connection>> getConnectionsThatFit(Map<Tile, List<Connection>> connectionsMap) {
        Map<Tile, List<Connection>> connectionsMapUnflipped = filterNormalConnections(connectionsMap);
        Map<Tile, List<Connection>> connectionsFitMap = new HashMap<>();
        connectionsMapUnflipped.forEach((tile, connections) -> {
            connectionsFitMap.put(tile, new ArrayList<>());
            connections.forEach(connection -> connectionsMap.entrySet().stream().filter(entry -> entry.getKey() != tile).forEach(entry -> {
                List<Connection> otherConnections = entry.getValue();
                otherConnections.forEach(otherConnection -> {
                    if (connection.values.equals(otherConnection.values)) {
                        connectionsFitMap.get(tile).add(otherConnection);
                    }
                });
            }));
        });
        return connectionsFitMap;
    }

    private static Map<Tile, List<Connection>> getConnectionsMap(List<Tile> tiles) {
        Map<Tile, List<Connection>> map = new HashMap<>();
        tiles.forEach(tile -> map.put(tile, getConnections(tile)));
        return map;
    }

    private static List<Connection> getConnections(Tile tile) {
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < Side.values().length; i++) {
            Side side = Side.values()[i];
            for (int j = 0; j < Order.values().length; j++) {
                Order order = Order.values()[j];
                Connection connection = new Connection(tile.id, side, order, tile.getSide(side, order));
                connections.add(connection);
            }
        }
        return connections;
    }

    private static List<Tile> getTiles(List<String> lines) {
        List<Tile> tiles = new ArrayList<>();
        Tile tile = null;
        for (String line : lines) {
            if (line.contains("Tile")) {
                int spaceIndex = line.indexOf(" ");
                int id = Integer.parseInt(line.substring(spaceIndex + 1, line.length() - 1));
                tile = new Tile(id, new ArrayList<>());
            } else if (line.isEmpty()){
                tiles.add(tile);
            } else {
                List<Boolean> values = line.chars().mapToObj(c -> c == '#').collect(Collectors.toList());
                assert tile != null;
                tile.grid.add(values);
            }
        }
        tiles.add(tile);
        return tiles;
    }
}
