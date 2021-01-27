import com.google.common.collect.Collections2;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Day13 {

    @Value
    private static class Person {
        String name;
        Map<String, Integer> happinessMap = new HashMap<>();

        public int getNeighbourScore(String neighbour) {
            return happinessMap.getOrDefault(neighbour, 0);
        }

        @Override
        public String toString() {
            return name + happinessMap;
        }
    }

    @Value
    private static class Seating {
        List<Person> persons;

        private Person getLeftNeighbour(int seatIndex) {
            int leftSeatIndex = Math.floorMod(seatIndex - 1, persons.size());
            return persons.get(leftSeatIndex);
        }

        private Person getRightNeighbour(int seatIndex) {
            int rightSeatIndex = Math.floorMod(seatIndex + 1, persons.size());
            return persons.get(rightSeatIndex);
        }

        public int getScore() {
            int score = 0;
            for (int i = 0; i < persons.size(); i++) {
                Person person = personsMap.get(persons.get(i).name);
                Person leftNeighbour = getLeftNeighbour(i);
                Person rightNeighbour = getRightNeighbour(i);
                score += person.getNeighbourScore(leftNeighbour.name) + person.getNeighbourScore(rightNeighbour.name);
            }
            return score;
        }

        @Override
        public String toString() {
            return persons.toString();
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input13.csv");
        intializeHappinessMap(lines);
        determineOptimalSeating();
        personsMap.put("Me", new Person("Me"));
        determineOptimalSeating();
    }

    private static void determineOptimalSeating() {
        int numberOfSeats = personsMap.size();
        List<Seating> seatings = Collections2.permutations(personsMap.values()).stream()
                .map(Seating::new).collect(Collectors.toList());
        int maxScore = 0;
        Seating maxSeating = null;
        for (Seating seating : seatings) {
            int score = seating.getScore();
            if (score > maxScore) {
                maxScore = score;
                maxSeating = seating;
            }
        }
        System.out.println("Max score: " + maxScore);
        assert maxSeating != null;
        System.out.println("Seating: " + maxSeating.toString());
    }

    private static final Map<String, Person> personsMap = new HashMap<>();

    public static void intializeHappinessMap(List<String> lines) {
        lines.forEach(line -> {
            String[] personAndRule = line.split(" would ");
            String name = personAndRule[0];
            String rule = personAndRule[1];
            String[] amountAndOtherPerson = rule.split(" happiness units by sitting next to ");
            String amount = amountAndOtherPerson[0];
            String otherName = amountAndOtherPerson[1].substring(0, amountAndOtherPerson[1].length() - 1);
            Person person = getPerson(name);
            Person otherPerson = getPerson(otherName);
            int value = amount.contains("gain ")? Integer.parseInt(amount.substring(5)) : -Integer.parseInt(amount.substring(5));
            person.happinessMap.put(otherPerson.name, value);
        });
    }

    private static Person getPerson(String name) {
        if (personsMap.containsKey(name)) {
            return personsMap.get(name);
        } else {
            Person person = new Person(name);
            personsMap.put(name, person);
            return person;
        }
    }
}
