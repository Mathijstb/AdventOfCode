import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Day6 {

    @Data
    private static class Group {
        List<String> answers = new ArrayList<>();
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input6.csv");

        List<Group> groups = new ArrayList<>();
        Group group = new Group();
        for (String line : lines) {
            if (line.isEmpty() || line.isBlank()) {
                groups.add(group);
                group = new Group();
            }
            else {
                group.getAnswers().add(line);
            }
        }
        groups.add(group);

        int numberOfAnswersCollection = groups.stream().map(Day6::countNumberOfAnswers).reduce(0, Integer::sum);
        System.out.println("numberOfAnswersCollection: " + numberOfAnswersCollection);

        int numberOfAnswersIntersection = groups.stream().map(Day6::countNumberOfAnswers2).reduce(0, Integer::sum);
        System.out.println("numberOfAnswersIntersection: " + numberOfAnswersIntersection);
    }

    private static int countNumberOfAnswers(Group group) {
        List<String> answers = group.getAnswers();
        Set<Character> uniqueAnswers = new HashSet<>();
        for (String answer: answers) {
            for (int i = 0; i < answer.length(); i++) {
                uniqueAnswers.add(answer.charAt(i));
            }
        }
        System.out.println("number of unique answers: " + uniqueAnswers.size());
        return uniqueAnswers.size();
    }

    private static int countNumberOfAnswers2(Group group) {
        List<String> answers = group.getAnswers();

        List<Character> intersection = new ArrayList<>();
        for(char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
            intersection.add(c);
        }

        for (String answer: answers) {
            List<Character> characters = new ArrayList<>();
            for(char c : answer.toCharArray()) {
                characters.add(c);
            }
            intersection.retainAll(characters);
        }
        System.out.println("number of common answers: " + intersection.size());
        return intersection.size();
    }

}
