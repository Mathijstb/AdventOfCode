import fileUtils.FileReader;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Day6 {

    @Data
    private static class Planet {
        String name;
        String orbitedPlanet;
        List<String> transfers = new ArrayList<>();

        public Planet(String name) {
            this.name = name;
        }
    }

    private static Map<String, Planet> planets = new HashMap<>();
    private static Map<String, Long> orbitMap = new HashMap<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input6.csv");
        readPlanets(lines);
        //countNumberOfOrbits();
        findMinimalNumberOfTransfersFromYouToSan();
    }

    private static void findMinimalNumberOfTransfersFromYouToSan() {
        Planet orbitedByYou = planets.get(planets.get("YOU").orbitedPlanet);
        Planet orbitedBySan = planets.get(planets.get("SAN").orbitedPlanet);
        Set<String> visitedPlanets = new HashSet<>();
        visitedPlanets.add(orbitedByYou.name);
        int numberOfTransfers = 0;
        do {
            numberOfTransfers += 1;
            Set<String> oldVisitedPlanets = new HashSet<>(visitedPlanets);
            oldVisitedPlanets.forEach(name -> visitedPlanets.addAll(planets.get(name).transfers));
        } while (!visitedPlanets.contains(orbitedBySan.name));

        //260 too high
        System.out.println("Number of transfers: " + numberOfTransfers);
    }

    private static void countNumberOfOrbits() {
        List<Planet> planetsThatDoNotOrbit = planets.values().stream().filter(p -> p.orbitedPlanet == null).collect(Collectors.toList());
        planetsThatDoNotOrbit.forEach(Day6::determineNumberOfOrbits);
        long numberOfOrbits = orbitMap.values().stream().reduce(0L, Long::sum);
        System.out.println("Number of orbits: " + numberOfOrbits);
    }

    private static void determineNumberOfOrbits(Planet planet) {
        List<Planet> orbitingPlanets = planets.values().stream().filter(p -> p.orbitedPlanet != null && p.orbitedPlanet.equals(planet.name)).collect(Collectors.toList());
        long numberOfOrbits = 0;
        for (Planet orbitingPlanet: orbitingPlanets) {
            determineNumberOfOrbits(orbitingPlanet);
            numberOfOrbits += 1 + orbitMap.get(orbitingPlanet.name);
        }
        orbitMap.put(planet.name, numberOfOrbits);
    }


    private static void readPlanets(List<String> lines) {
        lines.forEach(line -> {
            String[] parts = line.split("\\)");
            Planet planet1 = getPlanet(parts[0]);
            Planet planet2 = getPlanet(parts[1]);
            planet2.orbitedPlanet = planet1.name;
            planet1.transfers.add(planet2.name);
            planet2.transfers.add(planet1.name);
        });
    }

    private static Planet getPlanet(String name) {
        if (!planets.containsKey(name)) planets.put(name, new Planet(name));
        return planets.get(name);
    }

}
