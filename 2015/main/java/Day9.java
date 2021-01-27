import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Day9 {

    @Value
    private static class City {
        String name;
        Map<String, Integer> distances = new HashMap<>();
    }

    private static Map<String, City> cityMap = new HashMap<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input9.csv");
        determineCities(lines);
        determineShortestRoute();
        System.out.println();
        determineLongestRoute();
    }

    private static void determineShortestRoute() {
        int minDistance = 9999;
        for (City city: cityMap.values()) {
            List<City> remainingCities = new ArrayList<>(cityMap.values());
            remainingCities.remove(city);
            int distance = determineShortestSubRoute(city, remainingCities);
            System.out.printf("Minimum distance starting at city %s is: %s%n", city.name, distance);
            minDistance = Math.min(distance, minDistance);
        }
        System.out.println("The minimum distance is: " + minDistance);
    }

    private static int determineShortestSubRoute(City cityFrom, List<City> cities) {
        if (cities.isEmpty()) return 0;
        int minDistance = 9999;
        for (City city: cities) {
            if (!cityFrom.distances.containsKey(city.name)) continue;
            List<City> remainingCities = new ArrayList<>(cities);
            remainingCities.remove(city);
            int distance = cityFrom.distances.get(city.name) + determineShortestSubRoute(city, remainingCities);
            minDistance = Math.min(distance, minDistance);
        }
        return minDistance;
    }

    private static void determineLongestRoute() {
        int maxDistance = -9999;
        for (City city: cityMap.values()) {
            List<City> remainingCities = new ArrayList<>(cityMap.values());
            remainingCities.remove(city);
            int distance = determineLongestSubRoute(city, remainingCities);
            System.out.printf("Maximum distance starting at city %s is: %s%n", city.name, distance);
            maxDistance = Math.max(distance, maxDistance);
        }
        System.out.println("The maximum distance is: " + maxDistance);
    }

    private static int determineLongestSubRoute(City cityFrom, List<City> cities) {
        if (cities.isEmpty()) return 0;
        int maxDistance = -9999;
        for (City city: cities) {
            if (!cityFrom.distances.containsKey(city.name)) continue;
            List<City> remainingCities = new ArrayList<>(cities);
            remainingCities.remove(city);
            int distance = cityFrom.distances.get(city.name) + determineLongestSubRoute(city, remainingCities);
            maxDistance = Math.max(distance, maxDistance);
        }
        return maxDistance;
    }

    private static void determineCities(List<String> lines) {
        lines.forEach(line -> {
            String[] routeAndDistance = line.split(" = ");
            String route = routeAndDistance[0];
            int distance = Integer.parseInt(routeAndDistance[1]);
            String[] citieNames = route.split(" to ");
            String cityFromName = citieNames[0];
            String cityToName = citieNames[1];
            City cityFrom = cityMap.containsKey(cityFromName) ? cityMap.get(cityFromName) : createNewCity(cityFromName);
            City cityTo = cityMap.containsKey(cityToName) ? cityMap.get(cityToName) : createNewCity(cityToName);
            cityFrom.distances.put(cityTo.name, distance);
            cityTo.distances.put(cityFrom.name, distance);
        });
    }

    private static City createNewCity(String name) {
        City city = new City(name);
        cityMap.put(name, city);
        return city;
    }
}
