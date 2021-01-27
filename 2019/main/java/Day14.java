import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Day14 {

    @Data
    @AllArgsConstructor
    private static class ElementAmount {
        String element;
        long amount;
    }

    @Value
    private static class Reaction {
        List<ElementAmount> input;
        ElementAmount output;
    }

    private static Set<String> allElements = new HashSet<>();
    private static List<Reaction> reactions;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input14.csv");
        getReactions(lines);
        determineAmountOfOreNeededToProduceOneFuel();
        determineAmountOfFuelProduced();
    }

    private static void determineAmountOfFuelProduced() {
        final long goal = 1000000000000L;
        long previousAmountOfFuel = 1L;
        long amountOfFuel = 1L;
        long max = 10000000L;
        while (true) {
            Map<Reaction, Long> usedReactions = new HashMap<>();
            usedReactions.put(getReaction("FUEL"), amountOfFuel);
            long numberOfOreNeeded = getNumberOfOreNeeded(usedReactions);
            System.out.printf("Amount of fuel: %s, number of ore needed: %s%n", amountOfFuel, numberOfOreNeeded);
            System.out.println();
            if (amountOfFuel == previousAmountOfFuel + 1) {
                break;
            }
            if (numberOfOreNeeded < goal) {
                previousAmountOfFuel = amountOfFuel;
                amountOfFuel += LongMath.divide(max - amountOfFuel, 2, RoundingMode.CEILING);
            }
            else if (numberOfOreNeeded > goal) {
                max = amountOfFuel;
                amountOfFuel -= LongMath.divide(amountOfFuel - previousAmountOfFuel, 2, RoundingMode.CEILING);
            }
            else {
                break;
            }
        }
    }

    private static void determineAmountOfOreNeededToProduceOneFuel() {
        Map<Reaction, Long> usedReactions = new HashMap<>();
        usedReactions.put(getReaction("FUEL"), 1L);
        long numberOfOreNeeded = getNumberOfOreNeeded(usedReactions);
        System.out.println("Number of ore Needed: " + numberOfOreNeeded);
    }

    private static Map<Reaction, Long> reactionsResult;

    private static long getNumberOfOreNeeded(Map<Reaction, Long> usedReactions) {
        if (usedReactions.isEmpty()) {
            return 0;
        }
        Map<String, ElementAmount> elementMap = new HashMap<>();
        usedReactions.forEach((usedReaction, numberOfReactions) -> {
            usedReaction.input.forEach(input -> addToMap(elementMap, new ElementAmount(input.element, input.amount * numberOfReactions)));
            substractFromMap(elementMap, new ElementAmount(usedReaction.output.element, usedReaction.output.amount * numberOfReactions));
        });

        long numberOfOreNeeded = 0;
        Map<Reaction, Long> newUsedReactions = new HashMap<>();
        for (Map.Entry<String, ElementAmount> entry : elementMap.entrySet()) {
            String element = entry.getKey();
            long amountNeeded = entry.getValue().amount;
            if (amountNeeded <= 0) continue;
            if (element.equals("ORE")) {
                numberOfOreNeeded += amountNeeded;
            }
            else {
                Reaction reaction = getReaction(element);
                long numberOfReactionsNeeded = LongMath.divide(amountNeeded, reaction.output.amount, RoundingMode.CEILING);
                newUsedReactions.put(reaction, numberOfReactionsNeeded);
            }
        };
        if (!newUsedReactions.isEmpty()) {
            addToMap(newUsedReactions, usedReactions);
            return getNumberOfOreNeeded(newUsedReactions);
        }
        else {
            reactionsResult = usedReactions;
            return numberOfOreNeeded;
        }
    }

    private static void addToMap(Map<Reaction, Long> newUsedReactions, Map<Reaction, Long> usedReactions) {
        usedReactions.forEach((key, value) -> {
            if (!newUsedReactions.containsKey(key)) {
                newUsedReactions.put(key, value);
            } else {
                newUsedReactions.put(key, newUsedReactions.get(key) + value);
            }
        });
    }

    private static void addToMap(Map<String, ElementAmount> elementMap, ElementAmount addedElementAmount) {
        ElementAmount elementAmount = elementMap.getOrDefault(addedElementAmount.element, new ElementAmount(addedElementAmount.element, 0));
        elementAmount.amount += addedElementAmount.amount;
        elementMap.put(elementAmount.element, elementAmount);
    }

    private static void substractFromMap(Map<String, ElementAmount> elementMap, ElementAmount substractedElementAmount) {
        ElementAmount elementAmount = elementMap.getOrDefault(substractedElementAmount.element, new ElementAmount(substractedElementAmount.element, 0));
        elementAmount.amount -= substractedElementAmount.amount;
        elementMap.put(elementAmount.element, elementAmount);
    }

    private static Reaction getReaction(String output) {
        return reactions.stream().filter(r -> r.output.element.equals(output)).findFirst().orElseThrow();
    }

    private static void getReactions(List<String> lines) {
        reactions = lines.stream().map(line -> {
            String[] parts = line.split(" => ");
            String[] elementAmounts = parts[0].split(", ");
            String output = parts[1];
            List<ElementAmount> input = Arrays.stream(elementAmounts).map(Day14::fromString).collect(Collectors.toList());
            return new Reaction(input, fromString(output));
        }).collect(Collectors.toList());
    }

    private static ElementAmount fromString(String elementAmount) {
        String[] parts = elementAmount.split(" ");
        int amount = Integer.parseInt(parts[0]);
        String element = parts[1];
        allElements.add(element);
        return new ElementAmount(element, amount);
    }


}
