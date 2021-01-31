import fileUtils.FileReader;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day15 {

    @Value
    private static class Cookie {
        String name;
        int capacity;
        int durability;
        int flavor;
        int texture;
        int calories;
    }

    private static List<Cookie> cookies = new ArrayList<>();
    private static final int numberOfIngredients = 100;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input15.csv");
        setupCookies(lines);
        determineOptimalRecipe();
    }

    private static void setupCookies(List<String> lines) {
        lines.forEach(line -> {
            String[] nameAndRest = line.split(": capacity ");
            String[] capacityAndRest = nameAndRest[1].split(", durability ");
            String[] durabilityAndRest = capacityAndRest[1].split(", flavor ");
            String[] flavorAndRest = durabilityAndRest[1].split(", texture ");
            String[] textureAndRest = flavorAndRest[1].split(", calories ");
            cookies.add(new Cookie(nameAndRest[0], Integer.parseInt(capacityAndRest[0]), Integer.parseInt(durabilityAndRest[0]),
                    Integer.parseInt(flavorAndRest[0]), Integer.parseInt(textureAndRest[0]), Integer.parseInt(textureAndRest[1])));
        });
    }

    private static List<Map<Cookie, Integer>> cookieMaps = new ArrayList<>();

    private static void determineOptimalRecipe() {
        getNextIngredients(new HashMap<>(), new ArrayList<>(cookies));
        long maxScore = 0;
        Map<Cookie, Integer> maxCookieMap = cookieMaps.get(0);
        for (Map<Cookie, Integer> cookieMap: cookieMaps) {
            long score = getScore(cookieMap);
            if (score > maxScore) {
                maxScore = score;
                maxCookieMap = cookieMap;
            }
        }
        System.out.println("Max score: " + maxScore);
        System.out.println("Recipe: " + maxCookieMap);
    }

    private static long getScore(Map<Cookie, Integer> cookieMap) {
        long capacity = cookieMap.entrySet().stream().map(entry -> entry.getKey().capacity * entry.getValue()).reduce(0, Integer::sum);
        long durability = cookieMap.entrySet().stream().map(entry -> entry.getKey().durability * entry.getValue()).reduce(0, Integer::sum);
        long flavor = cookieMap.entrySet().stream().map(entry -> entry.getKey().flavor * entry.getValue()).reduce(0, Integer::sum);
        long texture = cookieMap.entrySet().stream().map(entry -> entry.getKey().texture * entry.getValue()).reduce(0, Integer::sum);
        long calories = cookieMap.entrySet().stream().map(entry -> entry.getKey().calories * entry.getValue()).reduce(0, Integer::sum);
        return calories == 500 ? Math.max(0, capacity) * Math.max(0, durability) * Math.max(0, flavor) * Math.max(0, texture) : 0;
    }

    private static void getNextIngredients(Map<Cookie, Integer> cookieMap, List<Cookie> remainingCookies) {
        Cookie cookie = remainingCookies.get(0);
        int remainingIngredients = numberOfIngredients - cookieMap.values().stream().reduce(0, Integer::sum);
        if (remainingCookies.size() == 1) {
            cookieMap.put(cookie, remainingIngredients);
            cookieMaps.add(cookieMap);
        } else {
            for (int i = 0; i <= remainingIngredients; i++) {
                cookieMap.put(cookie, i);
                List<Cookie> newRemainingCookies = new ArrayList<>(remainingCookies);
                newRemainingCookies.remove(cookie);
                getNextIngredients(new HashMap<>(cookieMap), newRemainingCookies);
            }
        }
    }
}
