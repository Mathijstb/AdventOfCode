package day8;

import fileUtils.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static algorithms.LCM.findLeastCommonMultiple;

public class Day8 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input8.csv");
        var directions = parseDirections(lines);
        var nodeMap = parseNodeMap(lines);
        int numberOfSteps = findNumberOfStepsFromAAAtoZZZ(nodeMap, directions);
        System.out.println("Number of steps: " + numberOfSteps);

        findNumberOfSimultaneousSteps(nodeMap, directions);
    }

    private static List<Direction> parseDirections(List<String> lines) {
        var directions = FileReader.splitLines(lines, String::isEmpty).get(0);
        return Arrays.stream(directions.get(0).split("")).map(Direction::of).toList();
    }

    private static Map<String, Node> parseNodeMap(List<String> lines) {
        var nodes = FileReader.splitLines(lines, String::isEmpty).get(1);
        return nodes.stream().map(node -> {
            var nameAndRest = node.split(" = \\(");
            var name = nameAndRest[0];
            var leftAndRight = nameAndRest[1].split(", ");
            var left = leftAndRight[0];
            var right = leftAndRight[1].split("\\)")[0];
            return new Node(name, left, right);
        }).collect(Collectors.toMap(Node::name, node -> node));
    }

    private static int findNumberOfStepsFromAAAtoZZZ(Map<String, Node> nodeMap, List<Direction> directions) {
        var source = nodeMap.get("AAA");
        var sink = nodeMap.get("ZZZ");
        var current = source;
        var directionIndex = 0;
        var numberOfSteps = 0;
        while (current != sink) {
            var direction = directions.get(directionIndex);
            current = direction.equals(Direction.L) ? nodeMap.get(current.left()) : nodeMap.get(current.right());
            directionIndex = (directionIndex + 1) % directions.size();
            numberOfSteps += 1;
        }
        return numberOfSteps;
    }

    private static void findNumberOfSimultaneousSteps(Map<String, Node> nodeMap, List<Direction> directions) {
        Map<Node, Long> cycleMap = findCycleMap(nodeMap, directions);
        var lcm = findLeastCommonMultiple(cycleMap.values().stream().toList());
        System.out.println("Least common multiple of cylces: " + lcm);
    }

    private static Map<Node, Long> findCycleMap(Map<String, Node> nodeMap, List<Direction> directions) {
        var sourceNodes = nodeMap.values().stream().filter(node -> node.name().endsWith("A")).toList();
        var sinkNodes = nodeMap.values().stream().filter(node -> node.name().endsWith("Z")).collect(Collectors.toSet());
        Map<Node, Long> nodeToCycleMap = new HashMap<>();
        sourceNodes.forEach(source -> {
            System.out.println("Source: " + source.name());
            Map<Node, List<Long>> sinkToStepsMap = new HashMap<>();
            var current = source;
            var directionIndex = 0;
            var numberOfSteps = 0L;
            while (true) {
                var direction = directions.get(directionIndex);
                current = direction.equals(Direction.L) ? nodeMap.get(current.left()) : nodeMap.get(current.right());
                numberOfSteps += 1;
                directionIndex = (directionIndex + 1) % directions.size();
                if (sinkNodes.contains(current)) {
                    if (!sinkToStepsMap.containsKey(current)) sinkToStepsMap.put(current, new ArrayList<>());
                    var stepsList = sinkToStepsMap.get(current);
                    var lastNumberOfSteps = stepsList.isEmpty() ? 0 : stepsList.get(stepsList.size() - 1);
                    var diffNumberOfSteps = numberOfSteps - lastNumberOfSteps;
                    stepsList.add(numberOfSteps);
                    System.out.printf("Sink: %s, number of steps: %d, diff: %d", current.name(), numberOfSteps, diffNumberOfSteps);
                    System.out.println();
                    if (diffNumberOfSteps % directions.size() == 0) {
                        System.out.println();
                        nodeToCycleMap.put(source, diffNumberOfSteps);
                        break;
                    }
                }
            }
        });
        return nodeToCycleMap;
    }
}
