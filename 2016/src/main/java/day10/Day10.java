package day10;

import fileUtils.FileReader;

import java.util.*;

public class Day10 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input10.csv");
        var nodeMap = readBots(lists);
        var instructions = readInstructions(lists, nodeMap);

        // part a
        executeInstructions(instructions);

        // part b
        showOutput(nodeMap);
    }

    private static Map<Id, Node> readBots(List<String> lists) {
        Map<Id, Node> result = new HashMap<>();
        var inventories = lists.stream().filter(list -> list.contains(" goes to bot ")).toList();
        inventories.forEach(inventory -> {
            var valueAndId = inventory.split("value ")[1].split(" goes to bot ");
            int value = Integer.parseInt(valueAndId[0]);
            var id = new Id(NodeType.BOT, Integer.parseInt(valueAndId[1]));
            result.putIfAbsent(id, new Node(id));
            var node = result.get(id);
            node.values.add(value);
        });
        return result;
    }

    private static List<Instruction> readInstructions(List<String> lists, Map<Id, Node> nodeMap) {
        var lines = lists.stream().filter(list -> list.startsWith("bot ")).toList();
        return lines.stream().map(line -> {
            var botAndRest = line.split(" gives low to ");
            var bot = readNode(botAndRest[0], nodeMap);
            var lowToAndHighTo = botAndRest[1].split(" and high to ");
            var lowNode = readNode(lowToAndHighTo[0], nodeMap);
            var highNode = readNode(lowToAndHighTo[1], nodeMap);
            return new Instruction(bot, lowNode, highNode);
        }).toList();
    }

    private static Node readNode(String line, Map<Id, Node> nodeMap) {
        if (line.contains("bot")) {
            var id = new Id(NodeType.BOT, Integer.parseInt(line.split("bot ")[1]));
            nodeMap.putIfAbsent(id, new Node(id));
            return nodeMap.get(id);
        }
        else if (line.contains("output")) {
            var id = new Id(NodeType.OUTPUT, Integer.parseInt(line.split("output ")[1]));
            nodeMap.putIfAbsent(id, new Node(id));
            return nodeMap.get(id);
        }
        else {
            throw new IllegalArgumentException("Can not parse line");
        }
    }

    private static void executeInstructions(List<Instruction> instructions) {
        boolean somethingDone = true;
        while (somethingDone) {
            somethingDone = false;
            for (Instruction instruction : instructions) {
                var values = instruction.bot().values;
                if (values.size() == 2) {
                    somethingDone = true;
                    var lowValue = Math.min(values.get(0), values.get(1));
                    var highValue = Math.max(values.get(0), values.get(1));
                    instruction.bot().values.clear();
                    instruction.lowNode().values.add(lowValue);
                    instruction.highNode().values.add(highValue);
                    assert instruction.lowNode().values.size() <= 2;
                    assert instruction.highNode().values.size() <= 2;
                    if (lowValue == 17 && highValue == 61) {
                        System.out.printf("Bot %d is responsible for comparing 17 and 67 values", instruction.bot().id.id());
                    }
                }
            }
        }
    }

    private static void showOutput(Map<Id, Node> nodeMap) {
        var outputNodes =nodeMap.entrySet().stream().filter(entry -> entry.getKey().nodeType().equals(NodeType.OUTPUT)).map(Map.Entry::getValue).toList();
        outputNodes.forEach(node -> System.out.printf("Output node %d has values: %s%n", node.id.id(), node.values));

        var nodes012 = outputNodes.stream().filter(node -> List.of(0, 1, 2).contains(node.id.id())).toList();
        var multiplication = nodes012.stream().map(node -> node.values.get(0)).reduce(1, (a,b) -> (a * b));
        System.out.println("Multiplication of values 0 1 2 = " + multiplication);
    }

}