package day20;


import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Parser {

    public static Map<Point, State> parseLines(java.util.List<String> lines) {
        Map<Point, State> pointStateMap = new HashMap<>();
        Map<Point, Character> pointCharacterMap = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String row = lines.get(y);
            for (int x = 0; x < row.length(); x++) {
                Character character = row.charAt(x);
                if (Pattern.matches("[A-Z]", character.toString())) {
                    pointCharacterMap.put(new Point(x,y), character);
                }
                else {
                    pointStateMap.put(new Point(x, y), State.fromValue(character));
                }

            }
        }
        findTeleports(pointStateMap, pointCharacterMap);
        return pointStateMap;
    }

    private static void findTeleports(Map<Point, State> pointStateMap, Map<Point, Character> pointCharacterMap) {
        Map<String, Teleport> teleports = new HashMap<>();
        pointCharacterMap.forEach((point, character) -> {
            List<Point> teleportLabels = findNeighbourCharacter(pointCharacterMap, point);
            Point gate = findTeleportLocation(pointStateMap, teleportLabels);

            String label = String.valueOf(pointCharacterMap.get(teleportLabels.get(0))) + pointCharacterMap.get(teleportLabels.get(1));
            if (!teleports.containsKey(label)) {
                teleports.put(label, new Teleport(label));
            }
            Teleport teleport = teleports.get(label);
            if (!teleport.gates.contains(gate)) {
                teleport.gates.add(gate);
                Point labelPart1 = teleportLabels.get(0);
                Point labelPart2 = teleportLabels.get(1);
                if (labelPart1.x < gate.x) {
                    GateType gateType = //label.equals("AA") ?  GateType.START : label.equals("ZZ") ? GateType.FINISH :
                            pointStateMap.entrySet().stream().anyMatch(entry ->
                                    entry.getKey().x < labelPart1.x && entry.getValue().getType() == StateType.WALL) ? GateType.INNER : GateType.OUTER;
                    teleport.gateTypeMap.put(gate, gateType);
                    pointStateMap.put(labelPart2, new State(gateType == GateType.INNER ? StateType.TELEPORT_INNEREXT : StateType.TELEPORT_OUTEREXT, teleport));
                }
                if (gate.x < labelPart1.x) {
                    GateType gateType = //label.equals("AA") ?  GateType.START : label.equals("ZZ") ? GateType.FINISH :
                            pointStateMap.entrySet().stream().anyMatch(entry ->
                                    entry.getKey().x > labelPart1.x && entry.getValue().getType() == StateType.WALL) ? GateType.INNER : GateType.OUTER;
                    teleport.gateTypeMap.put(gate, gateType);
                    pointStateMap.put(labelPart1, new State(gateType == GateType.INNER ? StateType.TELEPORT_INNEREXT : StateType.TELEPORT_OUTEREXT, teleport));
                }
                if (labelPart1.y < gate.y) {
                    GateType gateType = //label.equals("AA") ?  GateType.START : label.equals("ZZ") ? GateType.FINISH :
                            pointStateMap.entrySet().stream().anyMatch(entry ->
                                    entry.getKey().y < labelPart1.y && entry.getValue().getType() == StateType.WALL) ? GateType.INNER : GateType.OUTER;
                    teleport.gateTypeMap.put(gate, gateType);
                    pointStateMap.put(labelPart2, new State(gateType == GateType.INNER ? StateType.TELEPORT_INNEREXT : StateType.TELEPORT_OUTEREXT, teleport));
                }
                if (gate.y < labelPart1.y) {
                    GateType gateType = //label.equals("AA") ?  GateType.START : label.equals("ZZ") ? GateType.FINISH :
                            pointStateMap.entrySet().stream().anyMatch(entry ->
                                    entry.getKey().y > labelPart1.y && entry.getValue().getType() == StateType.WALL) ? GateType.INNER : GateType.OUTER;
                    teleport.gateTypeMap.put(gate, gateType);
                    pointStateMap.put(labelPart1, new State(gateType == GateType.INNER ? StateType.TELEPORT_INNEREXT : StateType.TELEPORT_OUTEREXT, teleport));
                }
            }
        });
        teleports.forEach((label, teleport) -> {
//            if (label.equals("AA")) {
//                pointStateMap.put(teleport.gates.stream().findFirst().orElseThrow(), new State(StateType.START, null));
//            }
//            else if (label.equals("ZZ")) {
//                pointStateMap.put(teleport.gates.stream().findFirst().orElseThrow(), new State(StateType.FINISH, null));
//            }
//            else {
                teleport.gates.forEach(gate -> pointStateMap.put(gate, new State(StateType.TELEPORT, teleport)));
//            }
        });
    }

    private static Point findTeleportLocation(Map<Point, State> pointStateMap, List<Point> teleportPoints) {
        Point point1 = teleportPoints.get(0);
        Point point2 = teleportPoints.get(1);
        List<Point> possibleLocations = Lists.newArrayList(new Point(point1.x - 1, point1.y), new Point(point2.x + 1, point2.y),
                new Point(point1.x, point1.y - 1), new Point(point2.x, point2.y + 1));
        return possibleLocations.stream().filter(p -> pointStateMap.containsKey(p) && pointStateMap.get(p).getType() == StateType.OPEN).findFirst().orElseThrow();
    }

    private static List<Point> findNeighbourCharacter(Map<Point, Character> pointCharacterMap, Point point) {
        List<Point> neighbourPoints = Lists.newArrayList(new Point(point.x - 1, point.y), new Point(point.x + 1, point.y),
                                                         new Point(point.x, point.y - 1), new Point(point.x, point.y + 1));
        Point neighbour = neighbourPoints.stream().filter(pointCharacterMap::containsKey).findFirst().orElseThrow();
        List<Point> points = Lists.newArrayList(point, neighbour);
        points.sort(Comparator.comparing(p -> p.x));
        points.sort(Comparator.comparing(p -> p.y));
        return points;
    }

}
