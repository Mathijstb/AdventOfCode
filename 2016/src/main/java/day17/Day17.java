package day17;

import cryptUtils.Crypto;
import fileUtils.FileReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Day17 {

    private static final Point START = new Point(0, 0);
    private static final Point GOAL = new Point(3, 3);

    public static void execute() {
        //part a
        String passcode = FileReader.getFileReader().readFile("input17.csv").stream().findFirst().orElseThrow();
        findPath(() -> findShortest(passcode));
        findPath(() -> findLongest(passcode));
    }

    private static void findPath(Supplier<Optional<State>> algorithm) {

        Optional<State> result = algorithm.get();

        if (result.isPresent()) {
            System.out.println("Path found! ");
            System.out.println("Path: " + result.get().getPath());
            System.out.println("Length: " + result.get().getPath().length());
        }
        else {
            System.out.println("No path possible!");
        }
    }

    private static Optional<State> findShortest(String passcode) {
        Set<State> states = Set.of(new State(START, new ArrayList<>()));
        while (true) {
            var newStates = findNewStates(passcode, states);

            if (newStates.isEmpty()) return Optional.empty();
            Optional<State> goalState = newStates.stream().filter(state -> state.getCurrentPoint().equals(GOAL)).findFirst();
            if (goalState.isPresent()) {
                return goalState;
            }
            states = newStates;
        }
    }

    private static Optional<State> findLongest(String passcode) {
        Set<State> states = Set.of(new State(START, new ArrayList<>()));
        Set<State> goalStates = new HashSet<>();
        while (true) {
            var newStates = findNewStates(passcode, states);
            var newGoalStates = newStates.stream().filter(state -> state.getCurrentPoint().equals(GOAL)).collect(Collectors.toSet());
            if (!newGoalStates.isEmpty()) {
                goalStates = newGoalStates;
                newStates.removeAll(newGoalStates);
            }
            if (newStates.isEmpty()) {
                return goalStates.stream().findFirst();
            }
            states = newStates;
        }
    }

    private static Set<State> findNewStates(String passcode, Set<State> states) {
        Set<State> newStates = new HashSet<>();
        for(State state :states) {
            var openDoors = getOpenDoors(passcode, state);
            newStates.addAll(openDoors.stream().map(door -> {
                var newState = state.copy();
                newState.addDoor(door);
                return newState;
            }).collect(Collectors.toSet()));
        }
        return newStates;
    }



    private static Set<Door> getOpenDoors(String passcode, State state) {
        Set<Door> result = new HashSet<>();
        var hash = Crypto.hashMD5(passcode + state.getPath());
        if (hash.charAt(0) >= 'b') result.add(Door.UP);
        if (hash.charAt(1) >= 'b') result.add(Door.DOWN);
        if (hash.charAt(2) >= 'b') result.add(Door.LEFT);
        if (hash.charAt(3) >= 'b') result.add(Door.RIGHT);
        return result.stream().filter(door -> state.getAvailableDoors().contains(door)).collect(Collectors.toSet());
    }
}