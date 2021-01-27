import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Day17 {

    @Value
    private static class Container {
        int number;
        int capacity;
    }

    private static final List<Container> containers = new ArrayList<>();
    private static final int liters = 150;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input17.csv");
        initializeContainers(lines);
        determineNumberOfCombinations();
    }

    private static final Set<Set<Container>> combinations = new HashSet<>();

    //17291952 too high
    private static void determineNumberOfCombinations() {
        getNextContainer(new HashSet<>());
        System.out.println("number of combinations: " + combinations.size());
        int minNumberOfContainers = combinations.stream().min(Comparator.comparing(Set::size)).orElseThrow().size();
        System.out.println("Minimum number of containers: " + minNumberOfContainers);
        List<Set<Container>> minCombinations = combinations.stream().filter(combination -> combination.size() == minNumberOfContainers).collect(Collectors.toList());
        System.out.println("Number of combinations with minimum number: " + minCombinations.size());
    }

    private static void getNextContainer(Set<Container> filledContainers) {
        int remainingLiters = liters - filledContainers.stream().map(Container::getCapacity).reduce(0, Integer::sum);
        if (remainingLiters == 0) {
            combinations.add(filledContainers);
        }
        else {
            List<Container> remainingContainers = new ArrayList<>(containers);
            remainingContainers.removeAll(filledContainers);
            for (Container remainingContainer : remainingContainers) {
                if (remainingContainer.capacity <= remainingLiters) {
                    Set<Container> newFilledContainers = new HashSet<>(filledContainers);
                    newFilledContainers.add(remainingContainer);
                    getNextContainer(newFilledContainers);
                }
            }
        }
    }

    private static void initializeContainers(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            containers.add(new Container(i, Integer.parseInt(lines.get(i))));
        }
    }

}
