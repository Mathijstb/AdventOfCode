package day12;

import fileUtils.FileReader;
import networks.Network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day12 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input12.csv");
        Network<Cave> network = setupCaveNetwork(input);
        printNumberOfDistinctPaths(network);
    }

    private static Network<Cave> setupCaveNetwork(List<String> input) {
        Network<Cave> network = new Network<>();
        input.forEach(line -> {
            String[] caves = line.split("-");
            String caveName1 = caves[0];
            String caveName2 = caves[1];

            if (!network.containsNode(caveName1)) {
                network.addNode(new Cave(caveName1, caveName1.toLowerCase().equals(caveName1)));
            }
            if (!network.containsNode(caveName2)) {
                network.addNode(new Cave(caveName2, caveName2.toLowerCase().equals(caveName2)));
            }
            network.addConnection(caveName1, network.getNode(caveName2));
            network.addConnection(caveName2, network.getNode(caveName1));

        });
        return network;
    }

    private static void printNumberOfDistinctPaths(Network<Cave> network) {
        Cave start = network.getNode("start");
        List<List<Cave>> result = new ArrayList<>();
        getPaths(network, start, List.of(start), Set.of(start), false, result);
        System.out.println("Number of paths: " + result.size());
    }

    private static void getPaths(Network<Cave> network, Cave cave, List<Cave> path, Set<Cave> visitedCaves, boolean wildCardUsed, List<List<Cave>> resultingPaths) {
        // If we are at 'end' add the resulting path
        if (cave.equals(network.getNode("end"))) {
            resultingPaths.add(path);
            return;
        }

        // Get neighbours
        Set<Cave> neighbours = network.getConnections(cave.getName());
        neighbours.stream().map(neighbour -> {

            //If next cave is small and already finished, we can only continue if we have still a wildcard
            boolean newWildCardUsed = wildCardUsed;
            if (neighbour.isSmall() && visitedCaves.contains(neighbour)) {
                if (!neighbour.equals(network.getNode("start")) && !wildCardUsed) {
                    newWildCardUsed = true;
                }
                else {
                    return new ArrayList<List<Cave>>();
                }
            }

            //Define new variables
            List<Cave> newPath = new ArrayList<>(path);
            newPath.add(neighbour);
            Set<Cave> newVisitedCaves = new HashSet<>(visitedCaves);
            newVisitedCaves.add(neighbour);
            List<List<Cave>> newResultingPaths = new ArrayList<>();

            //Call get paths to get new resulting paths to add to the result list
            getPaths(network, neighbour, newPath, newVisitedCaves, newWildCardUsed, newResultingPaths);
            return newResultingPaths;
        }).forEach(resultingPaths::addAll);
    }

}