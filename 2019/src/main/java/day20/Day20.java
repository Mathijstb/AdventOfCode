package day20;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fileUtils.FileReader;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Day20 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input20.csv");
        Map<Point, State> pointStateMap = Parser.parseLines(lines);
        teleports = pointStateMap.values().stream()
                .filter(s -> s.getType() == StateType.TELEPORT)
                .map(State::getTeleport).collect(Collectors.toSet());
        Point start = teleports.stream().filter(t -> t.label.equals("AA")).map(t -> t.gates.get(0)).findFirst().orElseThrow();
        Point finish = teleports.stream().filter(t -> t.label.equals("ZZ")).map(t -> t.gates.get(0)).findFirst().orElseThrow();
//        Point finish = pointStateMap.entrySet().stream().filter(entry -> entry.getValue().getTeleport().label.equals("ZZ")).map(Map.Entry::getKey).findFirst().orElseThrow();
//        List<Point> shortestRoute = findShortestRoute(pointStateMap, start, finish, true).orElseThrow();
//        GridPainter.draw(pointStateMap, shortestRoute, 100);
        findShortestPathRecursiveMaze(pointStateMap, start, finish);
        drawMultiMaze(pointStateMap);
    }

    private static void drawMultiMaze(Map<Point, State> pointStateMap) {
        List<GridPainter> painters = new ArrayList<>();
        int numberOfLevels = Collections.max(minLevels) + 1;
        for (int i = 0; i < numberOfLevels; i++) {
            GridPainter gridPainter = new GridPainter(new HashMap<>(pointStateMap));
            painters.add(gridPainter);
        }
        for (int i = 0; i < numberOfLevels; i++) {
            painters.get(i).draw(new ArrayList<>(), 0);
        }
        Scanner myScan = new Scanner( System.in );
        System.out.print( "Please enter something: " );
        myScan.nextLine();
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted");
        }

        for (int i = 0; i < minRoutes.size(); i++) {
            Route route = minRoutes.get(i);
            int level = minLevels.get(i);
            GridPainter painter = painters.get(level);
            painter.draw(route.getMoves(), 50);
        }

    }

    private static Map<Point, List<Route>> topLevelRoutesMap;
    private static Map<Point, List<Route>> otherLevelRoutesMap;

    public static void findShortestPathRecursiveMaze(Map<Point, State> pointStateMap, Point start, Point finish) {
        topLevelRoutesMap = findRoutesMap(pointStateMap, true);
        otherLevelRoutesMap = findRoutesMap(pointStateMap, false);

        List<Route> nextRoutes = findNextRoutes(start, 0);
        for (Route nextRoute: nextRoutes) {
            List<Route> newRoutes = Lists.newArrayList(nextRoute);
            int newLevel = 0;
            List<Integer> newLevels = Lists.newArrayList(newLevel);
            int newNumberOfMoves = nextRoute.getMoves().size() - 1;
            findShortestPathRecursiveMazeIteration(finish, newRoutes, newLevels, newNumberOfMoves);
            printRoutes();
        }
    }

    private static void printRoutes() {
        for (int i = 0; i < minRoutes.size(); i++) {
            Route route = minRoutes.get(i);
            int level = minLevels.get(i);
            System.out.printf("Level: %s, Route: %s %s -- %s %s%n", level, route.getFromGateType(), route.getFromTeleport().label, route.getToGateType(), route.getToTeleport().label);
        }
    }

    private static Set<Teleport> teleports;
    private static List<Route> minRoutes;
    private static List<Integer> minLevels;

    //7758
    public static void findShortestPathRecursiveMazeIteration(Point finish, List<Route> routes, List<Integer> levels, int numberOfMoves) {
        if (numberOfMoves > minNumberOfMoves) return;
        Route route = routes.get(routes.size() - 1);
        Point position = route.getToGate();
        int level = levels.get(levels.size() - 1);
        if (level > teleports.size() - 2) return;
        if (position.equals(finish)) {
            if (level == 0 && minNumberOfMoves > numberOfMoves) {
                minNumberOfMoves = numberOfMoves;
                minRoutes = routes;
                minLevels = levels;
                System.out.println("Found new minimum number of moves!");
                System.out.println("Min number of moves: " + minNumberOfMoves);
                System.out.println();
            }
            return;
        }

        //update position
        position = route.getToTeleport().getOtherGate(position);
        int newLevel = route.getToGateType().equals(GateType.INNER) ? level + 1 : level - 1;
        numberOfMoves += 1;

        List<Route> nextRoutes = findNextRoutes(position, newLevel);
        for (Route nextRoute: nextRoutes) {
            int routeIndex = routes.indexOf(nextRoute);
            if (routeIndex >= 0 && levels.get(routeIndex) == newLevel) continue;

            List<Route> newRoutes = new ArrayList<>(routes);
            newRoutes.add(nextRoute);

            List<Integer> newLevels = new ArrayList<>(levels);
            newLevels.add(newLevel);
            int newNumberOfMoves = numberOfMoves + nextRoute.getMoves().size() - 1;
            findShortestPathRecursiveMazeIteration(finish, newRoutes, newLevels, newNumberOfMoves);
        }
    }

    private static List<Route> findNextRoutes(Point position, int level) {
        if (level > 0) {
            return otherLevelRoutesMap.get(position);
        }
        else {
            return topLevelRoutesMap.get(position);
        }
    }

    private static Map<Point, List<Route>> findRoutesMap(Map<Point, State> pointStateMap, boolean isTopLevel) {
        Teleport start = teleports.stream().filter(t -> t.label.equals("AA")).findFirst().orElseThrow();
        Teleport finish = teleports.stream().filter(t -> t.label.equals("ZZ")).findFirst().orElseThrow();
        Set<Point> fromGates = teleports.stream().filter(t -> t != finish)
                .map(t -> t.gates).flatMap(List::stream).collect(Collectors.toSet());
        Set<Point> toGates = teleports.stream().filter(t -> t != start)
                .map(t -> t.gates).flatMap(List::stream).collect(Collectors.toSet());

        Map<Point, List<Route>> routesMap = new HashMap<>();
        for (Point gate: fromGates) {
            List<Route> routes = new ArrayList<>();
            routesMap.put(gate, routes);
            for (Point otherGate: toGates) {
                if (gate.equals(otherGate)) continue;
                Optional<List<Point>> minMoves = findShortestRoute(pointStateMap, gate, otherGate, false);
                minMoves.ifPresent(moves -> {
                    Teleport fromTeleport = teleports.stream().filter(t -> t.gates.contains(gate)).findFirst().orElseThrow();
                    Teleport toTeleport = teleports.stream().filter(t -> t.gates.contains(otherGate)).findFirst().orElseThrow();
                    if (isTopLevel) {
                        if (toTeleport == finish || (toTeleport.getGateType(otherGate).equals(GateType.INNER))) {
                            routes.add(new Route(gate, otherGate, fromTeleport, toTeleport, moves));
                        }
                    }
                    else {
                        if (fromTeleport != start && fromTeleport != finish && toTeleport!= start && toTeleport!= finish) {
                            routes.add(new Route(gate, otherGate, fromTeleport, toTeleport, moves));
                        }
                    }
                });
            }
        }

        return routesMap;
    }

    private static Optional<List<Point>> findShortestRoute(Map<Point, State> pointStateMap, Point start, Point finish, boolean allowTeleporting) {
        minNumberOfMoves = 99999999;
        minMoves = new ArrayList<>();
        findShortestRouteIteration(pointStateMap, Lists.newArrayList(start), finish, allowTeleporting);
        return (minNumberOfMoves < 99999999 ? Optional.of(minMoves) : Optional.empty());
    }

    private static int minNumberOfMoves;
    private static List<Point> minMoves;

    private static void findShortestRouteIteration(Map<Point, State> pointStateMap, List<Point> moves, Point finish, boolean allowTeleporting) {
        int numberOfMoves = moves.size() - 1;
        if (numberOfMoves > minNumberOfMoves) return;
        Point position = moves.get(numberOfMoves);
        if (position.equals(finish)) {
            if (minNumberOfMoves > numberOfMoves) {
                minNumberOfMoves = numberOfMoves;
                minMoves = moves;
                //System.out.println("Found min number of moves! Min number of moves: " + minNumberOfMoves);
            }
            return;
        }

        List<Point> neighbours = findNeighbours(pointStateMap, position, allowTeleporting);
        for (Point neighbour: neighbours) {
            if (moves.contains(neighbour)) continue;
            List<Point> newMoves = new ArrayList<>(moves);
            newMoves.add(neighbour);
            findShortestRouteIteration(pointStateMap, newMoves, finish, allowTeleporting);
        }
    }

    private static final Set<StateType> softStateTypes = Sets.newHashSet(StateType.OPEN, StateType.TELEPORT);

    private static List<Point> findNeighbours(Map<Point, State> pointStateMap, Point position, boolean allowTeleporting) {
        List<Point> possibleNeigbours = Lists.newArrayList(new Point(position.x - 1, position.y), new Point(position.x + 1, position.y),
                                                           new Point(position.x, position.y - 1), new Point(position.x, position.y + 1))
                .stream().filter(p -> pointStateMap.containsKey(p) && softStateTypes.contains(pointStateMap.get(p).getType())).collect(Collectors.toList());
        if (allowTeleporting && pointStateMap.get(position).getType() == StateType.TELEPORT) {
            possibleNeigbours.add(pointStateMap.get(position).getTeleport().getOtherGate(position));
        }
        return possibleNeigbours;
    }
}
