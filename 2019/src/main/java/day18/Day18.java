package day18;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import drawUtils.DrawGrid;
import drawUtils.Images;
import fileUtils.FileReader;
import lombok.Value;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day18 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input18.csv");
        Map<Point, State> pointTypeMap = initializePointTypeMap(lines);
        Point position = findPosition(pointTypeMap);
        startPosition = position;
        initializeRoutes(pointTypeMap);
        findShortestPathCollectingAllKeys();

        List<Point> minMoves = minRoutes.stream().map(Route::getMoves).flatMap(List::stream).collect(Collectors.toList());
        System.out.println();
        System.out.println("Minimum number of moves: " + minNumberOfMoves);
        pointTypeMap.put(position, new State(StateType.EMPTY, null));
        try {
            showVideo(pointTypeMap, minMoves);
        }
        catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted");
        }
    }

    private static void showVideo(Map<Point, State> pointTypeMap, List<Point> minMoves) throws InterruptedException {
        pointTypeMap.put(startPosition, new State(StateType.POSITION, null));
        Point position = startPosition;
        DrawGrid<State> drawGrid = drawGrid(pointTypeMap);
        synchronized (Thread.currentThread()) {
            Thread.currentThread().wait(10000);
        }

        for (Point move: minMoves) {
            synchronized (Thread.currentThread()) {
                Thread.currentThread().wait(20);
            }
            if (pointTypeMap.get(move).getType() == StateType.KEY) {
                openDoor(pointTypeMap, pointTypeMap.get(move).getCharacter());
            }
            pointTypeMap.put(position, new State(StateType.VISITED, null));
            pointTypeMap.put(move, new State(StateType.POSITION, null));
            position = move;
            drawGrid.repaint();
        }
    }

    private static Point findPosition(Map<Point, State> pointTypeMap) {
        return pointTypeMap.entrySet().stream().filter(entry -> entry.getValue().getType() == StateType.POSITION).map(Map.Entry::getKey).findFirst().orElseThrow();
    }

    private static final Set<StateType> solidStateTypes = Sets.newHashSet(StateType.WALL);
    private static long numberOfKeys;

    private static List<Point> getNeighbours(Map<Point, State> pointTypeMap, List<Point> visitedPoints, Point point) {
        List<Point> points = Lists.newArrayList(new Point(point.x - 1, point.y), new Point(point.x + 1, point.y),
                                                          new Point(point.x, point.y - 1), new Point(point.x, point.y + 1));
        return points.stream().filter(p -> !visitedPoints.contains(p) && pointTypeMap.containsKey(p)
                && !solidStateTypes.contains(pointTypeMap.get(p).getType())).collect(Collectors.toList());
    }

    private static Point startPosition;
    private static int minNumberOfMoves = 999999;
    private static List<Route> minRoutes;

    private static void findShortestPathCollectingAllKeys() {
        getShortestPath(new HashSet<>(), new ArrayList<>(), '@', 0);
    }


    private static Map<Character, Map<Set<Character>, Integer>> minMovesMap = new HashMap<>();
    //5644 too high
    private static void getShortestPath(Set<Character> collectedKeys, List<Route> routes, Character fromKey, int numberOfMoves) {
        if (!minMovesMap.containsKey(fromKey)) {
            minMovesMap.put(fromKey, new HashMap<>());
        }
        Map<Set<Character>, Integer> keysToMinMovesMap = minMovesMap.get(fromKey);
        if (!keysToMinMovesMap.containsKey(collectedKeys)) {
            keysToMinMovesMap.put(collectedKeys, numberOfMoves);
        }
        else {
            int minNumberOfMoves = keysToMinMovesMap.get(collectedKeys);
            if (numberOfMoves >= minNumberOfMoves) {
                return;
            }
            else {
                keysToMinMovesMap.put(collectedKeys, numberOfMoves);
            }
        }


        if (numberOfMoves >= minNumberOfMoves) return;

        if (collectedKeys.size() == numberOfKeys) {
            System.out.println("Found improvement on number of moves: " + numberOfMoves);
            minNumberOfMoves = numberOfMoves;
            minRoutes = routes;
        }
        else {
            List<Route> possibleRoutes = shortestRoutes.get(fromKey).stream()
                    .filter(route -> !collectedKeys.contains(route.toKey) && collectedKeys.containsAll(route.requiredKeys))
                    .collect(Collectors.toList());
            possibleRoutes.forEach(newRoute -> {
                List<Route> newRoutes = new ArrayList<>(routes);
                newRoutes.add(newRoute);
                Set<Character> newCollectedKeys = new HashSet<>(collectedKeys);
                newCollectedKeys.add(newRoute.toKey);
                getShortestPath(newCollectedKeys, newRoutes, newRoute.toKey, numberOfMoves + newRoute.moves.size());
            });
        }
    }


    @Value
    private static class Route {
        Point startPosition;
        List<Point> moves;
        Set<Character> requiredKeys;
        Character fromKey;
        Character toKey;
    }

    private static final Map<Character, Set<Route>> shortestRoutes = new HashMap<>();

    private static void initializeRoutes(Map<Point, State> pointTypeMap) {
        List<Point> keyPoints = pointTypeMap.entrySet().stream().filter(entry -> entry.getValue().getType() == StateType.KEY)
                                                           .map(Map.Entry::getKey).collect(Collectors.toList());
        keyPoints.add(startPosition);
        keyPoints.forEach(keyPoint -> findReachableKeys(pointTypeMap, Lists.newArrayList(keyPoint), new HashSet<>()));
    }

    private static void findReachableKeys(Map<Point, State> pointTypeMap, List<Point> visitedPoints, Set<Character> requiredKeys) {
        Point position = visitedPoints.get(visitedPoints.size() - 1);

        List<Point> neighbours = getNeighbours(pointTypeMap, visitedPoints, position);
        for (Point neighbour: neighbours) {
            Point newPosition = new Point(neighbour);
            List<Point> newVisitedPoints = new ArrayList<>(visitedPoints);
            newVisitedPoints.add(newPosition);
            Set<Character> newRequiredKeys = new HashSet<>(requiredKeys);

            StateType stateType = pointTypeMap.get(newPosition).getType();
            if (stateType == StateType.KEY) {
                Point startPosition = newVisitedPoints.get(0);
                Character fromKey = pointTypeMap.get(startPosition).getCharacter();
                Character toKey = pointTypeMap.get(newPosition).getCharacter();
                if (!shortestRoutes.containsKey(fromKey)) {
                    shortestRoutes.put(fromKey, new HashSet<>());
                }
                Set<Route> routesFromKey = shortestRoutes.get(fromKey);
                Optional<Route> routeToKey = routesFromKey.stream().filter(r -> r.toKey == toKey).findFirst();
                if (routeToKey.isEmpty()) {
                    routesFromKey.add(new Route(startPosition, newVisitedPoints.subList(1, newVisitedPoints.size()), newRequiredKeys, fromKey, toKey));
                }
                else if (routeToKey.get().getMoves().size() > newVisitedPoints.size()) {
                        routesFromKey.remove(routeToKey.get());
                        routesFromKey.add(new Route(startPosition, newVisitedPoints.subList(1, newVisitedPoints.size()), newRequiredKeys, fromKey, toKey));
                }
            }
            else if (stateType == StateType.DOOR) {
                newRequiredKeys.add(Character.toLowerCase(pointTypeMap.get(newPosition).getCharacter()));
            }

            findReachableKeys(pointTypeMap, newVisitedPoints, newRequiredKeys);
        };
    }

    private static void openDoor(Map<Point, State> pointTypeMap, Character key) {
        pointTypeMap.entrySet().stream().filter(entry -> entry.getValue().getType() == StateType.DOOR &&
                entry.getValue().getCharacter() == Character.toUpperCase(key))
                .map(Map.Entry::getKey).findFirst()
                .ifPresent(door -> pointTypeMap.put(door, new State(StateType.EMPTY, null)));
    }

    private static void moveToNeighbour(Map<Point, State> pointTypeMap, Point position, Point neighbour) {
        State state = pointTypeMap.get(neighbour);
        pointTypeMap.put(position, new State(StateType.EMPTY, null));
        pointTypeMap.put(neighbour, new State(StateType.POSITION, null));
    }

    private static Map<Point, State> initializePointTypeMap(List<String> lines) {
        List<List<State>> grid = lines.stream().map(line -> line.chars().mapToObj(i -> (char)i).map(State::fromValue).collect(Collectors.toList())).collect(Collectors.toList());
        Map<Point, State> pointTypeMap = new HashMap<>();
        for (int y = 0; y < grid.size(); y++) {
            List<State> row = grid.get(y);
            for (int x = 0; x < row.size(); x++) {
                pointTypeMap.put(new Point(x, y), row.get(x));
            }
        }
        numberOfKeys = pointTypeMap.values().stream().filter(s -> s.getType() == StateType.KEY).count();
        return pointTypeMap;
    }

    private static  Map<State, Consumer<DrawGrid.DrawParameters>> paintMap;

    private static DrawGrid<State> drawGrid(Map<Point, State> pointTypeMap) {
        if (paintMap == null) {
            paintMap = new HashMap<>();
            //paintMap.put(new State(StateType.EMPTY, null), (dp) -> dp.getG2d().drawImage(Images.getImage("dot.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(new State(StateType.WALL, null), (dp) -> dp.getG2d().drawImage(Images.getImage("wall.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null));
            paintMap.put(new State(StateType.VISITED, null), (dp) -> {
                dp.getG2d().setColor(Color.GREEN);
                dp.getG2d().fillRect(dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize());
            });
            paintMap.put(new State(StateType.POSITION, null), (dp) -> {
                dp.getG2d().setColor(Color.GREEN);
                dp.getG2d().fillRect(dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize());
                dp.getG2d().drawImage(Images.getImage("ball.png"), dp.getPoint().x + dp.getBlockSize() / 4, dp.getPoint().y + dp.getBlockSize() / 4, dp.getBlockSize() / 2, dp.getBlockSize() / 2, null);
            });
            IntStream.rangeClosed('A', 'Z').forEach(i -> drawCharacter((char) i, Color.RED));
            IntStream.rangeClosed('a', 'z').forEach(i -> drawCharacter((char) i, Color.BLUE));
        }
        return new DrawGrid<>("Maze", State.class, pointTypeMap, new State(StateType.EMPTY, null), paintMap);
    }

    private static void drawCharacter(Character c, Color color) {
        StateType stateType = Character.isUpperCase(c) ? StateType.DOOR : StateType.KEY;
        paintMap.put(new State(stateType, c), (dp) -> {
            Font font = new Font("Serif", Font.PLAIN, dp.getBlockSize());
            dp.getG2d().setColor(color);
            dp.getG2d().setFont(font);
            //dp.getG2d().drawImage(Images.getImage(stateType == StateType.DOOR ? "door.png" : "key.png"), dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize(), null);
            dp.getG2d().drawString(c.toString(), dp.getPoint().x + dp.getBlockSize() / 2 - font.getSize() / 3, dp.getPoint().y + dp.getBlockSize() / 3 + font.getSize() / 2);
            //dp.getG2d().drawRect(dp.getPoint().x, dp.getPoint().y, dp.getBlockSize(), dp.getBlockSize());
        });
    }


}
