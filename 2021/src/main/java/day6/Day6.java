package day6;

import fileUtils.FileReader;

import java.util.*;
import java.util.stream.Collectors;

public class Day6 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input6.csv");
        assert (input.size() == 1);
        List<Fish> fishes = Arrays.stream(input.get(0).split(",")).map(s -> new Fish(Integer.parseInt(s))).collect(Collectors.toList());

        List<Fish> fishesCopy = new ArrayList<>();
        fishes.forEach(fish -> fishesCopy.add(new Fish(fish.creationTimer)));
        List<Fish> updatedFishes = cycleDays(80, fishesCopy);
        System.out.println("Number of fishes after cycling: " + updatedFishes.size());

        long numberCalculated = calculateNumberOfFishesAfterDays(256, fishes);
        System.out.println("Number of fishes calculated: " + numberCalculated);
    }

    private static List<Fish> cycleDays(int numberOfDays, List<Fish> fishes) {
        for (int i = 0; i < numberOfDays; i++) {
            System.out.println("Day: " + (i+1) + ", Number of fishes: " + fishes.size());
            List<Fish> updatedFishes = new ArrayList<>(fishes);
            fishes.forEach(fish -> {
                Optional<Fish> newFish = fish.cycle();
                newFish.ifPresent(updatedFishes::add);
            });
            fishes = updatedFishes;
        }
        return fishes;
    }

    private static long calculateNumberOfFishesAfterDays(int numberOfDays, List<Fish> fishes) {
        Map<Integer, Map<Integer, Long>> daysToNumberOfFishesPerTimerMap = new HashMap<>();
        for (int day = 0; day <= numberOfDays; day++) {
            Map<Integer, Long> timerToNumberOfFishesMap = new HashMap<>();
            for (int timer = 0; timer <= 8; timer++) {
                if (timer >= day) {
                    timerToNumberOfFishesMap.put(timer, 1L);
                }
                else {
                    long numberOfFishes = daysToNumberOfFishesPerTimerMap.get(day - (timer + 1)).get(6) + daysToNumberOfFishesPerTimerMap.get(day - (timer + 1)).get(8);
                    timerToNumberOfFishesMap.put(timer, numberOfFishes);
                }
            }
            daysToNumberOfFishesPerTimerMap.put(day, timerToNumberOfFishesMap);
        }

        return fishes.stream().map(fish -> daysToNumberOfFishesPerTimerMap.get(numberOfDays).get(fish.creationTimer)).mapToLong(v -> v).sum();
    }
}
