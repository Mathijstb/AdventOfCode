package day16;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.Collectors;

public class Day16 {

    private static Map<String, Valve> valvesMap;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input16.csv");
        valvesMap = readValves(lines);
        releasePressure();
    }

    private static Map<String, Valve> readValves(List<String> lines) {
        var result = new HashMap<String, Valve>();
        int index = 0;
        for(String line : lines) {
            var valveAndRest = line.split(" has flow rate=");
            var name = valveAndRest[0].split("Valve ")[1];
            var rest = (valveAndRest[1].contains("valves")) ? valveAndRest[1].split("; tunnels lead to valves ")
                    : valveAndRest[1].split("; tunnel leads to valve ");
            var flowRate = Integer.parseInt(rest[0]);
            var valves = Arrays.stream(rest[1].split(", ")).toList();
            result.put(name, new Valve(index, name, flowRate, valves));
            index += 1;
        }
        return result;
    }

    private static void releasePressure() {
        List<Valve> valves = valvesMap.values().stream().toList();
        Set<Valve> openedValves = new HashSet<>();

        var valveA = valvesMap.get("AA");
        var weights = getWeights(valves);
        var valvesWithPositiveFlowRate = valves.stream().filter(v -> v.flowRate() > 0).toList();
        var paths1 = getPaths(valvesWithPositiveFlowRate, new Path(List.of(valveA)), weights, 30);

        //part a
        Map<Path, Integer> costs1 = determineCosts(paths1, weights, 30);
        var maxEntry = costs1.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).stream().findFirst().orElseThrow();
        System.out.println("Max pressure at AA: " + maxEntry.getValue());
        System.out.println("Max Path: " + maxEntry.getKey().valves().stream().map(Valve::name).toList());

        //part b
        var paths = getPaths(valvesWithPositiveFlowRate, new Path(List.of(valveA)), weights, 26);
        Map<Path, Integer> costs = determineCosts(paths1, weights, 26);

        var maxCost = 0;
        Path maxPath1 = null;
        Path maxPath2 = null;
        for (int i = 0; i < paths.size(); i++) {
            var path1 = paths.get(i);
            Set<Integer> valves1 = path1.valves().stream().map(Valve::index).collect(Collectors.toSet());
            valves1.remove(valveA.index());
            for (int j = i+1; j < paths.size(); j++) {
                var path2 = paths.get(j);
                Set<Integer> valves2 = path2.valves().stream().map(Valve::index).collect(Collectors.toSet());
                valves2.remove(valveA.index());
                if (Collections.disjoint(valves1, valves2)) {
                    var cost = costs.get(path1) + costs.get(path2);
                    if (cost > maxCost) {
                        maxCost = cost;
                        maxPath1 = path1;
                        maxPath2 = path2;
                    }
                }
            }
        }
        System.out.println("Max pressure with elephant: " + maxCost);
        assert maxPath1 != null;
        System.out.println("Path1: " + maxPath1.valves().stream().map(Valve::name).toList());
        System.out.println("Path2: " + maxPath2.valves().stream().map(Valve::name).toList());
    }

    private static int[][] getWeights(List<Valve> valves) {
        int[][] weights = new int[valves.size()][valves.size()];
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights.length; j++) {
                weights[i][j] = i == j ? 0 : 9999;
            }
        }
        valves.forEach(valve1 -> valve1.valves().forEach(valve2 -> weights[valve1.index()][valvesMap.get(valve2).index()] = 1));
        for (int k = 0; k < valves.size(); k++) {
            for (int i = 0; i < valves.size(); i++) {
                for (int j = 0; j < valves.size(); j++) {
                    if (weights[i][j] > weights[i][k] + weights[k][j]) {
                        weights[i][j] = weights[i][k] + weights[k][j];
                    }
                }
            }
        }
        return weights;
    }

    private static List<Path> getPaths(List<Valve> valves, Path path, int[][] weights, int timeLeft) {
        var current = path.valves().get(path.valves().size() - 1);
        var nextValves = valves.stream()
                .filter(valve -> (valve != current) && !path.valves().contains(valve) && weights[current.index()][valve.index()] + 1 <= timeLeft).toList();
        List<Path> result = new ArrayList<>();
        for (Valve valve : nextValves) {
            var newPath = new Path(new ArrayList<>(path.valves()));
            newPath.valves().add(valve);
            result.add(newPath);
            result.addAll(getPaths(valves, newPath, weights, timeLeft - (weights[current.index()][valve.index()] + 1)));
        }
        return result;
    }

    private static Map<Path, Integer> determineCosts(List<Path> paths, int[][] weights, int totalTime) {
        Map<Path, Integer> costs = new HashMap<>();
        paths.forEach(path ->  {
            int cost = 0;
            int timeLeft = totalTime;
            for (int i = 1; i < path.valves().size(); i++) {
                var valve = path.valves().get(i);
                var timeCost = weights[path.valves().get(i-1).index()][valve.index()] + 1;
                cost += valve.flowRate() * (timeLeft - timeCost);
                timeLeft -= timeCost;
            }
            costs.put(path, cost);
        });
        return costs;
    }

}