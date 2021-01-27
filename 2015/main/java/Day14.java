import lombok.Data;
import lombok.Value;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day14 {

    @Data
    private static class State {
        int distance = 0;
        int points = 0;
    }

    @Value
    private static class Reindeer {
        String name;
        int speed;
        int runPeriod;
        int pausePeriod;
    }

    private static final Map<Reindeer, State> reindeerMap = new HashMap<>();

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input14.csv");
        setupReindeers(lines);
        executeGame();
        findWinningReindeerDistance();
        findWinningReindeerPoints();
    }

    private static void findWinningReindeerDistance() {
        Map.Entry<Reindeer, State> maxEntry = reindeerMap.entrySet().stream().max(Comparator.comparing(entry -> entry.getValue().distance)).orElseThrow();
        Reindeer winningReindeer = maxEntry.getKey();
        int winningDistance = maxEntry.getValue().distance;
        System.out.println("Winning reindeer: " + winningReindeer);
        System.out.println("Winning distance: " + winningDistance);
        System.out.println("");
        System.out.println("Other reindeers: ");
        reindeerMap.entrySet().stream().filter(entry -> entry.getKey() != winningReindeer).forEach(entry -> {
            System.out.printf("Reindeer: %s, distance: %s%n", entry.getKey(), entry.getValue());
        });
    }

    private static void findWinningReindeerPoints() {
        Map.Entry<Reindeer, State> maxEntry = reindeerMap.entrySet().stream().max(Comparator.comparing(entry -> entry.getValue().points)).orElseThrow();
        Reindeer winningReindeer = maxEntry.getKey();
        int winningPoints = maxEntry.getValue().points;
        System.out.println();
        System.out.println("Winning reindeer: " + winningReindeer);
        System.out.println("Winning points: " + winningPoints);
        System.out.println("");
        System.out.println("Other reindeers: ");
        reindeerMap.entrySet().stream().filter(entry -> entry.getKey() != winningReindeer).forEach(entry -> {
            System.out.printf("Reindeer: %s, distance: %s%n", entry.getKey(), entry.getValue());
        });
    }

    private static void executeGame() {
        int matchTime = 2503;
        for (int i = 0; i < matchTime; i++) {
            for (Reindeer reindeer: reindeerMap.keySet()) {
                int runPeriod = reindeer.runPeriod;
                int pausePeriod = reindeer.pausePeriod;
                if (Math.floorMod(i, runPeriod + pausePeriod) < runPeriod) {
                    State state = reindeerMap.get(reindeer);
                    state.distance += reindeer.speed;
                }
            }
            int maxDistance = reindeerMap.values().stream().max(Comparator.comparing(state -> state.distance)).orElseThrow().distance;
            List<Reindeer> leadingReindeers = reindeerMap.entrySet().stream().filter(entry -> entry.getValue().distance == maxDistance)
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            leadingReindeers.forEach(reindeer -> {
                State state = reindeerMap.get(reindeer);
                state.points += 1;
            });
        }
    }

    private static void setupReindeers(List<String> lines) {
        lines.forEach(line -> {
            String[] nameAndRest = line.split(" can fly ");
            String[] speedAndRest = nameAndRest[1].split(" km/s for ");
            String[] runPeriodAndRest = speedAndRest[1].split(" seconds, but then must rest for ");
            String[] pausePeriodAndRest = runPeriodAndRest[1].split(" seconds.");
            reindeerMap.put(new Reindeer(nameAndRest[0], Integer.parseInt(speedAndRest[0]),
                                Integer.parseInt(runPeriodAndRest[0]), Integer.parseInt(pausePeriodAndRest[0])), new State());
        });
    }
}
