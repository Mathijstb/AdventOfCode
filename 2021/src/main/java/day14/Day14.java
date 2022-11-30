package day14;

import fileUtils.FileReader;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day14 {

    public static void execute() {
        List<String> input = FileReader.getFileReader().readFile("input14.csv");
        String molecule = input.get(0);
        List<Rule> rules =input.subList(2, input.size()).stream().map(s -> {
            String[] parts = s.split(" -> ");
            return new Rule(parts[0], parts[1]);
        }).collect(Collectors.toList());

        System.out.println("Starting molecule:");
        System.out.println("-------");
        System.out.println(molecule);
        System.out.println();

        executeInsertions(molecule, rules, 10);

        countUniquePairs(molecule, rules, 40);
    }

    private static void executeInsertions(String molecule, List<Rule> rules, int numberOfSteps) {
        for (int i = 0; i < numberOfSteps; i++) {
            molecule = executeInsertions(molecule, rules);
            System.out.println("Step " + (i+1) + ":");
            System.out.println("-------");
            System.out.println(molecule);
            System.out.println();
        }

        printQuantities(molecule);
    }

    private static String executeInsertions(String molecule, List<Rule> rules) {
        Map<String, String> tokens = new HashMap<>();
        rules.forEach(rule -> tokens.put(rule.getPair(), rule.getInsertElement()));

        String patternString = "(" + StringUtils.join(tokens.keySet(), "|") + ")";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(molecule);

        StringBuilder sb = new StringBuilder();
        int start = 0;
        int lastEndIndex = -1;
        while(matcher.find(start)) {
            sb.append(molecule, start, matcher.start(1));
            if (lastEndIndex < matcher.start(1)) {
                sb.append(matcher.group(1).charAt(0));
            }
            sb.append(tokens.get(matcher.group(1))).append(matcher.group(1).charAt(1));
            start = matcher.end(1)-1;
            lastEndIndex = matcher.end(1);
        }
        return sb.toString();
    }

    private static void countUniquePairs(String molecule, List<Rule> rules, int numberOfSteps) {
        Map<String, Long> numberOfPairs = new HashMap<>();
        for (Rule rule : rules) {
            Matcher matcher = Pattern.compile("(?=" + rule.getPair() + ")").matcher(molecule);
            long count = 0;
            while (matcher.find()) {
                count++;
            }
            numberOfPairs.put(rule.getPair(), count);
        }

        Map<String, String> replacementMap = new HashMap<>();
        rules.forEach(rule -> replacementMap.put(rule.getPair(), rule.getInsertElement()));

        String startingPair = molecule.substring(0, 2);
        String endingPair = molecule.substring(molecule.length() - 2);
        for (int i = 0; i < numberOfSteps; i++) {
            startingPair = startingPair.charAt(0) + replacementMap.get(startingPair);
            endingPair = replacementMap.get(endingPair) + endingPair.charAt(1);
        }

        for (int i = 0; i < numberOfSteps; i++) {
            Map<String, Long> newNumberOfPairs = new HashMap<>(numberOfPairs);
            numberOfPairs.forEach((element, amount) -> {
                if (amount > 0) {
                    String insertElement = replacementMap.get(element);
                    String pair1 = element.charAt(0) + insertElement;
                    String pair2 = insertElement + element.charAt(1);
                    newNumberOfPairs.put(element, newNumberOfPairs.get(element) - amount);
                    newNumberOfPairs.put(pair1, newNumberOfPairs.get(pair1) + amount);
                    newNumberOfPairs.put(pair2, newNumberOfPairs.get(pair2) + amount);
                }
            });
            numberOfPairs = newNumberOfPairs;
        }
        Map<Character, Long> characterMap = new HashMap<>();
        numberOfPairs.forEach((element, amount) -> {
            Character char1 = element.charAt(0);
            Character char2 = element.charAt(1);
            characterMap.put(char1, characterMap.getOrDefault(char1, 0L) + amount);
            characterMap.put(char2, characterMap.getOrDefault(char2, 0L) + amount);
        });
        Character startingCharacter = startingPair.charAt(0);
        Character endingCharacter = endingPair.charAt(1);
        characterMap.put(startingCharacter, characterMap.get(startingCharacter) + 1);
        characterMap.put(endingCharacter, characterMap.get(endingCharacter) + 1);
        characterMap.forEach((character, amount) -> characterMap.put(character, amount/2));

        Character leastOccurring = characterMap.entrySet().stream()
                .min(Comparator.comparingLong(Map.Entry::getValue)).stream().map(Map.Entry::getKey).findFirst().orElseThrow();
        Character mostOccurring = characterMap.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue)).stream().map(Map.Entry::getKey).findFirst().orElseThrow();
        long leastAmount = characterMap.get(leastOccurring);
        long mostAmount = characterMap.get(mostOccurring);
        long difference = mostAmount - leastAmount;
        System.out.println("Least occuring: " + leastOccurring + ", amount: " + leastAmount);
        System.out.println("Most occuring: " + mostOccurring + ", amount: " + mostAmount);
        System.out.println("Diference: " + difference);
    }

    private static void printQuantities(String molecule) {
        Map<Character, Long> characterMap = new HashMap<>();
        molecule.chars().mapToObj(c -> (char) c).forEach(c -> {
            long numberOfCharacters = characterMap.getOrDefault(c, 0L);
            characterMap.put(c, numberOfCharacters + 1);
        });
        Character leastOccurring = characterMap.entrySet().stream()
                .min(Comparator.comparingLong(Map.Entry::getValue)).stream().map(Map.Entry::getKey).findFirst().orElseThrow();
        Character mostOccurring = characterMap.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue)).stream().map(Map.Entry::getKey).findFirst().orElseThrow();
        long leastAmount = characterMap.get(leastOccurring);
        long mostAmount = characterMap.get(mostOccurring);
        long difference = mostAmount - leastAmount;
        System.out.println("Least occuring: " + leastOccurring + ", amount: " + leastAmount);
        System.out.println("Most occuring: " + mostOccurring + ", amount: " + mostAmount);
        System.out.println("Diference: " + difference);
    }
}