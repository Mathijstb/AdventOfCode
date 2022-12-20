package day19;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.Collectors;

public class Day19 {

    private static List<BluePrint> bluePrints;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input19.csv");
        bluePrints = readBluePrints(lines);
        determineMaxNumberOfGeodes(24);
    }

    private static List<BluePrint> readBluePrints(List<String> lines) {
        return lines.stream().map(line -> {
            Map<RobotType, Map<ResourceType, Integer>> costs = new HashMap<>();
            Arrays.stream(RobotType.values()).forEach(robotType -> costs.put(robotType, new HashMap<>()));
            var bluePrintIdAndOre =line.split(": Each ore robot costs ");
            int id = Integer.parseInt(bluePrintIdAndOre[0].split("Blueprint ")[1]);
            var oreAndClay = bluePrintIdAndOre[1].split(" ore. Each clay robot costs ");
            var oreRobotOreCost = Integer.parseInt(oreAndClay[0]);
            var clayAndObsidian = oreAndClay[1].split(" ore. Each obsidian robot costs ");
            var clayRobotOreCost = Integer.parseInt(clayAndObsidian[0]);
            var obsidianAndGeode = clayAndObsidian[1].split(" clay. Each geode robot costs ");
            var obsidianRobotCosts = obsidianAndGeode[0].split(" ore and ");
            var obsidianRobotOreCost = Integer.parseInt(obsidianRobotCosts[0]);
            var obsidianRobotClayCost = Integer.parseInt(obsidianRobotCosts[1]);
            var geodeRobotCosts = obsidianAndGeode[1].split(" obsidian.")[0].split(" ore and ");
            var geodeRobotOreCost = Integer.parseInt(geodeRobotCosts[0]);
            var geodeRobotObsidianCost = Integer.parseInt(geodeRobotCosts[1]);

            costs.get(RobotType.ORE).put(ResourceType.ORE, oreRobotOreCost);
            costs.get(RobotType.CLAY).put(ResourceType.ORE, clayRobotOreCost);
            costs.get(RobotType.OBSIDIAN).put(ResourceType.ORE, obsidianRobotOreCost);
            costs.get(RobotType.OBSIDIAN).put(ResourceType.CLAY, obsidianRobotClayCost);
            costs.get(RobotType.GEODE).put(ResourceType.ORE, geodeRobotOreCost);
            costs.get(RobotType.GEODE).put(ResourceType.OBSIDIAN, geodeRobotObsidianCost);
            return new BluePrint(id, costs);
        }).toList();
    }

    private static int determineMaxNumberOfGeodes(int numberOfMinutes) {
        Map<Integer, Set<State>> StatesMap = new HashMap<>();
        var startState = new State(List.of(1 ,0, 0, 0), List.of(0 ,0, 0, 0));
        Set<State> previousStates = new HashSet<State>(Collections.singleton(startState));

        for(BluePrint bluePrint : bluePrints) {
            for (int i = 0; i < 24; i++) {
                Set<State> newStates = new HashSet<>();
                for(State state : previousStates) {
                    newStates.addAll(determineNextStates(state, (24 - i), bluePrint));
                }
                Set<State> dominatedStates = new HashSet<>();
                for(State state1 : newStates) {
                    for (State state2 : newStates) {
                        if (state1.isHigherState(state2)) {
                            dominatedStates.add(state2);
                        } else if (state2.isHigherState(state1)) {
                            dominatedStates.add(state1);
                            break;
                        }
                    }
                }

                newStates.removeAll(dominatedStates);
                previousStates = newStates;
            }
        }
        return 0;
    }

    private static Set<State> determineNextStates(State state, int timeLeft, BluePrint bluePrint) {
        if (timeLeft == 0) {
            return Set.of(state);
        }
        var amountOfOreRobots = state.getRobotAmount(RobotType.ORE);
        var amountOfClayRobots = state.getRobotAmount(RobotType.CLAY);
        var amountOfObsidianRobots = state.getRobotAmount(RobotType.OBSIDIAN);
        var amountOfGeodeRobots = state.getRobotAmount(RobotType.GEODE);
        Set<State> result = new HashSet<>();
        if (state.canBuildRobot(RobotType.GEODE, bluePrint)) {
            result.add(state.buildRobot(RobotType.GEODE, bluePrint));
        }
        else {
            Arrays.stream(RobotType.values()).forEach(robotType -> {
                if (state.canBuildRobot(robotType, bluePrint)) {
                    result.add(state.buildRobot(robotType, bluePrint));
                }
            });
        }
        if (result.isEmpty()) {
            result.add(state.copy());
        }
        return result.stream()
                .map(s -> s.addResources(List.of(amountOfOreRobots, amountOfClayRobots, amountOfObsidianRobots, amountOfGeodeRobots)))
                .collect(Collectors.toSet());
    }

}