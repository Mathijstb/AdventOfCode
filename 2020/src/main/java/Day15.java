import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Day15 {

    @Data
    @AllArgsConstructor
    private static class SpokenNumber {
        int number;
        List<Integer> turns = new ArrayList<>();

        public SpokenNumber(int number, int firstTurn) {
            this.number = number;
            this.turns.add(firstTurn);
        }
    }

    private static final Integer lastTurn = 30000000;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input15.csv");
        List<Integer> initialNumbers = Arrays.stream(lines.get(0).split("[,]")).map(Integer::parseInt).collect(Collectors.toList());
        Map<Integer, SpokenNumber> spokenNumberMap = initializeMap(initialNumbers);
        executeGame(spokenNumberMap);
    }

    private static Map<Integer, SpokenNumber> initializeMap(List<Integer> initialNumbers) {
        Map<Integer, SpokenNumber> spokenNumberMap = new HashMap<>();
        for (int i = 0; i < initialNumbers.size(); i++) {
            int initialNumber = initialNumbers.get(i);
            spokenNumberMap.put(initialNumber, new SpokenNumber(initialNumber, i + 1));
        }
        return spokenNumberMap;
    }

    private static void executeGame(Map<Integer, SpokenNumber> spokenNumberMap) {
        SpokenNumber spokenNumber = Collections.max(spokenNumberMap.values(), Comparator.comparing(v -> v.turns.get(0)));

        int turnNumber = spokenNumberMap.size() + 1;
        while (true) {
            if (spokenNumber.turns.size() == 1) {
                spokenNumber = spokenNumberMap.get(0);
                spokenNumber.turns.add(turnNumber);
            }
            else {
                List<Integer> lastTurns = spokenNumber.turns;
                int newNumber = lastTurns.get(lastTurns.size() - 1) - lastTurns.get(lastTurns.size() - 2);
                if (spokenNumberMap.containsKey(newNumber)) {
                    spokenNumber = spokenNumberMap.get(newNumber);
                    spokenNumber.turns.add(turnNumber);
                }
                else {
                    spokenNumber = new SpokenNumber(newNumber, turnNumber);
                    spokenNumberMap.put(newNumber, spokenNumber);
                }
            }
            if (turnNumber == lastTurn) {
                System.out.printf("The %sth spoken number is: %s%n", lastTurn, spokenNumber);
                break;
            }
            turnNumber += 1;
        }
    }
}
